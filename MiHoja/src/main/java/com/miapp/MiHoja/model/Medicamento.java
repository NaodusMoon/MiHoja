package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "medicamento")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n", nullable = false)
    @JsonIgnore
    private Persona persona;

    @ManyToMany(mappedBy = "medicamentos")
    private Set<Enfermedad> enfermedades = new HashSet<>();

    // Constructores
    public Medicamento() {}

    public Medicamento(String nombre) {
        this.nombre = nombre;
    }

    // Métodos de asociación bidireccional
    public void addEnfermedad(Enfermedad enfermedad) {
        if (enfermedades.add(enfermedad)) {
            enfermedad.getMedicamentos().add(this);
        }
    }

    public void removeEnfermedad(Enfermedad enfermedad) {
        if (enfermedades.remove(enfermedad)) {
            enfermedad.getMedicamentos().remove(this);
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Set<Enfermedad> getEnfermedades() {
        return enfermedades;
    }

    public void setEnfermedades(Set<Enfermedad> enfermedades) {
        this.enfermedades = enfermedades;
    }

    @Override
    public String toString() {
        return "Medicamento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicamento)) return false;
        Medicamento that = (Medicamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
