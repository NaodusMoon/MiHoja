package com.miapp.MiHoja.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "campo_personalizado_meta")
public class CampoPersonalizadoMeta {

    @Id
    @Column(name = "campo_id")
    private Long campoId;

    @Column(name = "etiqueta")
    private String etiqueta;

    @Column(name = "tipo_dato", nullable = false)
    private String tipoDato = "texto";

    @Column(name = "orden_mostrar")
    private Integer ordenMostrar;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn = LocalDateTime.now();

    public Long getCampoId() { return campoId; }
    public void setCampoId(Long campoId) { this.campoId = campoId; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public String getTipoDato() { return tipoDato; }
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }

    public Integer getOrdenMostrar() { return ordenMostrar; }
    public void setOrdenMostrar(Integer ordenMostrar) { this.ordenMostrar = ordenMostrar; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
