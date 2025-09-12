package com.miapp.MiHoja.dto;

import java.time.LocalDate;
import java.util.List;

public class PersonaCompletaDTO {

    // ========================
    // PERSONA
    // ========================
    private Long id;
    private String nombres;
    private String apellidos;
    private String cedula;
    private String lugarExpedicion;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String sexo;
    private Integer numero;
    private String correoInstitucional;
    private String telefonoInstitucional;
    private String enlaceSigep;
    private String estado;
    private Integer numeroHijos;
    private String imagenUrl;

    // ========================
    // FORMACION (1:N)
    // ========================
    private List<Formacion> formacion;
    public static class Formacion {
        private Long idFormacion;
        private Long n;
        private String formacionAcademica;
        private String grado;
        private String titulo;
        // Getters y Setters
        public Long getIdFormacion() { return idFormacion; }
        public void setIdFormacion(Long idFormacion) { this.idFormacion = idFormacion; }
        public Long getN() { return n; }
        public void setN(Long n) { this.n = n; }
        public String getFormacionAcademica() { return formacionAcademica; }
        public void setFormacionAcademica(String formacionAcademica) { this.formacionAcademica = formacionAcademica; }
        public String getGrado() { return grado; }
        public void setGrado(String grado) { this.grado = grado; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
    }

    // ========================
    // CARGO LABORAL (N:M)
    // ========================
    private List<CargoLaboral> cargoLaboral;
    private List<PersonaCargoLaboral> personaCargoLaboral;
    public static class CargoLaboral {
        private Long idCargo;
        private String cargo;
        private String codigo;
        private String dependencia;
        // Getters y Setters
        public Long getIdCargo() { return idCargo; }
        public void setIdCargo(Long idCargo) { this.idCargo = idCargo; }
        public String getCargo() { return cargo; }
        public void setCargo(String cargo) { this.cargo = cargo; }
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public String getDependencia() { return dependencia; }
        public void setDependencia(String dependencia) { this.dependencia = dependencia; }
    }
    public static class PersonaCargoLaboral {
        private Long idPcl;
        private Long personaId;
        private Long cargoId;
        private LocalDate fechaIngreso;
        private LocalDate fechaFirmaContrato;
        private Integer mesesExperiencia;
        // Getters y Setters
        public Long getIdPcl() { return idPcl; }
        public void setIdPcl(Long idPcl) { this.idPcl = idPcl; }
        public Long getPersonaId() { return personaId; }
        public void setPersonaId(Long personaId) { this.personaId = personaId; }
        public Long getCargoId() { return cargoId; }
        public void setCargoId(Long cargoId) { this.cargoId = cargoId; }
        public LocalDate getFechaIngreso() { return fechaIngreso; }
        public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
        public LocalDate getFechaFirmaContrato() { return fechaFirmaContrato; }
        public void setFechaFirmaContrato(LocalDate fechaFirmaContrato) { this.fechaFirmaContrato = fechaFirmaContrato; }
        public Integer getMesesExperiencia() { return mesesExperiencia; }
        public void setMesesExperiencia(Integer mesesExperiencia) { this.mesesExperiencia = mesesExperiencia; }
    }

    // ========================
    // INDUCCION / EXAMEN
    // ========================
    private List<InduccionExamen> induccionExamen;
    public static class InduccionExamen {
        private Long idInduccion;
        private Long personaCargoId;
        private Boolean induccion;
        private Boolean examenIngreso;
        private LocalDate fechaEgreso;
        // Getters y Setters
        public Long getIdInduccion() { return idInduccion; }
        public void setIdInduccion(Long idInduccion) { this.idInduccion = idInduccion; }
        public Long getPersonaCargoId() { return personaCargoId; }
        public void setPersonaCargoId(Long personaCargoId) { this.personaCargoId = personaCargoId; }
        public Boolean getInduccion() { return induccion; }
        public void setInduccion(Boolean induccion) { this.induccion = induccion; }
        public Boolean getExamenIngreso() { return examenIngreso; }
        public void setExamenIngreso(Boolean examenIngreso) { this.examenIngreso = examenIngreso; }
        public LocalDate getFechaEgreso() { return fechaEgreso; }
        public void setFechaEgreso(LocalDate fechaEgreso) { this.fechaEgreso = fechaEgreso; }
    }

