package com.miapp.MiHoja.api.dto;

import java.util.List;

public record DashboardActionResponse(
        String message,
        int deletedCount,
        List<Long> failedIds
) {
}
