package com.miapp.MiHoja.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "campo_base_meta")
public class CampoBaseMeta {

    @Id
    @Column(name = "nombre_campo", nullable = false)
    private String nombreCampo;

    @Column(name = "etiqueta")
    private String etiqueta;

    @Column(name = "tipo_dato", nullable = false)
    private String tipoDato = "texto";

    @Column(name = "orden_mostrar")
    private Integer ordenMostrar;

    @Column(name = "oculto", nullable = false)
    private Boolean oculto = false;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn = LocalDateTime.now();

    public String getNombreCampo() { return nombreCampo; }
    public void setNombreCampo(String nombreCampo) { this.nombreCampo = nombreCampo; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public String getTipoDato() { return tipoDato; }
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }

    public Integer getOrdenMostrar() { return ordenMostrar; }
    public void setOrdenMostrar(Integer ordenMostrar) { this.ordenMostrar = ordenMostrar; }

    public Boolean getOculto() { return oculto; }
    public void setOculto(Boolean oculto) { this.oculto = oculto; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
