package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "enfermedad")
public class Enfermedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_enfermedad")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n", referencedColumnName = "n", nullable = false)
    @JsonIgnore
    private Persona persona;

    @ManyToMany
    @JoinTable(
        name = "enfermedad_medicamento",
        joinColumns = @JoinColumn(name = "enfermedad_id"),
        inverseJoinColumns = @JoinColumn(name = "medicamento_id")
    )
    private Set<Medicamento> medicamentos = new HashSet<>();

    // Constructores
    public Enfermedad() {}

    public Enfermedad(String nombre) {
        this.nombre = nombre;
    }

    // Métodos de asociación bidireccional
    public void addMedicamento(Medicamento medicamento) {
        if (medicamentos.add(medicamento)) {
            medicamento.getEnfermedades().add(this);
        }
    }

    public void removeMedicamento(Medicamento medicamento) {
        if (medicamentos.remove(medicamento)) {
            medicamento.getEnfermedades().remove(this);
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

    public Set<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(Set<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }

    @Override
    public String toString() {
        return "Enfermedad{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enfermedad)) return false;
        Enfermedad that = (Enfermedad) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
