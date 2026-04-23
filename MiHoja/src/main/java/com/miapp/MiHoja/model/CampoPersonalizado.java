package com.miapp.MiHoja.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "campo_personalizado", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nombre")
})
public class CampoPersonalizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campo")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Transient
    private String etiqueta;

    @Transient
    private String tipoDato = "texto";

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public String getTipoDato() { return tipoDato; }
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    @Transient
    public String getEtiquetaVisible() {
        if (etiqueta != null && !etiqueta.isBlank()) {
            return etiqueta.trim();
        }
        if (nombre == null || nombre.isBlank()) {
            return "Campo";
        }
        return nombre.replace("_", " ").trim();
    }

    @Transient
    public String getTipoDatoNormalizado() {
        if (tipoDato == null || tipoDato.isBlank()) {
            return "texto";
        }
        return tipoDato.trim().toLowerCase();
    }
}
