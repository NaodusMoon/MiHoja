package com.miapp.MiHoja.api.dto;

public record DashboardPersonCardResponse(
        Long id,
        Integer numero,
        String nombres,
        String apellidos,
        String cedula,
        String cargo,
        String dependencia,
        String correoInstitucional,
        String telefonoInstitucional,
        String estado,
        String imagenUrl
) {
}
