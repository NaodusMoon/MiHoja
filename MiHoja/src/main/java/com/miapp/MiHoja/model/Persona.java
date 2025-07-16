package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "persona")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n")
    private Long id;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "cedula", unique = true)
    private String cedula;

    @Column(name = "lugar_expedicion")
    private String lugarExpedicion;

    @Column(name = "fecha_nacimiento")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "sexo")
    private String sexo;

    @Column(name = "correo_institucional")
    private String correoInstitucional;

    @Column(name = "telefono_institucional")
    private String telefonoInstitucional;

    @Column(name = "enlace_sigep")
    private String enlaceSigep;

    // Relaciones

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Medicamento> medicamentos;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Formacion> formaciones;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<RiesgoProcedencia> riesgoProcedencias;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Salud> registrosSalud;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ContactoEmergencia> contactosEmergencia;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Enfermedad> enfermedades;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Alergia> alergias;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<PersonaCargoLaboral> cargosLaborales;

    // Getters y Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

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

    public String getCorreoInstitucional() { return correoInstitucional; }
    public void setCorreoInstitucional(String correoInstitucional) { this.correoInstitucional = correoInstitucional; }

    public String getTelefonoInstitucional() { return telefonoInstitucional; }
    public void setTelefonoInstitucional(String telefonoInstitucional) { this.telefonoInstitucional = telefonoInstitucional; }

    public String getEnlaceSigep() { return enlaceSigep; }
    public void setEnlaceSigep(String enlaceSigep) { this.enlaceSigep = enlaceSigep; }

    public Set<Medicamento> getMedicamentos() { return medicamentos; }
    public void setMedicamentos(Set<Medicamento> medicamentos) { this.medicamentos = medicamentos; }

    public Set<Formacion> getFormaciones() { return formaciones; }
    public void setFormaciones(Set<Formacion> formaciones) { this.formaciones = formaciones; }

    public Set<RiesgoProcedencia> getRiesgoProcedencias() { return riesgoProcedencias; }
    public void setRiesgoProcedencias(Set<RiesgoProcedencia> riesgoProcedencias) { this.riesgoProcedencias = riesgoProcedencias; }

    public Set<Salud> getRegistrosSalud() { return registrosSalud; }
    public void setRegistrosSalud(Set<Salud> registrosSalud) { this.registrosSalud = registrosSalud; }

    public Set<ContactoEmergencia> getContactosEmergencia() { return contactosEmergencia; }
    public void setContactosEmergencia(Set<ContactoEmergencia> contactosEmergencia) { this.contactosEmergencia = contactosEmergencia; }

    public Set<Enfermedad> getEnfermedades() { return enfermedades; }
    public void setEnfermedades(Set<Enfermedad> enfermedades) { this.enfermedades = enfermedades; }

    public Set<Alergia> getAlergias() { return alergias; }
    public void setAlergias(Set<Alergia> alergias) { this.alergias = alergias; }

    public Set<PersonaCargoLaboral> getCargosLaborales() { return cargosLaborales; }
    public void setCargosLaborales(Set<PersonaCargoLaboral> cargosLaborales) { this.cargosLaborales = cargosLaborales; }

    @Override
    public String toString() {
        return "Persona{" +
                "id=" + id +
                ", numero=" + numero +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", cedula='" + cedula + '\'' +
                ", lugarExpedicion='" + lugarExpedicion + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", direccion='" + direccion + '\'' +
                ", sexo='" + sexo + '\'' +
                ", correoInstitucional='" + correoInstitucional + '\'' +
                ", telefonoInstitucional='" + telefonoInstitucional + '\'' +
                ", enlaceSigep='" + enlaceSigep + '\'' +
                '}';
    }
}
