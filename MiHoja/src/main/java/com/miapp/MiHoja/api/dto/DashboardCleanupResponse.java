package com.miapp.MiHoja.api.dto;

public record DashboardCleanupResponse(
        String message,
        int personasRevisadas,
        int alergiasEliminadas,
        int medicamentosEliminados,
        int enfermedadesEliminadas
) {
}
