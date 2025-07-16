package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "induccion_examen")
public class InduccionExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_induccion")
    private Long idInduccion;

    @Column(name = "induccion")
    private Boolean induccion;

    @Column(name = "examen_ingreso")
    private Boolean examenIngreso;

    @Column(name = "fecha_egreso")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEgreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_cargo_id", nullable = false)
    @JsonIgnore
    private PersonaCargoLaboral personaCargoLaboral;

    // Constructor vacío
    public InduccionExamen() {}

    // Constructor auxiliar útil si se transforman valores de texto a booleano
    public InduccionExamen(String examen, String induccion) {
        this.examenIngreso = parseBoolean(examen);
        this.induccion = parseBoolean(induccion);
    }

    private Boolean parseBoolean(String value) {
        if (value == null) return null;
        value = value.trim().toLowerCase();
        return value.equals("si") || value.equals("sí") || value.equals("true") || value.equals("1");
    }

    // Getters y Setters

    public Long getIdInduccion() {
        return idInduccion;
    }

    public void setIdInduccion(Long idInduccion) {
        this.idInduccion = idInduccion;
    }

    public Boolean getInduccion() {
        return induccion;
    }

    public void setInduccion(Boolean induccion) {
        this.induccion = induccion;
    }

    public Boolean getExamenIngreso() {
        return examenIngreso;
    }

    public void setExamenIngreso(Boolean examenIngreso) {
        this.examenIngreso = examenIngreso;
    }

    public LocalDate getFechaEgreso() {
        return fechaEgreso;
    }

    public void setFechaEgreso(LocalDate fechaEgreso) {
        this.fechaEgreso = fechaEgreso;
    }

    public PersonaCargoLaboral getPersonaCargoLaboral() {
        return personaCargoLaboral;
    }

    public void setPersonaCargoLaboral(PersonaCargoLaboral personaCargoLaboral) {
        this.personaCargoLaboral = personaCargoLaboral;
    }

    @Override
    public String toString() {
        return "InduccionExamen{" +
                "idInduccion=" + idInduccion +
                ", induccion=" + induccion +
                ", examenIngreso=" + examenIngreso +
                ", fechaEgreso=" + fechaEgreso +
                '}';
    }
}
