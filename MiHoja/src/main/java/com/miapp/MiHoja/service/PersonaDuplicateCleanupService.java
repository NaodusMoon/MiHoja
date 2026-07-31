package com.miapp.MiHoja.service;

import com.miapp.MiHoja.model.Alergia;
import com.miapp.MiHoja.model.Enfermedad;
import com.miapp.MiHoja.model.Medicamento;
import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.repository.AlergiaRepository;
import com.miapp.MiHoja.repository.EnfermedadRepository;
import com.miapp.MiHoja.repository.MedicamentoRepository;
import com.miapp.MiHoja.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PersonaDuplicateCleanupService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private AlergiaRepository alergiaRepository;

    @Autowired
    private EnfermedadRepository enfermedadRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Transactional
    public PersonaService.CleanupSummary limpiarDuplicadosExistentes() {
        PersonaService.CleanupSummary summary = new PersonaService.CleanupSummary();
        List<Persona> personas = personaRepository.findAllWithAllRelations();

        for (Persona persona : personas) {
            summary.incrementarPersonas();

            if (persona.getAlergias() != null && !persona.getAlergias().isEmpty()) {
                Map<String, Alergia> alergiasCanonicas = new HashMap<>();
                List<Alergia> ordenadas = persona.getAlergias().stream()
                        .sorted(Comparator.comparing(Alergia::getId, Comparator.nullsLast(Long::compareTo)))
                        .toList();
                for (Alergia actual : ordenadas) {
                    String key = normalizarClave(actual.getNombre());
                    if (key.isBlank()) continue;
                    Alergia canonica = alergiasCanonicas.get(key);
                    if (canonica == null) {
                        alergiasCanonicas.put(key, actual);
                        continue;
                    }
                    persona.getAlergias().remove(actual);
                    alergiaRepository.delete(actual);
                    summary.incrementarAlergias();
                }
            }

            if (persona.getEnfermedades() != null && !persona.getEnfermedades().isEmpty()) {
                Map<String, Enfermedad> enfermedadesCanonicas = new HashMap<>();
                List<Enfermedad> ordenadas = persona.getEnfermedades().stream()
                        .sorted(Comparator.comparing(Enfermedad::getId, Comparator.nullsLast(Long::compareTo)))
                        .toList();
                for (Enfermedad actual : ordenadas) {
                    String key = normalizarClave(actual.getNombre());
                    if (key.isBlank()) continue;
                    Enfermedad canonica = enfermedadesCanonicas.get(key);
                    if (canonica == null) {
                        enfermedadesCanonicas.put(key, actual);
                        continue;
                    }

                    if (actual.getMedicamentos() != null) {
                        for (Medicamento med : new HashSet<>(actual.getMedicamentos())) {
                            canonica.addMedicamento(med);
                        }
                        actual.getMedicamentos().clear();
                    }

                    persona.getEnfermedades().remove(actual);
                    enfermedadRepository.delete(actual);
                    summary.incrementarEnfermedades();
                }
            }

            if (persona.getMedicamentos() != null && !persona.getMedicamentos().isEmpty()) {
                Map<String, Medicamento> medicamentosCanonicos = new HashMap<>();
                List<Medicamento> ordenados = persona.getMedicamentos().stream()
                        .sorted(Comparator.comparing(Medicamento::getId, Comparator.nullsLast(Long::compareTo)))
                        .toList();
                for (Medicamento actual : ordenados) {
                    String key = normalizarClave(actual.getNombre());
                    if (key.isBlank()) continue;
                    Medicamento canonico = medicamentosCanonicos.get(key);
                    if (canonico == null) {
                        medicamentosCanonicos.put(key, actual);
                        continue;
                    }

                    if (actual.getEnfermedades() != null) {
                        for (Enfermedad enf : new HashSet<>(actual.getEnfermedades())) {
                            canonico.addEnfermedad(enf);
                        }
                        actual.getEnfermedades().clear();
                    }

                    persona.getMedicamentos().remove(actual);
                    medicamentoRepository.delete(actual);
                    summary.incrementarMedicamentos();
                }
            }
        }

        return summary;
    }

    private String normalizarClave(String valor) {
        if (valor == null) return "";
        return valor.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
