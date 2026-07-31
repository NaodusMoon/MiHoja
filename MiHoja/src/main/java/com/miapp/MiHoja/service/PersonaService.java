package com.miapp.MiHoja.service;

import com.miapp.MiHoja.dto.PersonaCompletaDTO;
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
import com.miapp.MiHoja.repository.CargoLaboralRepository;
import com.miapp.MiHoja.repository.EnfermedadRepository;
import com.miapp.MiHoja.repository.MedicamentoRepository;
import com.miapp.MiHoja.repository.PersonaRepository;
import com.miapp.MiHoja.service.maintenance.PersonaBulkDeleteService;
import com.miapp.MiHoja.service.maintenance.PersonaNumberingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PersonaService {

    @Autowired
    private EnfermedadRepository enfermedadRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private CargoLaboralRepository cargoLaboralRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private PersonaNumberingService personaNumberingService;

    @Autowired
    private PersonaBulkDeleteService personaBulkDeleteService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void eliminarVarios(List<Long> ids) {
        eliminarVariosConResumen(ids);
    }

    public static class DeletionSummary {
        private int eliminados;
        private final List<Long> fallidos = new ArrayList<>();

        public int getEliminados() { return eliminados; }
        public void setEliminados(int eliminados) { this.eliminados = eliminados; }
        public List<Long> getFallidos() { return fallidos; }

        static DeletionSummary from(PersonaBulkDeleteService.DeletionSummary source) {
            DeletionSummary summary = new DeletionSummary();
            summary.setEliminados(source.getEliminados());
            summary.getFallidos().addAll(source.getFallidos());
            return summary;
        }
    }

    @Transactional
    public DeletionSummary eliminarVariosConResumen(List<Long> ids) {
        return DeletionSummary.from(personaBulkDeleteService.eliminarVariosConResumen(ids));
    }

    @Transactional
    public Persona guardarConNumero(Persona persona) {
        if (persona.getNumero() == null || persona.getNumero() <= 0) {
            persona.setNumero(personaNumberingService.obtenerSiguienteNumeroSinHuecos());
        }
        return personaRepository.save(persona);
    }

    @Transactional public void guardarFormacion(Formacion f) { entityManager.persist(f); }
    @Transactional public void guardarPersonaCargo(PersonaCargoLaboral pcl) { entityManager.persist(pcl); }
    @Transactional public void guardarInduccion(InduccionExamen ie) { entityManager.persist(ie); }
    @Transactional public void guardarRiesgo(RiesgoProcedencia r) { entityManager.persist(r); }
    @Transactional public void guardarSalud(Salud s) { entityManager.persist(s); }
    @Transactional public void guardarContactoEmergencia(ContactoEmergencia c) { entityManager.persist(c); }
    @Transactional public void guardarEnfermedad(Enfermedad e) { entityManager.persist(e); }
    @Transactional public void guardarAlergia(Alergia a) { entityManager.persist(a); }

    public Enfermedad obtenerOCrearEnfermedad(String nombreEnfermedad, Persona persona) {
        if (nombreEnfermedad == null || nombreEnfermedad.trim().isEmpty()) {
            return null;
        }

        Enfermedad enfermedadExistente = enfermedadRepository.findByNombre(nombreEnfermedad.trim());
        if (enfermedadExistente != null) {
            if (!persona.getEnfermedades().contains(enfermedadExistente)) {
                persona.getEnfermedades().add(enfermedadExistente);
            }
            return enfermedadExistente;
        }

        Enfermedad nuevaEnfermedad = new Enfermedad();
        nuevaEnfermedad.setNombre(nombreEnfermedad.trim());
        enfermedadRepository.save(nuevaEnfermedad);
        persona.getEnfermedades().add(nuevaEnfermedad);
        return nuevaEnfermedad;
    }

    @Transactional(readOnly = true)
    public List<Medicamento> obtenerMedicamentosPorPersona(Persona persona) {
        return medicamentoRepository.findByPersonaRelacionada(persona);
    }

    @Transactional
    public Medicamento obtenerOCrearMedicamento(String nombre, Persona persona) {
        List<Medicamento> medicamentosRelacionados = medicamentoRepository.findByPersonaRelacionada(persona);

        for (Medicamento medicamento : medicamentosRelacionados) {
            if (medicamento.getNombre().equalsIgnoreCase(nombre)) {
                return medicamento;
            }
        }

        Medicamento nuevo = new Medicamento();
        nuevo.setNombre(nombre);
        nuevo.setPersona(persona);
        entityManager.persist(nuevo);
        return nuevo;
    }

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

    public Enfermedad obtenerOCrearEnfermedad(String nombreEnfermedad) {
        if (nombreEnfermedad == null || nombreEnfermedad.trim().isEmpty()) {
            return null;
        }

        Enfermedad enfermedadExistente = enfermedadRepository.findFirstByNombreIgnoreCase(nombreEnfermedad.trim());
        if (enfermedadExistente != null) {
            return enfermedadExistente;
        }

        Enfermedad nuevaEnfermedad = new Enfermedad();
        nuevaEnfermedad.setNombre(nombreEnfermedad.trim());
        return enfermedadRepository.save(nuevaEnfermedad);
    }

    public Medicamento obtenerOCrearMedicamento(String nombreMedicamento) {
        if (nombreMedicamento == null || nombreMedicamento.trim().isEmpty()) {
            return null;
        }

        Medicamento medicamentoExistente = medicamentoRepository.findFirstByNombreIgnoreCase(nombreMedicamento.trim());
        if (medicamentoExistente != null) {
            return medicamentoExistente;
        }

        Medicamento nuevoMedicamento = new Medicamento();
        nuevoMedicamento.setNombre(nombreMedicamento.trim());
        return medicamentoRepository.save(nuevoMedicamento);
    }

    @Transactional
    public void guardarEnfermedadConMedicamentos(Enfermedad enfermedad, List<String> nombresMedicamentos, Persona persona) {
        enfermedad.setPersona(persona);

        if (enfermedad.getId() == null) {
            entityManager.persist(enfermedad);
        } else {
            entityManager.merge(enfermedad);
        }

        Set<String> nombresUnicos = new HashSet<>(nombresMedicamentos);
        for (String nombreMedicamento : nombresUnicos) {
            Medicamento medicamento = obtenerOCrearMedicamento(nombreMedicamento, persona);
            asociarMedicamentoAEnfermedad(medicamento, enfermedad);
        }
    }

    @Transactional
    public Persona guardarPersona(Persona persona) {
        return guardarConNumero(persona);
    }

    @Transactional
    public void actualizarPersona(Long id, Persona personaActualizada) {
        Persona personaExistente = personaRepository.findById(id).orElse(null);
        if (personaExistente == null) {
            throw new RuntimeException("No se encontro la persona con ID: " + id);
        }

        personaExistente.setNombres(personaActualizada.getNombres());
        personaExistente.setApellidos(personaActualizada.getApellidos());
        personaExistente.setCedula(personaActualizada.getCedula());
        personaExistente.setLugarExpedicion(personaActualizada.getLugarExpedicion());
        personaExistente.setDireccion(personaActualizada.getDireccion());
        personaExistente.setSexo(personaActualizada.getSexo());
        personaExistente.setCorreoInstitucional(personaActualizada.getCorreoInstitucional());
        personaExistente.setTelefonoInstitucional(personaActualizada.getTelefonoInstitucional());
        personaExistente.setEnlaceSigep(personaActualizada.getEnlaceSigep());
        personaExistente.setNumero(personaActualizada.getNumero());

        personaRepository.save(personaExistente);
    }

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

        if (!lista.isEmpty()) {
            return lista.get(0);
        }

        CargoLaboral nuevo = new CargoLaboral();
        nuevo.setCargo(cargo);
        nuevo.setCodigo(codigo);
        nuevo.setDependencia(dependencia);
        entityManager.persist(nuevo);
        return nuevo;
    }

    @Transactional(readOnly = true)
    public int obtenerSiguienteNumeroSinHuecos() {
        return personaNumberingService.obtenerSiguienteNumeroSinHuecos();
    }

    @Transactional
    public List<Persona> guardarPersonasEnLote(List<Persona> personas) {
        personaNumberingService.asignarNumerosFaltantes(personas);
        List<Persona> guardadas = personaRepository.saveAll(personas);
        personaRepository.flush();
        return guardadas;
    }

    @Transactional
    public void guardarCargosEnLote(List<CargoLaboral> cargos) {
        if (cargos == null || cargos.isEmpty()) {
            return;
        }

        List<CargoLaboral> nuevos = cargos.stream()
                .filter(cargo -> cargo.getId() == null)
                .collect(Collectors.toList());

        if (!nuevos.isEmpty()) {
            cargoLaboralRepository.saveAll(nuevos);
        }
    }

    public Map<String, CargoLaboral> obtenerTodosLosCargosComoMapa() {
        List<CargoLaboral> cargosExistentes = cargoLaboralRepository.findAll();
        Map<String, CargoLaboral> mapa = new HashMap<>();

        for (CargoLaboral cargo : cargosExistentes) {
            String key = (cargo.getCargo() + "|" + cargo.getCodigo() + "|" + cargo.getDependencia()).toUpperCase();
            mapa.put(key, cargo);
        }

        return mapa;
    }

    @Transactional
    public void reordenarNumerosSoloNuevos(List<Persona> nuevasPersonas) {
        if (nuevasPersonas == null || nuevasPersonas.isEmpty()) {
            return;
        }

        Integer maxNumeroActual = personaRepository.findMaxNumero();
        if (maxNumeroActual == null) {
            maxNumeroActual = 0;
        }

        int numeroAsignado = maxNumeroActual + 1;
        for (Persona persona : nuevasPersonas) {
            persona.setNumero(numeroAsignado++);
        }

        personaRepository.saveAll(nuevasPersonas);
    }

    @Transactional
    public void guardarFormacionesEnLote(List<Formacion> formaciones) {
        for (int i = 0; i < formaciones.size(); i++) {
            entityManager.persist(formaciones.get(i));
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Transactional
    public void guardarPersonaCargoEnLote(List<PersonaCargoLaboral> cargos) {
        for (int i = 0; i < cargos.size(); i++) {
            entityManager.persist(cargos.get(i));
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Transactional
    public void guardarInduccionesEnLote(List<InduccionExamen> inducciones) {
        for (int i = 0; i < inducciones.size(); i++) {
            entityManager.persist(inducciones.get(i));
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Transactional
    public void guardarRiesgosEnLote(List<RiesgoProcedencia> riesgos) {
        for (int i = 0; i < riesgos.size(); i++) {
            entityManager.persist(riesgos.get(i));
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Transactional
    public void guardarSaludEnLote(List<Salud> saludLote) {
        for (int i = 0; i < saludLote.size(); i++) {
            entityManager.persist(saludLote.get(i));
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    public Persona buscarPorId(Long id) {
        return personaRepository.findById(id).orElse(null);
    }

    public Persona guardar(Persona persona) {
        return personaRepository.save(persona);
    }

    @Transactional
    public void guardarContactosEnLote(List<ContactoEmergencia> contactos) {
        for (int i = 0; i < contactos.size(); i++) {
            entityManager.persist(contactos.get(i));
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Transactional
    public void guardarEnfermedadesEnLote(List<Enfermedad> enfermedades) {
        for (int i = 0; i < enfermedades.size(); i++) {
            entityManager.persist(enfermedades.get(i));
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Transactional
    public void guardarAlergiasEnLote(List<Alergia> alergias) {
        for (int i = 0; i < alergias.size(); i++) {
            entityManager.persist(alergias.get(i));
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Transactional
    public void eliminarPersonaYReordenar(Long id) {
        Persona persona = personaRepository.findById(id).orElse(null);
        if (persona != null) {
            Integer numeroEliminado = persona.getNumero();
            personaRepository.deleteById(id);
            personaNumberingService.decrementarPosteriores(numeroEliminado);
        }
    }

    public void guardarMedicamento(Medicamento medicamento) {
        if (medicamento == null) {
            throw new IllegalArgumentException("El medicamento no puede ser nulo.");
        }

        if (medicamento.getPersona() == null || medicamento.getPersona().getId() == null) {
            throw new IllegalArgumentException("Debe asociar una persona antes de guardar el medicamento.");
        }

        medicamentoRepository.save(medicamento);
    }

    @Autowired
    private PersonaDtoMapperService personaDtoMapperService;

    @Autowired
    private PersonaDtoPersistenceService personaDtoPersistenceService;

    @Autowired
    private PersonaDuplicateCleanupService personaDuplicateCleanupService;

    public PersonaCompletaDTO convertirADTO(Persona persona) {
        return personaDtoMapperService.convertirADTO(persona);
    }

    @Transactional
    public void guardarDTO(PersonaCompletaDTO dto) {
        personaDtoPersistenceService.guardarDTO(dto);
    }

    @Transactional(readOnly = true)
    public PersonaCompletaDTO buscarDTOporId(Long id) {
        return personaDtoMapperService.buscarDTOporId(id);
    }

    @Transactional
    public void reordenarNumeros() {
        personaNumberingService.reordenarNumeros();
    }

    @Transactional(readOnly = true)
    public Persona obtenerPersonaConRelaciones(Long id) {
        return personaRepository.findByIdWithAllRelations(id).orElse(null);
    }

    public List<Persona> buscarPorCedula(String cedula) {
        return personaRepository.findByCedula(cedula);
    }

    public List<Persona> buscarPorCorreoInstitucional(String correo) {
        return personaRepository.findByCorreoInstitucional(correo);
    }

    public List<Persona> buscarPorNombreCompleto(String nombres, String apellidos) {
        return personaRepository.findByNombresContainingIgnoreCaseAndApellidosContainingIgnoreCase(nombres, apellidos);
    }

    public static class CleanupSummary {
        private int personasRevisadas;
        private int alergiasEliminadas;
        private int medicamentosEliminados;
        private int enfermedadesEliminadas;

        public int getPersonasRevisadas() { return personasRevisadas; }
        public int getAlergiasEliminadas() { return alergiasEliminadas; }
        public int getMedicamentosEliminados() { return medicamentosEliminados; }
        public int getEnfermedadesEliminadas() { return enfermedadesEliminadas; }

        void incrementarPersonas() { personasRevisadas++; }
        void incrementarAlergias() { alergiasEliminadas++; }
        void incrementarMedicamentos() { medicamentosEliminados++; }
        void incrementarEnfermedades() { enfermedadesEliminadas++; }
    }

    @Transactional
    public CleanupSummary limpiarDuplicadosExistentes() {
        return personaDuplicateCleanupService.limpiarDuplicadosExistentes();
    }
}
