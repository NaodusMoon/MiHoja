package com.miapp.MiHoja.dto;

/**
 * Proyección para mostrar los datos de una persona junto con su cargo más reciente.
 * Coincide con la consulta nativa en PersonaRepository.consultarPersonasConCargo().
 */
public interface PersonaConCargo {

    // ✅ ID y datos básicos
    Long getId();                       
    String getNombres();
    String getApellidos();
    String getCedula();
    Integer getNumero();

    // ✅ Cargo más reciente
    String getCargo();
    String getDependencia();

    // ✅ Datos personales y de contacto
    String getLugarExpedicion();
    String getDireccion();
    String getSexo();
    String getCorreoInstitucional();
    String getTelefonoInstitucional();
    String getEnlaceSigep();

    // ✅ Nuevos campos agregados
    String getEstado();
    Integer getNumeroHijos();
    String getImagenUrl();

    // ✅ Formación (última)
    String getFormacion();
    String getGrado();

    // ✅ Salud
    String getRh();
    String getEps();
    String getAfp();
    String getDotacion();
    String getCarnetVacunacion();

    // ✅ Riesgo y procedencia
    String getRiesgo();
    String getMedioTransporte();
    String getProcedencia();

    // ✅ Inducción y examen
    String getInduccion();
    String getExamen();

    // ✅ Experiencia
    String getMesesExperiencia();
}
