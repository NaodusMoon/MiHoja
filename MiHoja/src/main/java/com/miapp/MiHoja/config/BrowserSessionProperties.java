package com.miapp.MiHoja.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.browser-session")
public class BrowserSessionProperties {

    private boolean autoShutdownEnabled = true;
    private boolean localOnly = true;
    private Duration inactivityTimeout = Duration.ofSeconds(45);
    private Duration startupGrace = Duration.ofSeconds(20);
    private Duration monitorInterval = Duration.ofSeconds(5);

    public boolean isAutoShutdownEnabled() {
        return autoShutdownEnabled;
    }

    public void setAutoShutdownEnabled(boolean autoShutdownEnabled) {
        this.autoShutdownEnabled = autoShutdownEnabled;
    }

    public boolean isLocalOnly() {
        return localOnly;
    }

    public void setLocalOnly(boolean localOnly) {
        this.localOnly = localOnly;
    }

    public Duration getInactivityTimeout() {
        return inactivityTimeout;
    }

    public void setInactivityTimeout(Duration inactivityTimeout) {
        this.inactivityTimeout = inactivityTimeout;
    }

    public Duration getStartupGrace() {
        return startupGrace;
    }

    public void setStartupGrace(Duration startupGrace) {
        this.startupGrace = startupGrace;
    }

    public Duration getMonitorInterval() {
        return monitorInterval;
    }

    public void setMonitorInterval(Duration monitorInterval) {
        this.monitorInterval = monitorInterval;
    }
}
