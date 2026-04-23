package com.miapp.MiHoja.api.dto;

import java.util.List;

public record DashboardDeleteRequest(
        List<Long> ids
) {
}
