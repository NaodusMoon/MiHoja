package com.miapp.MiHoja.api.service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.miapp.MiHoja.api.dto.DashboardActionResponse;
import com.miapp.MiHoja.api.dto.DashboardCleanupResponse;
import com.miapp.MiHoja.api.dto.DashboardFilterOptionsResponse;
import com.miapp.MiHoja.api.dto.DashboardMetricResponse;
import com.miapp.MiHoja.api.dto.DashboardOverviewResponse;
import com.miapp.MiHoja.api.dto.DashboardPeopleResponse;
import com.miapp.MiHoja.api.dto.DashboardPersonCardResponse;
import com.miapp.MiHoja.api.mapper.DashboardPersonMapper;
import com.miapp.MiHoja.dto.PersonaConCargo;
import com.miapp.MiHoja.service.PersonaService;
import com.miapp.MiHoja.service.query.PersonaConsultaService;

@Service
public class DashboardApiService {

    private static final List<String> SEXO_OPTIONS = List.of("M", "F");

    private final PersonaConsultaService personaConsultaService;
    private final PersonaService personaService;
    private final DashboardPersonMapper dashboardPersonMapper;

    public DashboardApiService(
            PersonaConsultaService personaConsultaService,
            PersonaService personaService,
            DashboardPersonMapper dashboardPersonMapper
    ) {
        this.personaConsultaService = personaConsultaService;
        this.personaService = personaService;
        this.dashboardPersonMapper = dashboardPersonMapper;
    }

    public DashboardOverviewResponse getOverview() {
        DashboardPeopleResponse peopleResponse = getPeople(new DashboardPeopleQuery(
                "",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                "number-asc",
                1,
                8
        ));

        List<String> highlights = List.of(
                "Consulta en vivo contra Spring Boot con filtros del backend",
                "Mantenimiento de duplicados y eliminacion multiple desde el dashboard",
                "Panel Next.js animado con datos reales y paginacion"
        );

        return new DashboardOverviewResponse(peopleResponse.metrics(), peopleResponse.people(), highlights);
    }

    public DashboardPeopleResponse getPeople(DashboardPeopleQuery query) {
        PersonaConsultaService.ConsultaResultado resultado = personaConsultaService.consultar(
                new PersonaConsultaService.PersonaFiltro(
                        null,
                        null,
                        null,
                        defaultList(query.lugarExpedicion()),
                        null,
                        firstOrNull(query.sexo()),
                        null,
                        null,
                        null,
                        defaultList(query.formacion()),
                        List.of(),
                        defaultList(query.cargo()),
                        defaultList(query.dependencia()),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        List<PersonaConCargo> filtered = applyFreeText(resultado.personas(), query.query());
        List<PersonaConCargo> sorted = sortPeople(filtered, query.sortBy());
        DashboardFilterOptionsResponse filterOptions = buildFilterOptions(resultado.personas());

        int page = Math.max(query.page(), 1);
        int size = Math.max(query.size(), 1);
        int totalPages = Math.max(1, (int) Math.ceil((double) sorted.size() / size));
        int safePage = Math.min(page, totalPages);
        int fromIndex = Math.min((safePage - 1) * size, sorted.size());
        int toIndex = Math.min(fromIndex + size, sorted.size());

        List<DashboardPersonCardResponse> people = sorted.subList(fromIndex, toIndex).stream()
                .map(dashboardPersonMapper::toCard)
                .toList();

        long duplicateCount = countDuplicates(filtered);
        int activeFilterCount = countActiveFilters(query);
        List<DashboardMetricResponse> metrics = List.of(
                new DashboardMetricResponse("visible", "Visibles", Integer.toString(filtered.size()), "positive"),
                new DashboardMetricResponse("selected", "Seleccionadas", "0", "neutral"),
                new DashboardMetricResponse("filters", "Filtros activos", Integer.toString(activeFilterCount), "accent"),
                new DashboardMetricResponse("duplicates", "Duplicados", Long.toString(duplicateCount), "warning")
        );

        return new DashboardPeopleResponse(
                people,
                metrics,
                filterOptions,
                filtered.size(),
                safePage,
                size,
                totalPages,
                duplicateCount,
                activeFilterCount,
                defaultText(query.query())
        );
    }

    public DashboardActionResponse deletePeople(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new DashboardActionResponse("No se recibieron registros para eliminar.", 0, List.of());
        }

        PersonaService.DeletionSummary summary = personaService.eliminarVariosConResumen(ids);
        if (summary.getEliminados() > 0) {
            personaService.reordenarNumeros();
        }

        String message = summary.getFallidos().isEmpty()
                ? "Registros eliminados correctamente."
                : "Eliminacion parcial completada.";

        return new DashboardActionResponse(message, summary.getEliminados(), summary.getFallidos());
    }

    public DashboardCleanupResponse cleanupDuplicates() {
        PersonaService.CleanupSummary summary = personaService.limpiarDuplicadosExistentes();
        String message = String.format(
                "Limpieza completada. Personas revisadas: %d, alergias eliminadas: %d, medicamentos eliminados: %d, enfermedades eliminadas: %d",
                summary.getPersonasRevisadas(),
                summary.getAlergiasEliminadas(),
                summary.getMedicamentosEliminados(),
                summary.getEnfermedadesEliminadas()
        );

        return new DashboardCleanupResponse(
                message,
                summary.getPersonasRevisadas(),
                summary.getAlergiasEliminadas(),
                summary.getMedicamentosEliminados(),
                summary.getEnfermedadesEliminadas()
        );
    }

    private DashboardFilterOptionsResponse buildFilterOptions(List<PersonaConCargo> people) {
        return new DashboardFilterOptionsResponse(
                SEXO_OPTIONS,
                extractOptions(people, PersonaConCargo::getLugarExpedicion),
                extractOptions(people, PersonaConCargo::getFormacion),
                extractOptions(people, PersonaConCargo::getDependencia),
                extractOptions(people, PersonaConCargo::getCargo)
        );
    }

    private List<PersonaConCargo> applyFreeText(List<PersonaConCargo> people, String query) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isBlank()) {
            return people;
        }

