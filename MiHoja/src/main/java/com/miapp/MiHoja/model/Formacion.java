package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "formacion")
public class Formacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formacion")
    private Long idFormacion;

    @Column(name = "formacion_academica")
    private String formacionAcademica;

    @Column(name = "grado")
    private String grado;

    @Column(name = "titulo")
    private String titulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n", nullable = false)
    @JsonIgnore
    private Persona persona;

    // Constructores
    public Formacion() {}

    public Formacion(String titulo) {
        this.titulo = titulo;
    }

    public Formacion(String titulo, String grado) {
        this.titulo = titulo;
        this.grado = grado;
    }

    public Formacion(String titulo, String grado, String formacionAcademica) {
        this.titulo = titulo;
        this.grado = grado;
        this.formacionAcademica = formacionAcademica;
    }

    // Getters y Setters
    public Long getIdFormacion() {
        return idFormacion;
    }

    public void setIdFormacion(Long idFormacion) {
        this.idFormacion = idFormacion;
    }

    public String getFormacionAcademica() {
        return formacionAcademica;
    }

    public void setFormacionAcademica(String formacionAcademica) {
        this.formacionAcademica = formacionAcademica;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    @Override
    public String toString() {
        return "Formacion{" +
                "idFormacion=" + idFormacion +
                ", formacionAcademica='" + formacionAcademica + '\'' +
                ", grado='" + grado + '\'' +
                ", titulo='" + titulo + '\'' +
                '}';
    }
}