    // ========================
    // RIESGO PROCEDENCIA (1:N)
    // ========================
    private List<RiesgoProcedencia> riesgoProcedencia;
    public static class RiesgoProcedencia {
        private Long idRiesgo;
        private Long n;
        private String riesgo;
        private String medioTransporte;
        private String procedenciaTrabajador;
        // Getters y Setters
        public Long getIdRiesgo() { return idRiesgo; }
        public void setIdRiesgo(Long idRiesgo) { this.idRiesgo = idRiesgo; }
        public Long getN() { return n; }
        public void setN(Long n) { this.n = n; }
        public String getRiesgo() { return riesgo; }
        public void setRiesgo(String riesgo) { this.riesgo = riesgo; }
        public String getMedioTransporte() { return medioTransporte; }
        public void setMedioTransporte(String medioTransporte) { this.medioTransporte = medioTransporte; }
        public String getProcedenciaTrabajador() { return procedenciaTrabajador; }
        public void setProcedenciaTrabajador(String procedenciaTrabajador) { this.procedenciaTrabajador = procedenciaTrabajador; }
    }

    // ========================
    // SALUD (1:1)
    // ========================
    private Salud salud;
    public static class Salud {
        private Long idSalud;
        private Long n;
        private String dotacion;
        private String arl;
        private String eps;
        private String afp;
        private String ccf;
        private String rh;
        private Boolean carnetVacunacion;
        // Getters y Setters
        public Long getIdSalud() { return idSalud; }
        public void setIdSalud(Long idSalud) { this.idSalud = idSalud; }
        public Long getN() { return n; }
        public void setN(Long n) { this.n = n; }
        public String getDotacion() { return dotacion; }
        public void setDotacion(String dotacion) { this.dotacion = dotacion; }
        public String getArl() { return arl; }
        public void setArl(String arl) { this.arl = arl; }
        public String getEps() { return eps; }
        public void setEps(String eps) { this.eps = eps; }
        public String getAfp() { return afp; }
        public void setAfp(String afp) { this.afp = afp; }
        public String getCcf() { return ccf; }
        public void setCcf(String ccf) { this.ccf = ccf; }
        public String getRh() { return rh; }
        public void setRh(String rh) { this.rh = rh; }
        public Boolean getCarnetVacunacion() { return carnetVacunacion; }
        public void setCarnetVacunacion(Boolean carnetVacunacion) { this.carnetVacunacion = carnetVacunacion; }
    }

    // ========================
    // CONTACTO EMERGENCIA (1:N)
    // ========================
    private List<ContactoEmergencia> contactoEmergencia;
    public static class ContactoEmergencia {
        private Long idContacto;
        private Long n;
        private String nombreContactoEmergencia;
        private String parentesco;
        private String telefonoContactoEmergencia;
        // Getters y Setters
        public Long getIdContacto() { return idContacto; }
        public void setIdContacto(Long idContacto) { this.idContacto = idContacto; }
        public Long getN() { return n; }
        public void setN(Long n) { this.n = n; }
        public String getNombreContactoEmergencia() { return nombreContactoEmergencia; }
        public void setNombreContactoEmergencia(String nombreContactoEmergencia) { this.nombreContactoEmergencia = nombreContactoEmergencia; }
        public String getParentesco() { return parentesco; }
        public void setParentesco(String parentesco) { this.parentesco = parentesco; }
        public String getTelefonoContactoEmergencia() { return telefonoContactoEmergencia; }
        public void setTelefonoContactoEmergencia(String telefonoContactoEmergencia) { this.telefonoContactoEmergencia = telefonoContactoEmergencia; }
    }

    // ========================
// ENFERMEDAD (1:N)
// ========================
private List<Enfermedad> enfermedad;

// 👇 Campo auxiliar para formulario
private String enfermedades;

public static class Enfermedad {
    private Long idEnfermedad;
    private Long n;
    private String nombre;

    // Getters y Setters
    public Long getIdEnfermedad() { return idEnfermedad; }
    public void setIdEnfermedad(Long idEnfermedad) { this.idEnfermedad = idEnfermedad; }

