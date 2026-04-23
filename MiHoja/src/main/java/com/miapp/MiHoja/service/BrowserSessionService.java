package com.miapp.MiHoja.service;

import com.miapp.MiHoja.config.BrowserSessionProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BrowserSessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserSessionService.class);

    private final BrowserSessionProperties properties;
    private final ApplicationContext applicationContext;
    private final Map<String, Instant> activeClients = new ConcurrentHashMap<>();
    private final AtomicBoolean shutdownStarted = new AtomicBoolean(false);
    private volatile Instant startedAt;
    private volatile Instant lastActivityAt;

    public BrowserSessionService(BrowserSessionProperties properties, ApplicationContext applicationContext) {
        this.properties = properties;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    void init() {
        Instant now = Instant.now();
        this.startedAt = now;
        this.lastActivityAt = now;
    }

    public void ping(String clientId, HttpServletRequest request) {
        if (!properties.isAutoShutdownEnabled() || !isRequestAllowed(request) || clientId == null || clientId.isBlank()) {
            return;
        }
        Instant now = Instant.now();
        activeClients.put(clientId, now);
        lastActivityAt = now;
    }

    public void end(String clientId, HttpServletRequest request) {
        if (!properties.isAutoShutdownEnabled() || !isRequestAllowed(request)) {
            return;
        }
        if (clientId != null && !clientId.isBlank()) {
            activeClients.remove(clientId);
        }
        lastActivityAt = Instant.now();
    }

    @Scheduled(fixedDelayString = "${app.browser-session.monitor-interval:5s}")
    void monitor() {
        if (!properties.isAutoShutdownEnabled() || shutdownStarted.get()) {
            return;
        }

        Instant now = Instant.now();
        if (now.isBefore(startedAt.plus(properties.getStartupGrace()))) {
            return;
        }

        Instant staleBefore = now.minus(properties.getInactivityTimeout());
        activeClients.entrySet().removeIf(entry -> entry.getValue().isBefore(staleBefore));

        if (!activeClients.isEmpty()) {
            return;
        }

        if (lastActivityAt.isAfter(staleBefore)) {
            return;
        }

        if (shutdownStarted.compareAndSet(false, true)) {
            LOGGER.info("No browser clients detected. Shutting down application.");
            Thread shutdownThread = new Thread(this::shutdownNow, "browser-session-shutdown");
            shutdownThread.setDaemon(false);
            shutdownThread.start();
        }
    }

    private boolean isRequestAllowed(HttpServletRequest request) {
        if (!properties.isLocalOnly()) {
            return true;
        }
        try {
            InetAddress address = InetAddress.getByName(request.getRemoteAddr());
            return address.isLoopbackAddress() || address.isAnyLocalAddress();
        } catch (Exception ignored) {
            return false;
        }
    }

    private void shutdownNow() {
        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(exitCode);
    }
}
