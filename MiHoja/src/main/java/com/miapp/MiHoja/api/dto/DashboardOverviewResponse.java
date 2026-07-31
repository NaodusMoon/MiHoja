package com.miapp.MiHoja.api.dto;

import java.util.List;

public record DashboardOverviewResponse(
        List<DashboardMetricResponse> metrics,
        List<DashboardPersonCardResponse> recentPeople,
        List<String> highlights
) {
}
