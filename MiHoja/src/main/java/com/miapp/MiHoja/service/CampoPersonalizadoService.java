package com.miapp.MiHoja.service;

import com.miapp.MiHoja.model.CampoPersonalizado;
import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.model.PersonaCampoValor;
import com.miapp.MiHoja.repository.CampoPersonalizadoRepository;
import com.miapp.MiHoja.repository.PersonaCampoValorRepository;
import com.miapp.MiHoja.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CampoPersonalizadoService {

    @Autowired
    private CampoPersonalizadoRepository campoRepository;

    @Autowired
    private PersonaCampoValorRepository valorRepository;

    @Autowired
    private PersonaRepository personaRepository;

    private String limpio(String value) {
        if (value == null) return "NO DISPONIBLE";
        String out = value.trim();
        return out.isEmpty() ? "NO DISPONIBLE" : out;
    }

    @Transactional(readOnly = true)
    public List<CampoPersonalizado> listarActivos() {
        return campoRepository.findByActivoTrueOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<CampoPersonalizado> listarTodos() {
        return campoRepository.findAllByOrderByNombreAsc();
    }

    @Transactional
    public CampoPersonalizado crearCampo(String nombre) {
        String finalNombre = limpio(nombre);
        CampoPersonalizado campo = campoRepository.findFirstByNombreIgnoreCase(finalNombre)
                .orElseGet(CampoPersonalizado::new);
        campo.setNombre(finalNombre);
        campo.setActivo(true);
        return campoRepository.save(campo);
    }

    @Transactional
    public void desactivarCampo(Long campoId) {
        CampoPersonalizado campo = campoRepository.findById(campoId)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));
        campo.setActivo(false);
        campoRepository.save(campo);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> mapaValoresPorPersona(Long personaId) {
        Map<Long, String> mapa = new HashMap<>();
        for (PersonaCampoValor valor : valorRepository.findByPersonaId(personaId)) {
            mapa.put(valor.getCampo().getId(), limpio(valor.getValor()));
        }
        return mapa;
    }

    @Transactional(readOnly = true)
    public Map<String, String> mapaValoresPorNombre(Long personaId) {
        Map<String, String> mapa = new LinkedHashMap<>();
        for (PersonaCampoValor valor : valorRepository.findByPersonaId(personaId)) {
            mapa.put(valor.getCampo().getNombre(), limpio(valor.getValor()));
        }
        return mapa;
    }

    @Transactional
    public void guardarValoresDesdeFormulario(Long personaId, Map<String, String> params) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        for (CampoPersonalizado campo : listarActivos()) {
            String key = "campo_custom_" + campo.getId();
            String valor = limpio(params.get(key));
            PersonaCampoValor row = valorRepository.findFirstByPersonaIdAndCampoId(personaId, campo.getId())
                    .orElseGet(PersonaCampoValor::new);
            row.setPersona(persona);
            row.setCampo(campo);
            row.setValor(valor);
            valorRepository.save(row);
        }
    }

    @Transactional
    public void guardarValorPorNombre(Persona persona, String nombreCampo, String valor) {
        if (persona == null || persona.getId() == null || nombreCampo == null) return;
        Optional<CampoPersonalizado> campoOpt = campoRepository.findFirstByNombreIgnoreCase(nombreCampo);
        if (campoOpt.isEmpty()) return;

        CampoPersonalizado campo = campoOpt.get();
        if (!Boolean.TRUE.equals(campo.getActivo())) return;

        PersonaCampoValor row = valorRepository.findFirstByPersonaIdAndCampoId(persona.getId(), campo.getId())
                .orElseGet(PersonaCampoValor::new);
        row.setPersona(persona);
        row.setCampo(campo);
        row.setValor(limpio(valor));
        valorRepository.save(row);
    }
}
