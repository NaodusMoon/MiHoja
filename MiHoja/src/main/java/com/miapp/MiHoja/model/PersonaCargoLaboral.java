package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "persona_cargo_laboral")
public class PersonaCargoLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pcl")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    @JsonIgnore
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    private CargoLaboral cargo;

    @Column(name = "fecha_ingreso")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_firma_contrato")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFirmaContrato;

    @Column(name = "meses_experiencia")
    private Integer mesesExperiencia;

    @OneToMany(mappedBy = "personaCargoLaboral", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InduccionExamen> induccionesExamen;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public CargoLaboral getCargo() {
        return cargo;
    }

    public void setCargo(CargoLaboral cargo) {
        this.cargo = cargo;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDate getFechaFirmaContrato() {
        return fechaFirmaContrato;
    }

    public void setFechaFirmaContrato(LocalDate fechaFirmaContrato) {
        this.fechaFirmaContrato = fechaFirmaContrato;
    }

    public Integer getMesesExperiencia() {
        return mesesExperiencia;
    }

    public void setMesesExperiencia(Integer mesesExperiencia) {
        this.mesesExperiencia = mesesExperiencia;
    }

    public List<InduccionExamen> getInduccionesExamen() {
        return induccionesExamen;
    }

    public void setInduccionesExamen(List<InduccionExamen> induccionesExamen) {
        this.induccionesExamen = induccionesExamen;
    }

    @Override
    public String toString() {
        return "PersonaCargoLaboral{" +
                "id=" + id +
                ", fechaIngreso=" + fechaIngreso +
                ", fechaFirmaContrato=" + fechaFirmaContrato +
                ", mesesExperiencia=" + mesesExperiencia +
                '}';
    }
}
