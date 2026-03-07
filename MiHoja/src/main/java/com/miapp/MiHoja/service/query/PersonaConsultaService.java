package com.miapp.MiHoja.service.query;

import com.miapp.MiHoja.dto.PersonaConCargo;
import com.miapp.MiHoja.repository.PersonaRepository;
import com.miapp.MiHoja.service.support.QueryStringService;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PersonaConsultaService {

    private final PersonaRepository personaRepository;
    private final QueryStringService queryStringService;

    public PersonaConsultaService(PersonaRepository personaRepository, QueryStringService queryStringService) {
        this.personaRepository = personaRepository;
        this.queryStringService = queryStringService;
    }

    public ConsultaResultado consultar(PersonaFiltro filtro) {
        List<PersonaConCargo> personas = personaRepository.consultarPersonasConCargo();

        List<String> opcionesLugarExpedicion = extraerOpciones(personas, PersonaConCargo::getLugarExpedicion);
        List<String> opcionesProcedencia = extraerOpciones(personas, PersonaConCargo::getProcedencia);

        personas = aplicarFiltros(personas, filtro).stream()
                .sorted(Comparator.comparingInt(p -> Optional.ofNullable(p.getNumero()).orElse(999999)))
                .toList();

        Map<String, Object> filtrosActivos = construirFiltrosActivos(filtro);
        String filtrosQuery = queryStringService.buildFromObjects(filtrosActivos);

        return new ConsultaResultado(personas, opcionesLugarExpedicion, opcionesProcedencia, filtrosActivos, filtrosQuery);
    }

    private List<PersonaConCargo> aplicarFiltros(List<PersonaConCargo> personas, PersonaFiltro filtro) {
        List<PersonaConCargo> resultado = personas;

        resultado = filtrarTexto(resultado, filtro.nombre(), PersonaConCargo::getNombres, true);
        resultado = filtrarTexto(resultado, filtro.apellido(), PersonaConCargo::getApellidos, true);
        resultado = filtrarTextoExacto(resultado, filtro.cedula(), PersonaConCargo::getCedula);
        resultado = filtrarSeleccionMultipleExacta(resultado, filtro.lugarExpedicion(), PersonaConCargo::getLugarExpedicion);
        resultado = filtrarTexto(resultado, filtro.direccion(), PersonaConCargo::getDireccion, true);
        resultado = filtrarTextoExacto(resultado, filtro.sexo(), PersonaConCargo::getSexo);
        resultado = filtrarTextoExacto(resultado, filtro.correo(), PersonaConCargo::getCorreoInstitucional);
        resultado = filtrarTextoExacto(resultado, filtro.telefono(), PersonaConCargo::getTelefonoInstitucional);
        resultado = filtrarTexto(resultado, filtro.enlaceSigep(), PersonaConCargo::getEnlaceSigep, true);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.formacion(), PersonaConCargo::getFormacion);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.grado(), PersonaConCargo::getGrado);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.cargo(), PersonaConCargo::getCargo);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.dependencia(), PersonaConCargo::getDependencia);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.rh(), PersonaConCargo::getRh);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.eps(), PersonaConCargo::getEps);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.afp(), PersonaConCargo::getAfp);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.carnetVacunacion(), PersonaConCargo::getCarnetVacunacion);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.riesgo(), PersonaConCargo::getRiesgo);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.medioTransporte(), PersonaConCargo::getMedioTransporte);
        resultado = filtrarSeleccionMultipleExacta(resultado, filtro.procedencia(), PersonaConCargo::getProcedencia);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.induccion(), PersonaConCargo::getInduccion);
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.examen(), PersonaConCargo::getExamen);
        resultado = filtrarMesesExperiencia(resultado, filtro.mesesExperiencia());
        resultado = filtrarSeleccionMultipleContiene(resultado, filtro.dotacion(), PersonaConCargo::getDotacion);

        return resultado;
    }

    private Map<String, Object> construirFiltrosActivos(PersonaFiltro filtro) {
        Map<String, Object> filtros = queryStringService.newOrderedMap();
        putIfPresent(filtros, "nombre", filtro.nombre());
        putIfPresent(filtros, "apellido", filtro.apellido());
        putIfPresent(filtros, "cedula", filtro.cedula());
        putIfPresent(filtros, "lugarExpedicion", filtro.lugarExpedicion());
        putIfPresent(filtros, "direccion", filtro.direccion());
        putIfPresent(filtros, "sexo", filtro.sexo());
        putIfPresent(filtros, "correo", filtro.correo());
        putIfPresent(filtros, "telefono", filtro.telefono());
        putIfPresent(filtros, "enlaceSigep", filtro.enlaceSigep());
        putIfPresentJoined(filtros, "formacion", filtro.formacion());
        putIfPresentJoined(filtros, "grado", filtro.grado());
        putIfPresentJoined(filtros, "cargo", filtro.cargo());
        putIfPresentJoined(filtros, "dependencia", filtro.dependencia());
        putIfPresentJoined(filtros, "rh", filtro.rh());
        putIfPresentJoined(filtros, "eps", filtro.eps());
        putIfPresentJoined(filtros, "afp", filtro.afp());
        putIfPresentJoined(filtros, "carnetVacunacion", filtro.carnetVacunacion());
        putIfPresentJoined(filtros, "riesgo", filtro.riesgo());
        putIfPresentJoined(filtros, "medioTransporte", filtro.medioTransporte());
        putIfPresentJoined(filtros, "procedencia", filtro.procedencia());
        putIfPresentJoined(filtros, "induccion", filtro.induccion());
        putIfPresentJoined(filtros, "examen", filtro.examen());
        putIfPresentJoined(filtros, "mesesExperiencia", filtro.mesesExperiencia());
        putIfPresentJoined(filtros, "dotacion", filtro.dotacion());
        return filtros;
    }

    private void putIfPresent(Map<String, Object> filtros, String key, String value) {
        if (value != null && !value.isBlank()) {
            filtros.put(key, value);
        }
    }

    private void putIfPresent(Map<String, Object> filtros, String key, List<String> values) {
        if (values != null && !values.isEmpty()) {
            filtros.put(key, values);
        }
    }

    private void putIfPresentJoined(Map<String, Object> filtros, String key, List<String> values) {
        if (values != null && !values.isEmpty()) {
            filtros.put(key, String.join(",", values));
        }
    }

    private List<PersonaConCargo> filtrarTexto(List<PersonaConCargo> personas,
                                               String valor,
                                               Function<PersonaConCargo, String> extractor,
                                               boolean contiene) {
        if (valor == null || valor.isBlank()) {
            return personas;
        }
        String valorNormalizado = normalizar(valor);
        return personas.stream()
                .filter(persona -> {
                    String actual = extractor.apply(persona);
                    if (actual == null) {
                        return false;
                    }
                    String actualNormalizado = normalizar(actual);
                    return contiene ? actualNormalizado.contains(valorNormalizado) : actualNormalizado.equals(valorNormalizado);
                })
                .toList();
    }

    private List<PersonaConCargo> filtrarTextoExacto(List<PersonaConCargo> personas,
                                                     String valor,
                                                     Function<PersonaConCargo, String> extractor) {
        return filtrarTexto(personas, valor, extractor, false);
    }

    private List<PersonaConCargo> filtrarSeleccionMultipleContiene(List<PersonaConCargo> personas,
                                                                   List<String> valores,
                                                                   Function<PersonaConCargo, String> extractor) {
        if (valores == null || valores.isEmpty()) {
            return personas;
        }
        List<String> normalizados = valores.stream().map(this::normalizar).toList();
        return personas.stream()
                .filter(persona -> {
                    String actual = extractor.apply(persona);
                    if (actual == null) {
                        return false;
                    }
                    String normalizado = normalizar(actual);
                    return normalizados.stream().anyMatch(normalizado::contains);
                })
                .toList();
    }

    private List<PersonaConCargo> filtrarSeleccionMultipleExacta(List<PersonaConCargo> personas,
                                                                 List<String> valores,
                                                                 Function<PersonaConCargo, String> extractor) {
        if (valores == null || valores.isEmpty()) {
            return personas;
        }
        Set<String> normalizados = valores.stream().map(this::normalizar).collect(Collectors.toSet());
        return personas.stream()
                .filter(persona -> {
                    String actual = normalizar(extractor.apply(persona));
                    return !actual.isEmpty() && normalizados.contains(actual);
                })
                .toList();
    }

    private List<PersonaConCargo> filtrarMesesExperiencia(List<PersonaConCargo> personas, List<String> rangos) {
        if (rangos == null || rangos.isEmpty()) {
            return personas;
        }

        return personas.stream()
                .filter(persona -> {
                    if (persona.getMesesExperiencia() == null) {
                        return false;
                    }
                    int meses;
                    try {
                        meses = Integer.parseInt(persona.getMesesExperiencia().trim());
                    } catch (NumberFormatException exception) {
                        return false;
                    }
                    return rangos.stream().anyMatch(rango -> matchRango(meses, rango));
                })
                .toList();
    }

    private boolean matchRango(int meses, String rango) {
        return switch (rango) {
            case "0-12" -> meses >= 0 && meses <= 12;
            case "13-60" -> meses >= 13 && meses <= 60;
            case "61-120" -> meses >= 61 && meses <= 120;
            case "121+" -> meses >= 121;
            default -> false;
        };
    }

    private List<String> extraerOpciones(List<PersonaConCargo> personas, Function<PersonaConCargo, String> extractor) {
        return personas.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();
    }

    public record PersonaFiltro(
            String nombre,
            String apellido,
            String cedula,
            List<String> lugarExpedicion,
            String direccion,
            String sexo,
            String correo,
            String telefono,
            String enlaceSigep,
            List<String> formacion,
            List<String> grado,
            List<String> cargo,
            List<String> dependencia,
            List<String> rh,
            List<String> eps,
            List<String> afp,
            List<String> carnetVacunacion,
            List<String> riesgo,
            List<String> medioTransporte,
            List<String> procedencia,
            List<String> induccion,
            List<String> examen,
            List<String> mesesExperiencia,
            List<String> dotacion
    ) {}

    public record ConsultaResultado(
            List<PersonaConCargo> personas,
            List<String> opcionesLugarExpedicion,
            List<String> opcionesProcedencia,
            Map<String, Object> filtrosActivos,
            String filtrosQuery
    ) {}
}
