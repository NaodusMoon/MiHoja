package com.miapp.MiHoja.api.dto;

public record DashboardMetricResponse(
        String id,
        String label,
        String value,
        String tone
) {
}
