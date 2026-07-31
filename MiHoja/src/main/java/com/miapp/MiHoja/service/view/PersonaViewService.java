package com.miapp.MiHoja.service.view;

import com.miapp.MiHoja.model.Alergia;
import com.miapp.MiHoja.model.CargoLaboral;
import com.miapp.MiHoja.model.ContactoEmergencia;
import com.miapp.MiHoja.model.Enfermedad;
import com.miapp.MiHoja.model.Formacion;
import com.miapp.MiHoja.model.InduccionExamen;
import com.miapp.MiHoja.model.Medicamento;
import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.model.PersonaCargoLaboral;
import com.miapp.MiHoja.model.RiesgoProcedencia;
import com.miapp.MiHoja.model.Salud;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PersonaViewService {

    public PersonaDetalleView construirDetalle(Persona persona) {
        Set<Formacion> formaciones = safeSet(persona.getFormaciones());
        Set<PersonaCargoLaboral> cargos = safeSet(persona.getCargosLaborales());
        Set<Salud> salud = safeSet(persona.getRegistrosSalud());
        Set<RiesgoProcedencia> riesgos = safeSet(persona.getRiesgoProcedencias());
        Set<ContactoEmergencia> contactos = safeSet(persona.getContactosEmergencia());
        Set<Enfermedad> enfermedades = safeSet(persona.getEnfermedades());
        Set<Alergia> alergias = safeSet(persona.getAlergias());

        Formacion formacion = latest(formaciones, Formacion::getIdFormacion, value -> null);
        PersonaCargoLaboral personaCargo = latest(cargos, PersonaCargoLaboral::getId, PersonaCargoLaboral::getFechaIngreso);
        CargoLaboral cargoLaboral = personaCargo != null ? personaCargo.getCargo() : null;
        InduccionExamen induccionExamen = personaCargo == null
                ? null
                : latest(safeCollection(personaCargo.getInduccionesExamen()), InduccionExamen::getIdInduccion, InduccionExamen::getFechaEgreso);
        Salud saludActual = latest(salud, Salud::getIdSalud, value -> null);
        RiesgoProcedencia riesgoActual = latest(riesgos, RiesgoProcedencia::getIdRiesgo, value -> null);
        ContactoEmergencia contactoActual = latest(contactos, ContactoEmergencia::getIdContacto, value -> null);

        List<String> listaEnfermedades = enfermedades.stream()
                .map(Enfermedad::getNombre)
                .filter(this::hasText)
                .distinct()
                .toList();

        List<String> listaAlergias = alergias.stream()
                .map(Alergia::getNombre)
                .filter(this::hasText)
                .distinct()
                .toList();

        List<String> listaMedicamentos = Stream.concat(
                        safeSet(persona.getMedicamentos()).stream(),
                        enfermedades.stream()
                                .flatMap(enfermedad -> safeCollection(enfermedad.getMedicamentos()).stream()))
                .map(Medicamento::getNombre)
                .filter(this::hasText)
                .distinct()
                .toList();

        return new PersonaDetalleView(
                persona,
                formacion,
                personaCargo,
                cargoLaboral,
                induccionExamen,
                saludActual,
                riesgoActual,
                contactoActual,
                listaEnfermedades,
                listaAlergias,
                listaMedicamentos
        );
    }

    private <T> Set<T> safeSet(Set<T> values) {
        return values == null ? Collections.emptySet() : values;
    }

    private <T> Collection<T> safeCollection(Collection<T> values) {
        return values == null ? Collections.emptyList() : values;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private <T> T latest(Collection<T> items,
                         Function<T, Long> idExtractor,
                         Function<T, LocalDate> dateExtractor) {
        return items.stream()
                .max(Comparator
                        .comparing((T item) -> Optional.ofNullable(dateExtractor.apply(item)).orElse(LocalDate.MIN))
                        .thenComparing(item -> Optional.ofNullable(idExtractor.apply(item)).orElse(Long.MIN_VALUE)))
                .orElse(null);
    }
}