        return people.stream()
                .filter(person -> normalize(String.join(" ",
                        defaultText(person.getNombres()),
                        defaultText(person.getApellidos()),
                        defaultText(person.getCedula()),
                        defaultText(person.getCargo()),
                        defaultText(person.getDependencia()),
                        defaultText(person.getLugarExpedicion())
                )).contains(normalizedQuery))
                .toList();
    }

    private List<PersonaConCargo> sortPeople(List<PersonaConCargo> people, String sortBy) {
        Comparator<PersonaConCargo> comparator = switch (defaultText(sortBy)) {
            case "name-desc" -> Comparator.comparing(this::formatName, String.CASE_INSENSITIVE_ORDER).reversed();
            case "number-asc" -> Comparator.comparing(person -> Optional.ofNullable(person.getNumero()).orElse(Integer.MAX_VALUE));
            default -> Comparator.comparing(this::formatName, String.CASE_INSENSITIVE_ORDER);
        };

        return people.stream().sorted(comparator).toList();
    }

    private long countDuplicates(List<PersonaConCargo> people) {
        return people.stream()
                .map(PersonaConCargo::getCedula)
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .values()
                .stream()
                .filter(count -> count > 1)
                .mapToLong(count -> count - 1)
                .sum();
    }

    private int countActiveFilters(DashboardPeopleQuery query) {
        int count = 0;
        if (!defaultText(query.query()).isBlank()) {
            count++;
        }
        count += countIfHasValues(query.sexo());
        count += countIfHasValues(query.lugarExpedicion());
        count += countIfHasValues(query.formacion());
        count += countIfHasValues(query.dependencia());
        count += countIfHasValues(query.cargo());
        return count;
    }

    private int countIfHasValues(List<String> values) {
        return values == null || values.isEmpty() ? 0 : 1;
    }

    private List<String> extractOptions(List<PersonaConCargo> people, Function<PersonaConCargo, String> extractor) {
        return people.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private String formatName(PersonaConCargo person) {
        return (defaultText(person.getApellidos()) + " " + defaultText(person.getNombres())).trim();
    }

    private List<String> defaultList(List<String> values) {
        return values == null ? List.of() : values;
    }

    private String firstOrNull(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    private String defaultText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalize(String value) {
        return Normalizer.normalize(defaultText(value), Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase();
    }
}
