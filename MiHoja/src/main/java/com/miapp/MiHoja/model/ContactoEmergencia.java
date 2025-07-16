package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "contacto_emergencia")
public class ContactoEmergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contacto")
    private Long idContacto;

    @Column(name = "nombre_contacto_emergencia")
    private String nombreContactoEmergencia;

    @Column(name = "parentesco")
    private String parentesco;

    @Column(name = "telefono_contacto_emergencia")
    private String telefonoContactoEmergencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n", referencedColumnName = "n", nullable = false)
    @JsonIgnore
    private Persona persona;

    // Constructor vacío
    public ContactoEmergencia() {}

    public ContactoEmergencia(String nombre, String parentesco, String telefono) {
        this.nombreContactoEmergencia = nombre;
        this.parentesco = parentesco;
        this.telefonoContactoEmergencia = telefono;
    }

    // Getters y Setters
    public Long getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(Long idContacto) {
        this.idContacto = idContacto;
    }

    public String getNombreContactoEmergencia() {
        return nombreContactoEmergencia;
    }

    public void setNombreContactoEmergencia(String nombreContactoEmergencia) {
        this.nombreContactoEmergencia = nombreContactoEmergencia;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public String getTelefonoContactoEmergencia() {
        return telefonoContactoEmergencia;
    }

    public void setTelefonoContactoEmergencia(String telefonoContactoEmergencia) {
        this.telefonoContactoEmergencia = telefonoContactoEmergencia;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    @Override
    public String toString() {
        return "ContactoEmergencia{" +
                "idContacto=" + idContacto +
                ", nombreContactoEmergencia='" + nombreContactoEmergencia + '\'' +
                ", parentesco='" + parentesco + '\'' +
                ", telefonoContactoEmergencia='" + telefonoContactoEmergencia + '\'' +
                '}';
    }
}
