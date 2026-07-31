package com.miapp.MiHoja.service.maintenance;

import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PersonaNumberingService {

    private final PersonaRepository personaRepository;

    public PersonaNumberingService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Transactional(readOnly = true)
    public int obtenerSiguienteNumeroSinHuecos() {
        List<Integer> numeros = personaRepository.findAll().stream()
                .map(Persona::getNumero)
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        int esperado = 1;
        for (int actual : numeros) {
            if (actual != esperado) {
                break;
            }
            esperado++;
        }
        return esperado;
    }

    @Transactional
    public void asignarNumerosFaltantes(List<Persona> personas) {
        if (personas == null || personas.isEmpty()) {
            return;
        }

        int siguiente = obtenerSiguienteNumeroSinHuecos();
        for (Persona persona : personas) {
            if (persona.getNumero() == null || persona.getNumero() <= 0) {
                persona.setNumero(siguiente++);
            }
        }
    }

    @Transactional
    public void reordenarNumeros() {
        List<Persona> personas = personaRepository.findAllByOrderByIdAsc();
        int numero = 1;
        for (Persona persona : personas) {
            persona.setNumero(numero++);
        }
        personaRepository.saveAll(personas);
    }

    @Transactional
    public void decrementarPosteriores(Integer numeroEliminado) {
        if (numeroEliminado != null && numeroEliminado > 0) {
            personaRepository.decrementarNumerosPosteriores(numeroEliminado);
        }
    }
}
