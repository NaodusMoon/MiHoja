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
    private String lugar_expedicion;
    private LocalDate fecha_nacimiento;
    private String direccion;
    private String sexo;
    private Integer numero;
    private String correo_institucional;
    private String telefono_institucional;
    private String enlace_sigep;
    private String estado;
    private Integer numero_hijos;
    private String imagen_url;

    // ========================
    // FORMACION (1:N)
    // ========================
    private List<Formacion> formacion;

    public static class Formacion {
        private Long id_formacion;
        private Long n;
        private String formacion_academica;
        private String grado;
        private String titulo;
        // getters y setters
        public Long getId_formacion() { return id_formacion; }
        public void setId_formacion(Long id_formacion) { this.id_formacion = id_formacion; }
        public Long getN() { return n; }
        public void setN(Long n) { this.n = n; }
        public String getFormacion_academica() { return formacion_academica; }
        public void setFormacion_academica(String formacion_academica) { this.formacion_academica = formacion_academica; }
        public String getGrado() { return grado; }
        public void setGrado(String grado) { this.grado = grado; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
    }

    // ========================
    // CARGO LABORAL (N:M)
    // ========================
    private List<CargoLaboral> cargo_laboral;
    private List<PersonaCargoLaboral> personaCargoLaboral;

    public static class CargoLaboral {
        private Long id_cargo;
        private String cargo;
        private String codigo;
        private String dependencia;
        // getters y setters
        public Long getId_cargo() { return id_cargo; }
        public void setId_cargo(Long id_cargo) { this.id_cargo = id_cargo; }
        public String getCargo() { return cargo; }
        public void setCargo(String cargo) { this.cargo = cargo; }
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public String getDependencia() { return dependencia; }
        public void setDependencia(String dependencia) { this.dependencia = dependencia; }
    }

    public static class PersonaCargoLaboral {
        private Long id_pcl;
        private Long persona_id;
        private Long cargo_id;
        private LocalDate fecha_ingreso;
        private LocalDate fecha_firma_contrato;
        private Integer meses_experiencia;
        // getters y setters
        public Long getId_pcl() { return id_pcl; }
        public void setId_pcl(Long id_pcl) { this.id_pcl = id_pcl; }
        public Long getPersona_id() { return persona_id; }
        public void setPersona_id(Long persona_id) { this.persona_id = persona_id; }
        public Long getCargo_id() { return cargo_id; }
        public void setCargo_id(Long cargo_id) { this.cargo_id = cargo_id; }
        public LocalDate getFecha_ingreso() { return fecha_ingreso; }
        public void setFecha_ingreso(LocalDate fecha_ingreso) { this.fecha_ingreso = fecha_ingreso; }
        public LocalDate getFecha_firma_contrato() { return fecha_firma_contrato; }
        public void setFecha_firma_contrato(LocalDate fecha_firma_contrato) { this.fecha_firma_contrato = fecha_firma_contrato; }
        public Integer getMeses_experiencia() { return meses_experiencia; }
        public void setMeses_experiencia(Integer meses_experiencia) { this.meses_experiencia = meses_experiencia; }
    }

    // ========================
    // INDUCCION / EXAMEN (1:N por persona_cargo)
    // ========================
    private String induccionExamen;

    public static class InduccionExamen {
        private Long id_induccion;
        private Long persona_cargo_id;
        private Boolean induccion;
        private Boolean examen_ingreso;
        private LocalDate fecha_egreso;
        // getters y setters
        public Long getId_induccion() { return id_induccion; }
        public void setId_induccion(Long id_induccion) { this.id_induccion = id_induccion; }
        public Long getPersona_cargo_id() { return persona_cargo_id; }
        public void setPersona_cargo_id(Long persona_cargo_id) { this.persona_cargo_id = persona_cargo_id; }
        public Boolean getInduccion() { return induccion; }
        public void setInduccion(Boolean induccion) { this.induccion = induccion; }
        public Boolean getExamen_ingreso() { return examen_ingreso; }
        public void setExamen_ingreso(Boolean examen_ingreso) { this.examen_ingreso = examen_ingreso; }
        public LocalDate getFecha_egreso() { return fecha_egreso; }
        public void setFecha_egreso(LocalDate fecha_egreso) { this.fecha_egreso = fecha_egreso; }
    }

    // ========================
    // RIESGO PROCEDENCIA (1:N)
    // ========================
    private List<RiesgoProcedencia> riesgo_procedencia;

    public static class RiesgoProcedencia {
        private Long id_riesgo;
        private Long n;
        private String riesgo;
        private String medio_transporte;
        private String procedencia_trabajador;
        // getters y setters
        public Long getId_riesgo() { return id_riesgo; }
        public void setId_riesgo(Long id_riesgo) { this.id_riesgo = id_riesgo; }
        public Long getN() { return n; }
        public void setN(Long n) { this.n = n; }
        public String getRiesgo() { return riesgo; }
        public void setRiesgo(String riesgo) { this.riesgo = riesgo; }
        public String getMedio_transporte() { return medio_transporte; }
        public void setMedio_transporte(String medio_transporte) { this.medio_transporte = medio_transporte; }
        public String getProcedencia_trabajador() { return procedencia_trabajador; }
        public void setProcedencia_trabajador(String procedencia_trabajador) { this.procedencia_trabajador = procedencia_trabajador; }
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

    // getters y setters
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
    private List<ContactoEmergencia> contacto_emergencia;

    public static class ContactoEmergencia {
        private Long id_contacto;
        private Long n;
        private String nombre_contacto_emergencia;
        private String parentesco;
        private String telefono_contacto_emergencia;
        // getters y setters
        public Long getId_contacto() { return id_contacto; }
        public void setId_contacto(Long id_contacto) { this.id_contacto = id_contacto; }
        public Long getN() { return n; }
        public void setN(Long n) { this.n = n; }
        public String getNombre_contacto_emergencia() { return nombre_contacto_emergencia; }
        public void setNombre_contacto_emergencia(String nombre_contacto_emergencia) { this.nombre_contacto_emergencia = nombre_contacto_emergencia; }
        public String getParentesco() { return parentesco; }
        public void setParentesco(String parentesco) { this.parentesco = parentesco; }
        public String getTelefono_contacto_emergencia() { return telefono_contacto_emergencia; }
        public void setTelefono_contacto_emergencia(String telefono_contacto_emergencia) { this.telefono_contacto_emergencia = telefono_contacto_emergencia; }
    }

    // ========================
    // ENFERMEDAD (1:N)
    // ========================
    private List<Enfermedad> enfermedad;

    public static class Enfermedad {
        private Long id_enfermedad;
        private Long n;
        private String nombre;
        // getters y setters
        public Long getId_enfermedad() { return id_enfermedad; }
        public void setId_enfermedad(Long id_enfermedad) { this.id_enfermedad = id_enfermedad; }
        public Long getN() { return n; }
        public void setN(Long n) { this.n = n; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    // ========================
    // MEDICAMENTO (independiente + relación enfermedad_medicamento)
    // ========================
    private List<Medicamento> medicamento;
    private List<EnfermedadMedicamento> enfermedad_medicamento;

    public static class Medicamento {
        private Long id_medicamento;
        private String nombre;
        // getters y setters
        public Long getId_medicamento() { return id_medicamento; }
        public void setId_medicamento(Long id_medicamento) { this.id_medicamento = id_medicamento; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    public static class EnfermedadMedicamento {
        private Long enfermedad_id;
        private Long medicamento_id;
        // getters y setters
        public Long getEnfermedad_id() { return enfermedad_id; }
        public void setEnfermedad_id(Long enfermedad_id) { this.enfermedad_id = enfermedad_id; }
        public Long getMedicamento_id() { return medicamento_id; }
        public void setMedicamento_id(Long medicamento_id) { this.medicamento_id = medicamento_id; }
    }

    // ========================
    // ALERGIA (1:N)
    // ========================
    private List<Alergia> alergia;

    public static class Alergia {
        private Long id_alergia;
        private Long n;
        private String nombre;
        // getters y setters
        public Long getId_alergia() { return id_alergia; }
        public void setId_alergia(Long id_alergia) { this.id_alergia = id_alergia; }
        public Long getN() { return n; }
        public void setN(Long n) { this.n = n; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

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
    public String getLugar_expedicion() { return lugar_expedicion; }
    public void setLugar_expedicion(String lugar_expedicion) { this.lugar_expedicion = lugar_expedicion; }
    public LocalDate getFecha_nacimiento() { return fecha_nacimiento; }
    public void setFecha_nacimiento(LocalDate fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public String getCorreo_institucional() { return correo_institucional; }
    public void setCorreo_institucional(String correo_institucional) { this.correo_institucional = correo_institucional; }
    public String getTelefono_institucional() { return telefono_institucional; }
    public void setTelefono_institucional(String telefono_institucional) { this.telefono_institucional = telefono_institucional; }
    public String getEnlace_sigep() { return enlace_sigep; }
    public void setEnlace_sigep(String enlace_sigep) { this.enlace_sigep = enlace_sigep; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getNumero_hijos() { return numero_hijos; }
    public void setNumero_hijos(Integer numero_hijos) { this.numero_hijos = numero_hijos; }
    public String getImagen_url() { return imagen_url; }
    public void setImagen_url(String imagen_url) { this.imagen_url = imagen_url; }

    // getters y setters para listas
    public List<Formacion> getFormacion() { return formacion; }
    public void setFormacion(List<Formacion> formacion) { this.formacion = formacion; }
    public List<CargoLaboral> getCargo_laboral() { return cargo_laboral; }
    public void setCargo_laboral(List<CargoLaboral> cargo_laboral) { this.cargo_laboral = cargo_laboral; }
    public List<PersonaCargoLaboral> getPersonaCargoLaboral() {
    return personaCargoLaboral;
}

public void setPersonaCargoLaboral(List<PersonaCargoLaboral> personaCargoLaboral) {
    this.personaCargoLaboral = personaCargoLaboral;
}
    public String getInduccionExamen() {
    return induccionExamen;
}

public void setInduccionExamen(String induccionExamen) {
    this.induccionExamen = induccionExamen;
}
    public List<RiesgoProcedencia> getRiesgo_procedencia() { return riesgo_procedencia; }
    public void setRiesgo_procedencia(List<RiesgoProcedencia> riesgo_procedencia) { this.riesgo_procedencia = riesgo_procedencia; }
    public Salud getSalud() { return salud; }
    public void setSalud(Salud salud) { this.salud = salud; }
    public List<ContactoEmergencia> getContacto_emergencia() { return contacto_emergencia; }
    public void setContacto_emergencia(List<ContactoEmergencia> contacto_emergencia) { this.contacto_emergencia = contacto_emergencia; }
    public List<Enfermedad> getEnfermedad() { return enfermedad; }
    public void setEnfermedad(List<Enfermedad> enfermedad) { this.enfermedad = enfermedad; }
    public List<Medicamento> getMedicamento() { return medicamento; }
    public void setMedicamento(List<Medicamento> medicamento) { this.medicamento = medicamento; }
    public List<EnfermedadMedicamento> getEnfermedad_medicamento() { return enfermedad_medicamento; }
    public void setEnfermedad_medicamento(List<EnfermedadMedicamento> enfermedad_medicamento) { this.enfermedad_medicamento = enfermedad_medicamento; }
    public List<Alergia> getAlergia() { return alergia; }
    public void setAlergia(List<Alergia> alergia) { this.alergia = alergia; }
}
