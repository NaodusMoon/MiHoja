package com.miapp.MiHoja.api.service;

import java.util.List;

public record DashboardPeopleQuery(
        String query,
        List<String> sexo,
        List<String> lugarExpedicion,
        List<String> formacion,
        List<String> dependencia,
        List<String> cargo,
        String sortBy,
        int page,
        int size
) {
}
