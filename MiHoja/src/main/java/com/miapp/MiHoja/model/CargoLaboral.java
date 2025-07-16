package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "cargo_laboral")
public class CargoLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cargo")
    private Long id;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "dependencia")
    private String dependencia;

    // Relación con tabla intermedia (muchos a muchos con persona)
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PersonaCargoLaboral> personaCargos;

    public CargoLaboral() {}

    public CargoLaboral(String cargo, String codigo, String dependencia) {
        this.cargo = cargo;
        this.codigo = codigo;
        this.dependencia = dependencia;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public List<PersonaCargoLaboral> getPersonaCargos() {
        return personaCargos;
    }

    public void setPersonaCargos(List<PersonaCargoLaboral> personaCargos) {
        this.personaCargos = personaCargos;
    }

    @Override
    public String toString() {
        return "CargoLaboral{" +
                "id=" + id +
                ", cargo='" + cargo + '\'' +
                ", codigo='" + codigo + '\'' +
                ", dependencia='" + dependencia + '\'' +
                '}';
    }
}
