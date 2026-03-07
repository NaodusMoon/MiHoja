package com.miapp.MiHoja.service.support;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueryStringService {

    public String buildFromMap(Map<String, String> params, String... keysToExclude) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        return params.entrySet().stream()
                .filter(entry -> !shouldExclude(entry.getKey(), keysToExclude))
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    public String buildFromObjects(Map<String, ?> params, String... keysToExclude) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        return params.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> !shouldExclude(entry.getKey(), keysToExclude))
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue().toString()))
                .collect(Collectors.joining("&"));
    }

    public String sanitizeReturnQuery(String returnQuery) {
        if (returnQuery == null || returnQuery.isBlank()) {
            return "";
        }

        return Arrays.stream(returnQuery.split("&"))
                .map(String::trim)
                .filter(part -> !part.isBlank())
                .map(part -> part.startsWith("?") ? part.substring(1) : part)
                .filter(part -> !part.toLowerCase(Locale.ROOT).startsWith("edit="))
                .collect(Collectors.joining("&"));
    }

    public Map<String, Object> newOrderedMap() {
        return new LinkedHashMap<>();
    }

    private boolean shouldExclude(String key, String... keysToExclude) {
        if (keysToExclude == null) {
            return false;
        }
        return Arrays.stream(keysToExclude).anyMatch(excluded -> excluded.equalsIgnoreCase(key));
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
