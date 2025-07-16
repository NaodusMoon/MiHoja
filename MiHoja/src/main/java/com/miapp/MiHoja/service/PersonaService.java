package com.miapp.MiHoja.service;

import com.miapp.MiHoja.model.*;
import com.miapp.MiHoja.repository.MedicamentoRepository;
import com.miapp.MiHoja.repository.PersonaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public void eliminarVarios(List<Long> ids) {
        personaRepository.deleteAllById(ids);
    }

    // Guardar una persona con número si no tiene
    @Transactional
    public Persona guardarConNumero(Persona persona) {
        if (persona.getNumero() == null || persona.getNumero() <= 0) {
            persona.setNumero(obtenerSiguienteNumeroSinHuecos());
        }
        return personaRepository.save(persona);
    }

    // Guardar otras entidades individuales
    @Transactional public void guardarFormacion(Formacion f) { entityManager.persist(f); }
    @Transactional public void guardarPersonaCargo(PersonaCargoLaboral pcl) { entityManager.persist(pcl); }
    @Transactional public void guardarInduccion(InduccionExamen ie) { entityManager.persist(ie); }
    @Transactional public void guardarRiesgo(RiesgoProcedencia r) { entityManager.persist(r); }
    @Transactional public void guardarSalud(Salud s) { entityManager.persist(s); }
    @Transactional public void guardarContactoEmergencia(ContactoEmergencia c) { entityManager.persist(c); }
    @Transactional public void guardarEnfermedad(Enfermedad e) { entityManager.persist(e); }
    @Transactional public void guardarAlergia(Alergia a) { entityManager.persist(a); }

        // Buscar medicamentos asociados a una persona (vía enfermedades)
    @Transactional(readOnly = true)
    public List<Medicamento> obtenerMedicamentosPorPersona(Persona persona) {
        return medicamentoRepository.findByPersonaRelacionada(persona);
    }

    // Guardar o buscar medicamento por nombre y asociar persona como propietario (solo si aplica)
    @Transactional
    public Medicamento obtenerOCrearMedicamento(String nombre, Persona persona) {
        List<Medicamento> medicamentosRelacionados = medicamentoRepository.findByPersonaRelacionada(persona);

        for (Medicamento med : medicamentosRelacionados) {
            if (med.getNombre().equalsIgnoreCase(nombre)) {
                return med;
            }
        }

        Medicamento nuevo = new Medicamento();
        nuevo.setNombre(nombre);
        nuevo.setPersona(persona);
        entityManager.persist(nuevo);
        return nuevo;
    }

    // Relacionar medicamento con enfermedad (bidireccional con persistencia)
    @Transactional
    public void asociarMedicamentoAEnfermedad(Medicamento medicamento, Enfermedad enfermedad) {
        enfermedad.getMedicamentos().add(medicamento);
        medicamento.getEnfermedades().add(enfermedad);

        if (enfermedad.getId() == null) {
            entityManager.persist(enfermedad);
        } else {
            entityManager.merge(enfermedad);
        }
    }

    // Guardar enfermedad y asociar medicamentos
    @Transactional
    public void guardarEnfermedadConMedicamentos(Enfermedad enfermedad, List<String> nombresMedicamentos, Persona persona) {
        enfermedad.setPersona(persona);

        if (enfermedad.getId() == null) {
            entityManager.persist(enfermedad);
        } else {
            entityManager.merge(enfermedad);
        }

        Set<String> nombresUnicos = new HashSet<>(nombresMedicamentos);

        for (String nombreMed : nombresUnicos) {
            Medicamento medicamento = obtenerOCrearMedicamento(nombreMed, persona);
            asociarMedicamentoAEnfermedad(medicamento, enfermedad);
        }
    }

    // Obtener o crear cargo laboral
    @Transactional
    public CargoLaboral obtenerOCrearCargo(String cargo, String codigo, String dependencia) {
        List<CargoLaboral> lista = entityManager
                .createQuery("SELECT c FROM CargoLaboral c WHERE LOWER(c.cargo) = LOWER(:cargo) " +
                             "AND LOWER(c.codigo) = LOWER(:codigo) " +
                             "AND LOWER(c.dependencia) = LOWER(:dependencia)", CargoLaboral.class)
                .setParameter("cargo", cargo)
                .setParameter("codigo", codigo)
                .setParameter("dependencia", dependencia)
                .getResultList();

        if (!lista.isEmpty()) return lista.get(0);

        CargoLaboral nuevo = new CargoLaboral();
        nuevo.setCargo(cargo);
        nuevo.setCodigo(codigo);
        nuevo.setDependencia(dependencia);
        entityManager.persist(nuevo);
        return nuevo;
    }

        // Obtener siguiente número sin huecos
    @Transactional(readOnly = true)
    public int obtenerSiguienteNumeroSinHuecos() {
        List<Integer> numeros = personaRepository.findAll().stream()
                .map(Persona::getNumero)
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        int esperado = 1;
        for (int actual : numeros) {
            if (actual != esperado) break;
            esperado++;
        }
        return esperado;
    }

    // Eliminar persona y reorganizar números
    @Transactional
    public void eliminarPersonaYReordenar(Long id) {
        Persona persona = personaRepository.findById(id).orElse(null);
        if (persona != null) {
            Integer numeroEliminado = persona.getNumero();
            personaRepository.deleteById(id);

            if (numeroEliminado != null && numeroEliminado > 0) {
                personaRepository.decrementarNumerosPosteriores(numeroEliminado);
            }
        }
    }

    // Reordenar todos los números
    @Transactional
    public void reordenarNumeros() {
        List<Persona> personas = personaRepository.findAll().stream()
                .sorted(Comparator.comparing(Persona::getNumero, Comparator.nullsLast(Integer::compareTo)))
                .toList();

        int nuevoNumero = 1;
        boolean cambios = false;

        for (Persona persona : personas) {
            if (persona.getNumero() == null || !persona.getNumero().equals(nuevoNumero)) {
                persona.setNumero(nuevoNumero);
                cambios = true;
            }
            nuevoNumero++;
        }

        if (cambios) {
            personaRepository.saveAll(personas);
        }
    }

    // Obtener persona con todas las relaciones
    @Transactional(readOnly = true)
    public Persona obtenerPersonaConRelaciones(Long id) {
        return personaRepository.findByIdWithAllRelations(id).orElse(null);
    }

    // Búsquedas simples
    public List<Persona> buscarPorCedula(String cedula) {
        return personaRepository.findByCedula(cedula);
    }

    public List<Persona> buscarPorCorreoInstitucional(String correo) {
        return personaRepository.findByCorreoInstitucional(correo);
    }

    public List<Persona> buscarPorNombreCompleto(String nombres, String apellidos) {
        return personaRepository.findByNombresContainingIgnoreCaseAndApellidosContainingIgnoreCase(nombres, apellidos);
    }

        // ✅ Filtro múltiple avanzado
    public List<Persona> filtrarPersonas(String nombre,
                                         List<String> formaciones,
                                         List<String> grados,
                                         List<String> cargos,
                                         List<String> dependencias,
                                         List<String> rh,
                                         List<String> eps,
                                         List<String> afp,
                                         List<String> carnetVacunacion,
                                         List<String> riesgos,
                                         List<String> medioTransporte,
                                         List<String> procedencias,
                                         List<String> inducciones,
                                         List<String> examenes,
                                         List<String> mesesExperiencia,
                                         List<String> dotacion) {

        List<Persona> personas = personaRepository.findAll();

        return personas.stream()

            // ✅ Filtro por nombre completo
            .filter(p -> nombre == null || nombre.isBlank() ||
                    (p.getNombres() + " " + p.getApellidos())
                            .toLowerCase()
                            .contains(nombre.toLowerCase()))

            // ✅ Filtros por formaciones y grado
            .filter(p -> formaciones == null || formaciones.isEmpty() ||
                    p.getFormaciones().stream()
                            .anyMatch(f -> f.getFormacionAcademica() != null &&
                                           formaciones.contains(f.getFormacionAcademica())))
            .filter(p -> grados == null || grados.isEmpty() ||
                    p.getFormaciones().stream()
                            .anyMatch(f -> f.getGrado() != null &&
                                           grados.contains(f.getGrado())))

            // ✅ Filtros por cargos y dependencias
            .filter(p -> cargos == null || cargos.isEmpty() ||
                    p.getCargosLaborales().stream()
                            .anyMatch(c -> c.getCargo() != null &&
                                           cargos.contains(c.getCargo().getCargo())))
            .filter(p -> dependencias == null || dependencias.isEmpty() ||
                    p.getCargosLaborales().stream()
                            .anyMatch(c -> c.getCargo() != null &&
                                           dependencias.contains(c.getCargo().getDependencia())))

            // ✅ Filtros en SALUD
            .filter(p -> rh == null || rh.isEmpty() ||
                    p.getRegistrosSalud().stream()
                            .anyMatch(s -> s.getRh() != null && rh.contains(s.getRh())))
            .filter(p -> eps == null || eps.isEmpty() ||
                    p.getRegistrosSalud().stream()
                            .anyMatch(s -> s.getEps() != null && eps.contains(s.getEps())))
            .filter(p -> afp == null || afp.isEmpty() ||
                    p.getRegistrosSalud().stream()
                            .anyMatch(s -> s.getAfp() != null && afp.contains(s.getAfp())))
            .filter(p -> carnetVacunacion == null || carnetVacunacion.isEmpty() ||
                    p.getRegistrosSalud().stream()
                            .anyMatch(s -> s.getCarnetVacunacion() != null &&
                                           carnetVacunacion.contains(s.getCarnetVacunacion().toString())))
            .filter(p -> dotacion == null || dotacion.isEmpty() ||
                    p.getRegistrosSalud().stream()
                            .anyMatch(s -> s.getDotacion() != null && dotacion.contains(s.getDotacion())))

            // ✅ Filtro por RIESGO_PROCEDENCIA
            .filter(p -> medioTransporte == null || medioTransporte.isEmpty() ||
                    p.getRiesgoProcedencias().stream()
                            .anyMatch(r -> r.getMedioTransporte() != null &&
                                           medioTransporte.contains(r.getMedioTransporte())))
            .filter(p -> procedencias == null || procedencias.isEmpty() ||
                    p.getRiesgoProcedencias().stream()
                            .anyMatch(r -> r.getProcedenciaTrabajador() != null &&
                                           procedencias.contains(r.getProcedenciaTrabajador())))
            .filter(p -> riesgos == null || riesgos.isEmpty() ||
                    p.getRiesgoProcedencias().stream()
                            .anyMatch(r -> r.getRiesgo() != null && riesgos.contains(r.getRiesgo())))

            // ✅ Filtro por PERSONA_CARGO_LABORAL (meses experiencia)
            .filter(p -> mesesExperiencia == null || mesesExperiencia.isEmpty() ||
                    p.getCargosLaborales().stream()
                            .anyMatch(c -> c.getMesesExperiencia() != null &&
                                           mesesExperiencia.contains(c.getMesesExperiencia().toString())))

            // ✅ Filtro por INDUCCION_EXAMEN
            .filter(p -> inducciones == null || inducciones.isEmpty() ||
                    p.getCargosLaborales().stream()
                            .flatMap(c -> c.getInduccionesExamen().stream())
                            .anyMatch(i -> i.getInduccion() != null &&
                                           i.getInduccion() &&
                                           inducciones.contains("SI")))
            .filter(p -> examenes == null || examenes.isEmpty() ||
                    p.getCargosLaborales().stream()
                            .flatMap(c -> c.getInduccionesExamen().stream())
                            .anyMatch(i -> i.getExamenIngreso() != null &&
                                           i.getExamenIngreso() &&
                                           examenes.contains("SI")))

            .collect(Collectors.toList());
    }
}
