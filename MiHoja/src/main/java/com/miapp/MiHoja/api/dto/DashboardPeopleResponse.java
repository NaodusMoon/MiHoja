package com.miapp.MiHoja.api.dto;

import java.util.List;

public record DashboardPeopleResponse(
        List<DashboardPersonCardResponse> people,
        List<DashboardMetricResponse> metrics,
        DashboardFilterOptionsResponse filterOptions,
        long total,
        int page,
        int size,
        int totalPages,
        long duplicateCount,
        int activeFilterCount,
        String query
) {
}
