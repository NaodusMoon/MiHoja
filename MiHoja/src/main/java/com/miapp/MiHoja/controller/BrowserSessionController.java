package com.miapp.MiHoja.controller;

import com.miapp.MiHoja.service.BrowserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/browser-session")
public class BrowserSessionController {

    private final BrowserSessionService browserSessionService;

    public BrowserSessionController(BrowserSessionService browserSessionService) {
        this.browserSessionService = browserSessionService;
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping(@RequestBody(required = false) Map<String, String> body, HttpServletRequest request) {
        browserSessionService.ping(readClientId(body), request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/end")
    public ResponseEntity<Void> end(@RequestBody(required = false) Map<String, String> body, HttpServletRequest request) {
        browserSessionService.end(readClientId(body), request);
        return ResponseEntity.noContent().build();
    }

    private String readClientId(Map<String, String> body) {
        return body != null ? body.get("clientId") : null;
    }
}