    public Long getN() { return n; }
    public void setN(Long n) { this.n = n; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

// ========================
// MEDICAMENTO
// ========================
private List<Medicamento> medicamento;
private List<EnfermedadMedicamento> enfermedadMedicamento;

// 👇 Campo auxiliar para formulario
private String medicamentos;

public static class Medicamento {
    private Long idMedicamento;
    private String nombre;

    // Getters y Setters
    public Long getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(Long idMedicamento) { this.idMedicamento = idMedicamento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

public static class EnfermedadMedicamento {
    private Long enfermedadId;
    private Long medicamentoId;

    // Getters y Setters
    public Long getEnfermedadId() { return enfermedadId; }
    public void setEnfermedadId(Long enfermedadId) { this.enfermedadId = enfermedadId; }

    public Long getMedicamentoId() { return medicamentoId; }
    public void setMedicamentoId(Long medicamentoId) { this.medicamentoId = medicamentoId; }
}

// ========================
// ALERGIA (1:N)
// ========================
private List<Alergia> alergia;

// 👇 Campo auxiliar para formulario
private String alergias;

public static class Alergia {
    private Long idAlergia;
    private Long n;
    private String nombre;

    // Getters y Setters
    public Long getIdAlergia() { return idAlergia; }
    public void setIdAlergia(Long idAlergia) { this.idAlergia = idAlergia; }

    public Long getN() { return n; }
    public void setN(Long n) { this.n = n; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

// ========================
// GETTERS y SETTERS auxiliares
// ========================
public String getEnfermedades() { return enfermedades; }
public void setEnfermedades(String enfermedades) { this.enfermedades = enfermedades; }

public String getMedicamentos() { return medicamentos; }
public void setMedicamentos(String medicamentos) { this.medicamentos = medicamentos; }

public String getAlergias() { return alergias; }
public void setAlergias(String alergias) { this.alergias = alergias; }

    // ========================
    // GETTERS Y SETTERS DE PERSONA
    // ========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getLugarExpedicion() { return lugarExpedicion; }
    public void setLugarExpedicion(String lugarExpedicion) { this.lugarExpedicion = lugarExpedicion; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public String getCorreoInstitucional() { return correoInstitucional; }
    public void setCorreoInstitucional(String correoInstitucional) { this.correoInstitucional = correoInstitucional; }
    public String getTelefonoInstitucional() { return telefonoInstitucional; }
    public void setTelefonoInstitucional(String telefonoInstitucional) { this.telefonoInstitucional = telefonoInstitucional; }
    public String getEnlaceSigep() { return enlaceSigep; }
    public void setEnlaceSigep(String enlaceSigep) { this.enlaceSigep = enlaceSigep; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getNumeroHijos() { return numeroHijos; }
    public void setNumeroHijos(Integer numeroHijos) { this.numeroHijos = numeroHijos; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    // ========================
    // GETTERS Y SETTERS DE LISTAS
    // ========================
    public List<Formacion> getFormacion() { return formacion; }
    public void setFormacion(List<Formacion> formacion) { this.formacion = formacion; }
    public List<CargoLaboral> getCargoLaboral() { return cargoLaboral; }
    public void setCargoLaboral(List<CargoLaboral> cargoLaboral) { this.cargoLaboral = cargoLaboral; }
    public List<PersonaCargoLaboral> getPersonaCargoLaboral() { return personaCargoLaboral; }
    public void setPersonaCargoLaboral(List<PersonaCargoLaboral> personaCargoLaboral) { this.personaCargoLaboral = personaCargoLaboral; }
    public List<InduccionExamen> getInduccionExamen() { return induccionExamen; }
    public void setInduccionExamen(List<InduccionExamen> induccionExamen) { this.induccionExamen = induccionExamen; }
    public List<RiesgoProcedencia> getRiesgoProcedencia() { return riesgoProcedencia; }
    public void setRiesgoProcedencia(List<RiesgoProcedencia> riesgoProcedencia) { this.riesgoProcedencia = riesgoProcedencia; }
    public Salud getSalud() { return salud; }
    public void setSalud(Salud salud) { this.salud = salud; }
    public List<ContactoEmergencia> getContactoEmergencia() { return contactoEmergencia; }
    public void setContactoEmergencia(List<ContactoEmergencia> contactoEmergencia) { this.contactoEmergencia = contactoEmergencia; }
    public List<Enfermedad> getEnfermedad() { return enfermedad; }
    public void setEnfermedad(List<Enfermedad> enfermedad) { this.enfermedad = enfermedad; }
    public List<Medicamento> getMedicamento() { return medicamento; }
    public void setMedicamento(List<Medicamento> medicamento) { this.medicamento = medicamento; }
    public List<EnfermedadMedicamento> getEnfermedadMedicamento() { return enfermedadMedicamento; }
    public void setEnfermedadMedicamento(List<EnfermedadMedicamento> enfermedadMedicamento) { this.enfermedadMedicamento = enfermedadMedicamento; }
    public List<Alergia> getAlergia() { return alergia; }
    public void setAlergia(List<Alergia> alergia) { this.alergia = alergia; }
}
