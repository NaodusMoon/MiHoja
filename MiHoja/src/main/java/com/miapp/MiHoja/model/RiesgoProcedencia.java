package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "riesgo_procedencia")
public class RiesgoProcedencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_riesgo")
    private Long idRiesgo;

    @Column(name = "riesgo")
    private String riesgo;

    @Column(name = "medio_transporte")
    private String medioTransporte;

    @Column(name = "procedencia_trabajador")
    private String procedenciaTrabajador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n", referencedColumnName = "n")
    @JsonIgnore
    private Persona persona;

    // Constructor vacío
    public RiesgoProcedencia() {}

    // Constructor útil
    public RiesgoProcedencia(String riesgo, String medioTransporte, String procedenciaTrabajador) {
        this.riesgo = riesgo;
        this.medioTransporte = medioTransporte;
        this.procedenciaTrabajador = procedenciaTrabajador;
    }

    // Getters y setters

    public Long getIdRiesgo() {
        return idRiesgo;
    }

    public void setIdRiesgo(Long idRiesgo) {
        this.idRiesgo = idRiesgo;
    }

    public String getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(String riesgo) {
        this.riesgo = riesgo;
    }

    public String getMedioTransporte() {
        return medioTransporte;
    }

    public void setMedioTransporte(String medioTransporte) {
        this.medioTransporte = medioTransporte;
    }

    public String getProcedenciaTrabajador() {
        return procedenciaTrabajador;
    }

    public void setProcedenciaTrabajador(String procedenciaTrabajador) {
        this.procedenciaTrabajador = procedenciaTrabajador;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    @Override
    public String toString() {
        return "RiesgoProcedencia{" +
                "idRiesgo=" + idRiesgo +
                ", riesgo='" + riesgo + '\'' +
                ", medioTransporte='" + medioTransporte + '\'' +
                ", procedenciaTrabajador='" + procedenciaTrabajador + '\'' +
                '}';
    }
}
