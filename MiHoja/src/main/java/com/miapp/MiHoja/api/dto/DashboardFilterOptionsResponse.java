package com.miapp.MiHoja.api.dto;

import java.util.List;

public record DashboardFilterOptionsResponse(
        List<String> sexo,
        List<String> lugarExpedicion,
        List<String> formacion,
        List<String> dependencia,
        List<String> cargo
) {
}
