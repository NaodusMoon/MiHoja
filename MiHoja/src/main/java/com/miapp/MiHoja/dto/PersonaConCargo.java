package com.miapp.MiHoja.dto;

/**
 * Proyección para mostrar los datos de una persona junto con su cargo más reciente.
 */
public interface PersonaConCargo {
    Long getId();                       // ID de la persona (campo "n")
    String getNombres();
    String getApellidos();
    String getCedula();
    Integer getNumero();
    String getCargo();
    String getDependencia();

    // ✅ Campos adicionales que ya tenías
    String getLugarExpedicion();
    String getDireccion();
    String getSexo();
    String getCorreoInstitucional();
    String getTelefonoInstitucional();
    String getEnlaceSigep();

    // ✅ NUEVOS CAMPOS PARA FILTROS
    String getFormacion();
    String getGrado();
    String getRh();
    String getEps();
    String getAfp();
    String getCarnetVacunacion();
    String getRiesgo();
    String getMedioTransporte();
    String getProcedencia();
    String getInduccion();
    String getExamen();
    String getMesesExperiencia();
    String getDotacion();
}
