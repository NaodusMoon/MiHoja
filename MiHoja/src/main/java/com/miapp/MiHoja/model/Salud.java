package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "salud")
public class Salud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salud")
    private Long idSalud;

    @Column(name = "dotacion")
    private String dotacion;

    @Column(name = "arl")
    private String arl;

    @Column(name = "eps")
    private String eps;

    @Column(name = "afp")
    private String afp;

    @Column(name = "ccf")
    private String ccf;

    @Column(name = "rh")
    private String rh;

    @Column(name = "carnet_vacunacion")
    private Boolean carnetVacunacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n", referencedColumnName = "n")
    @JsonIgnore
    private Persona persona;

    // Constructor vacío
    public Salud() {}

    // Constructor útil para pruebas o inicialización
    public Salud(String arl, String eps, String afp, String ccf, String rh, Boolean carnetVacunacion) {
        this.arl = arl;
        this.eps = eps;
        this.afp = afp;
        this.ccf = ccf;
        this.rh = rh;
        this.carnetVacunacion = carnetVacunacion;
    }

    // Getters y setters

    public Long getIdSalud() {
        return idSalud;
    }

    public void setIdSalud(Long idSalud) {
        this.idSalud = idSalud;
    }

    public String getDotacion() {
        return dotacion;
    }

    public void setDotacion(String dotacion) {
        this.dotacion = dotacion;
    }

    public String getArl() {
        return arl;
    }

    public void setArl(String arl) {
        this.arl = arl;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getAfp() {
        return afp;
    }

    public void setAfp(String afp) {
        this.afp = afp;
    }

    public String getCcf() {
        return ccf;
    }

    public void setCcf(String ccf) {
        this.ccf = ccf;
    }

    public String getRh() {
        return rh;
    }

    public void setRh(String rh) {
        this.rh = rh;
    }

    public Boolean getCarnetVacunacion() {
        return carnetVacunacion;
    }

    public void setCarnetVacunacion(Boolean carnetVacunacion) {
        this.carnetVacunacion = carnetVacunacion;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    @Override
    public String toString() {
        return "Salud{" +
                "idSalud=" + idSalud +
                ", dotacion='" + dotacion + '\'' +
                ", arl='" + arl + '\'' +
                ", eps='" + eps + '\'' +
                ", afp='" + afp + '\'' +
                ", ccf='" + ccf + '\'' +
                ", rh='" + rh + '\'' +
                ", carnetVacunacion=" + carnetVacunacion +
                '}';
    }
}
