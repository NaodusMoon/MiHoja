package com.miapp.MiHoja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "persona_campo_valor", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"persona_id", "campo_id"})
})
public class PersonaCampoValor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valor")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    @JsonIgnore
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campo_id", nullable = false)
    private CampoPersonalizado campo;

    @Column(name = "valor", nullable = false, length = 2000)
    private String valor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public CampoPersonalizado getCampo() { return campo; }
    public void setCampo(CampoPersonalizado campo) { this.campo = campo; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}
