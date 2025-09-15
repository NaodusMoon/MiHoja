package com.miapp.MiHoja.service;

import com.miapp.MiHoja.dto.PersonaCompletaDTO;
import com.miapp.MiHoja.model.*;
import com.miapp.MiHoja.repository.AlergiaRepository;
import com.miapp.MiHoja.repository.CargoLaboralRepository;
import com.miapp.MiHoja.repository.ContactoEmergenciaRepository;
import com.miapp.MiHoja.repository.EnfermedadRepository;
import com.miapp.MiHoja.repository.FormacionRepository;
import com.miapp.MiHoja.repository.InduccionExamenRepository;
import com.miapp.MiHoja.repository.MedicamentoRepository;
import com.miapp.MiHoja.repository.PersonaCargoLaboralRepository;
import com.miapp.MiHoja.repository.PersonaRepository;
import com.miapp.MiHoja.repository.RiesgoProcedenciaRepository;
import com.miapp.MiHoja.repository.SaludRepository;

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
private EnfermedadRepository enfermedadRepository;


    @Autowired
    private PersonaRepository personaRepository;

     @Autowired
    private CargoLaboralRepository cargoLaboralRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @PersistenceContext
    private EntityManager entityManager;

     @Transactional
public void eliminarVarios(List<Long> ids) {
    int batchSize = 25;
    for (int i = 0; i < ids.size(); i += batchSize) {
        List<Long> subList = ids.subList(i, Math.min(i + batchSize, ids.size()));
        personaRepository.deleteBatchByIds(subList);
        entityManager.flush();
        entityManager.clear();
    }
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



    public Enfermedad obtenerOCrearEnfermedad(String nombreEnfermedad, Persona persona) {
    if (nombreEnfermedad == null || nombreEnfermedad.trim().isEmpty()) {
        return null; // Evita guardar enfermedades vacías
    }

    
    // Buscar si ya existe la enfermedad en la base de datos
    Enfermedad enfermedadExistente = enfermedadRepository.findByNombre(nombreEnfermedad.trim());

    if (enfermedadExistente != null) {
        // Si la persona aún no tiene esta enfermedad, la agregamos
        if (!persona.getEnfermedades().contains(enfermedadExistente)) {
            persona.getEnfermedades().add(enfermedadExistente);
        }
        return enfermedadExistente;
    }

    // Si no existe, creamos una nueva
    Enfermedad nuevaEnfermedad = new Enfermedad();
    nuevaEnfermedad.setNombre(nombreEnfermedad.trim());

    // Guardar la nueva enfermedad
    enfermedadRepository.save(nuevaEnfermedad);

    // Asociar la nueva enfermedad a la persona
    persona.getEnfermedades().add(nuevaEnfermedad);

    return nuevaEnfermedad;
}



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


public Enfermedad obtenerOCrearEnfermedad(String nombreEnfermedad) {
    if (nombreEnfermedad == null || nombreEnfermedad.trim().isEmpty()) {
        return null;
    }

    // Busca por nombre exacto
    Enfermedad enfermedadExistente = enfermedadRepository.findFirstByNombreIgnoreCase(nombreEnfermedad.trim());

    if (enfermedadExistente != null) {
        return enfermedadExistente;
    }

    // Si no existe, la crea
    Enfermedad nuevaEnfermedad = new Enfermedad();
    nuevaEnfermedad.setNombre(nombreEnfermedad.trim());

    return enfermedadRepository.save(nuevaEnfermedad);
}

public Medicamento obtenerOCrearMedicamento(String nombreMedicamento) {
    if (nombreMedicamento == null || nombreMedicamento.trim().isEmpty()) {
        return null;
    }

    // Busca por nombre exacto
    Medicamento medicamentoExistente = medicamentoRepository.findFirstByNombreIgnoreCase(nombreMedicamento.trim());

    if (medicamentoExistente != null) {
        return medicamentoExistente;
    }

    // Si no existe, lo crea
    Medicamento nuevoMedicamento = new Medicamento();
    nuevoMedicamento.setNombre(nombreMedicamento.trim());

    return medicamentoRepository.save(nuevoMedicamento);
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


    // ✅ Guardar persona (usando guardarConNumero internamente)
@Transactional
public Persona guardarPersona(Persona persona) {
    return guardarConNumero(persona);
}

// ✅ Actualizar persona
@Transactional
public void actualizarPersona(Long id, Persona personaActualizada) {
    Persona personaExistente = personaRepository.findById(id).orElse(null);

    if (personaExistente != null) {
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
        // ✅ Si tienes más campos, agrégalos aquí

        personaRepository.save(personaExistente);
    } else {
        throw new RuntimeException("No se encontró la persona con ID: " + id);
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


   @Transactional
public List<Persona> guardarPersonasEnLote(List<Persona> personas) {
    for (Persona persona : personas) {
        if (persona.getNumero() == null || persona.getNumero() <= 0) {
            persona.setNumero(obtenerSiguienteNumeroSinHuecos());
        }
    }
    List<Persona> guardadas = personaRepository.saveAll(personas);
    personaRepository.flush();
    return guardadas;
}

@Transactional
public void guardarCargosEnLote(List<CargoLaboral> cargos) {
    if (cargos == null || cargos.isEmpty()) return;

    // ✅ Filtrar solo los cargos sin ID (nuevos)
    List<CargoLaboral> nuevos = cargos.stream()
            .filter(c -> c.getId() == null)
            .collect(Collectors.toList());

    if (!nuevos.isEmpty()) {
        cargoLaboralRepository.saveAll(nuevos);
        System.out.println("✅ Nuevos cargos guardados en lote: " + nuevos.size());
    }
}

public Map<String, CargoLaboral> obtenerTodosLosCargosComoMapa() {
    List<CargoLaboral> cargosExistentes = cargoLaboralRepository.findAll();
    Map<String, CargoLaboral> mapa = new HashMap<>();

    for (CargoLaboral c : cargosExistentes) {
        String key = (c.getCargo() + "|" + c.getCodigo() + "|" + c.getDependencia()).toUpperCase();
        mapa.put(key, c);
    }

    System.out.println("✅ Cargos precargados en cache: " + mapa.size());
    return mapa;
}



@Transactional
public void reordenarNumerosSoloNuevos(List<Persona> nuevasPersonas) {
    if (nuevasPersonas == null || nuevasPersonas.isEmpty()) return;

    // ✅ Obtener el máximo número actual en la BD
    Integer maxNumeroActual = personaRepository.findMaxNumero();
    if (maxNumeroActual == null) maxNumeroActual = 0;

    int numeroAsignado = maxNumeroActual + 1;

    for (Persona persona : nuevasPersonas) {
        persona.setNumero(numeroAsignado++);
    }

    // ✅ Guardar solo las nuevas con los números actualizados
    personaRepository.saveAll(nuevasPersonas);

    System.out.println("✅ Números reasignados solo para nuevos registros. Último número: " + (numeroAsignado - 1));






    // ✅ Guardar solo los nuevos con los números actualizados
    personaRepository.saveAll(nuevasPersonas);

    System.out.println("✅ Números reasignados solo para nuevos registros. Último número: " + (numeroAsignado - 1));
}




@Transactional
public void guardarFormacionesEnLote(List<Formacion> formaciones) {
    for (int i = 0; i < formaciones.size(); i++) {
        entityManager.persist(formaciones.get(i));
        if (i % 50 == 0) { // 🔄 Flush y clear cada 50 para evitar sobrecarga de memoria
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

 // 🔹 Buscar por ID
    public Persona buscarPorId(Long id) {
        return personaRepository.findById(id).orElse(null);
    }
    
    // 🔹 Guardar (sirve tanto para insertar como para actualizar)
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

    
  public void guardarMedicamento(Medicamento medicamento) {
    if (medicamento == null) {
        throw new IllegalArgumentException("El medicamento no puede ser nulo.");
    }

    if (medicamento.getPersona() == null || medicamento.getPersona().getId() == null) {
        throw new IllegalArgumentException("Debe asociar una persona antes de guardar el medicamento.");
    }

    System.out.println("📌 Guardando medicamento: " + medicamento.getNombre() +
                       " para la persona ID: " + medicamento.getPersona().getId());

    medicamentoRepository.save(medicamento);
    System.out.println("✅ Medicamento guardado correctamente.");
}







// 🔹 Convierte de entidad -> DTO
public PersonaCompletaDTO convertirADTO(Persona persona) {
    if (persona == null) return null;

    PersonaCompletaDTO dto = new PersonaCompletaDTO();
    dto.setId(persona.getId());
    dto.setNombres(persona.getNombres());
    dto.setApellidos(persona.getApellidos());
    dto.setCedula(persona.getCedula());
    dto.setLugarExpedicion(persona.getLugarExpedicion());
    dto.setFechaNacimiento(persona.getFechaNacimiento());
    dto.setDireccion(persona.getDireccion());
    dto.setSexo(persona.getSexo());
    dto.setNumero(persona.getNumero());
    dto.setCorreoInstitucional(persona.getCorreoInstitucional());
    dto.setTelefonoInstitucional(persona.getTelefonoInstitucional());
    dto.setEnlaceSigep(persona.getEnlaceSigep());
    dto.setEstado(persona.getEstado());
    dto.setNumeroHijos(persona.getNumeroHijos());
    dto.setImagenUrl(persona.getImagenUrl());

    // ✅ Formaciones
    if (persona.getFormaciones() != null) {
        dto.setFormacion(persona.getFormaciones().stream().map(f -> {
            PersonaCompletaDTO.Formacion fDTO = new PersonaCompletaDTO.Formacion();
            fDTO.setIdFormacion(f.getIdFormacion());
            fDTO.setFormacionAcademica(f.getFormacionAcademica());
            fDTO.setGrado(f.getGrado());
            fDTO.setTitulo(f.getTitulo());
            return fDTO;
        }).toList());
    }

    // ✅ Cargos Laborales (PersonaCargoLaboral en tu entidad)
if (persona.getCargosLaborales() != null) {
    dto.setCargoLaboral(persona.getCargosLaborales().stream().map(pcl -> {
        PersonaCompletaDTO.CargoLaboral cDTO = new PersonaCompletaDTO.CargoLaboral();

        CargoLaboral cargo = pcl.getCargo(); // obtenemos el cargo real

        cDTO.setIdCargo(cargo.getId());      // el ID del cargo
        cDTO.setCodigo(cargo.getCodigo());   // el código del cargo
        cDTO.setCargo(cargo.getCargo());     // nombre del cargo
        cDTO.setDependencia(cargo.getDependencia()); // dependencia

        return cDTO;
    }).toList());
}


// ✅ Salud
if (persona.getRegistrosSalud() != null && !persona.getRegistrosSalud().isEmpty()) {
    Salud registro = persona.getRegistrosSalud().iterator().next(); // tomamos el primero
    PersonaCompletaDTO.Salud sDTO = new PersonaCompletaDTO.Salud();
    sDTO.setIdSalud(registro.getIdSalud());
    sDTO.setDotacion(registro.getDotacion());
    sDTO.setArl(registro.getArl());
    sDTO.setEps(registro.getEps());
    sDTO.setAfp(registro.getAfp());
    sDTO.setCcf(registro.getCcf());
    sDTO.setRh(registro.getRh());
    sDTO.setCarnetVacunacion(registro.getCarnetVacunacion());
    dto.setSalud(sDTO);
}



// ✅ Enfermedad
if (persona.getEnfermedades() != null && !persona.getEnfermedades().isEmpty()) {
    dto.setEnfermedad(
        persona.getEnfermedades().stream()
            .map(e -> {
                PersonaCompletaDTO.Enfermedad eDTO = new PersonaCompletaDTO.Enfermedad();
                eDTO.setIdEnfermedad(e.getId()); // <-- corregido
                eDTO.setNombre(e.getNombre());
                return eDTO;
            })
            .toList()
    );
}


// ✅ Alergia
if (persona.getAlergias() != null && !persona.getAlergias().isEmpty()) {
    dto.setAlergia(
        persona.getAlergias().stream()
            .map(a -> {
                PersonaCompletaDTO.Alergia aDTO = new PersonaCompletaDTO.Alergia();
                aDTO.setIdAlergia(a.getId()); // <-- corregido
                aDTO.setNombre(a.getNombre());
                return aDTO;
            })
            .toList()
    );
}


// ✅ Medicamento
if (persona.getMedicamentos() != null && !persona.getMedicamentos().isEmpty()) {
    dto.setMedicamento(
        persona.getMedicamentos().stream()
            .map(m -> {
                PersonaCompletaDTO.Medicamento mDTO = new PersonaCompletaDTO.Medicamento();
                mDTO.setIdMedicamento(m.getId()); // <-- corregido
                mDTO.setNombre(m.getNombre());
                return mDTO;
            })
            .toList()
    );
}


// ✅ Contacto de Emergencia
if (persona.getContactosEmergencia() != null && !persona.getContactosEmergencia().isEmpty()) {
    dto.setContactoEmergencia(
        persona.getContactosEmergencia().stream()
            .map(c -> {
                PersonaCompletaDTO.ContactoEmergencia cDTO = new PersonaCompletaDTO.ContactoEmergencia();
                cDTO.setIdContacto(c.getIdContacto());
                cDTO.setNombreContactoEmergencia(c.getNombreContactoEmergencia()); // <-- corregido
                cDTO.setTelefonoContactoEmergencia(c.getTelefonoContactoEmergencia()); // <-- corregido
                cDTO.setParentesco(c.getParentesco());
                return cDTO;
            })
            .toList()
    );
}


// ✅ Riesgo Procedencia
if (persona.getRiesgoProcedencias() != null && !persona.getRiesgoProcedencias().isEmpty()) {
    dto.setRiesgoProcedencia(
        persona.getRiesgoProcedencias().stream()
            .map(r -> {
                PersonaCompletaDTO.RiesgoProcedencia rDTO = new PersonaCompletaDTO.RiesgoProcedencia();
                rDTO.setIdRiesgo(r.getIdRiesgo());
                rDTO.setRiesgo(r.getRiesgo()); // <-- usamos setRiesgo en lugar de setDescripcion
                rDTO.setMedioTransporte(r.getMedioTransporte());
                rDTO.setProcedenciaTrabajador(r.getProcedenciaTrabajador());
                return rDTO;
            })
            .toList()
    );
}



// ✅ Inducción Examen (a través de los cargos laborales)
if (persona.getCargosLaborales() != null && !persona.getCargosLaborales().isEmpty()) {
    List<PersonaCompletaDTO.InduccionExamen> inducciones = persona.getCargosLaborales().stream()
        .filter(pcl -> pcl.getInduccionesExamen() != null && !pcl.getInduccionesExamen().isEmpty()) // obtiene InduccionExamen
        .flatMap(pcl -> pcl.getInduccionesExamen().stream())
        .map(i -> {
            PersonaCompletaDTO.InduccionExamen iDTO = new PersonaCompletaDTO.InduccionExamen();
            iDTO.setIdInduccion(i.getIdInduccion());
            iDTO.setInduccion(i.getInduccion());
            iDTO.setExamenIngreso(i.getExamenIngreso());
            iDTO.setFechaEgreso(i.getFechaEgreso());
            return iDTO;
        })
        .toList();

    dto.setInduccionExamen(inducciones);
}



return dto;
}


  

    @Autowired
    private PersonaCargoLaboralRepository pclRepository;

    @Autowired
    private SaludRepository saludRepository;

    @Autowired
    private FormacionRepository formacionRepository;

    @Autowired
    private AlergiaRepository alergiaRepository;

    @Autowired
private InduccionExamenRepository induccionExamenRepository;

 // 🔴 Falta esto para Contacto de emergencia
    @Autowired
    private ContactoEmergenciaRepository contactoEmergenciaRepository;

    @Autowired
    private RiesgoProcedenciaRepository riesgoProcedenciaRepository;

    



 @Transactional
public void guardarDTO(PersonaCompletaDTO dto) {
    if (dto.getId() == null) {
        throw new IllegalArgumentException("El ID del DTO es null, no se puede guardar.");
    }

    Persona persona = personaRepository.findById(dto.getId())
            .orElseThrow(() -> new RuntimeException("Persona con id " + dto.getId() + " no encontrada"));

    // 1️⃣ Campos simples
    if (dto.getNombres() != null) persona.setNombres(dto.getNombres());
    if (dto.getApellidos() != null) persona.setApellidos(dto.getApellidos());
    if (dto.getCedula() != null) persona.setCedula(dto.getCedula());
    if (dto.getLugarExpedicion() != null) persona.setLugarExpedicion(dto.getLugarExpedicion());
    if (dto.getFechaNacimiento() != null) persona.setFechaNacimiento(dto.getFechaNacimiento());
    if (dto.getDireccion() != null) persona.setDireccion(dto.getDireccion());
    if (dto.getSexo() != null) persona.setSexo(dto.getSexo());
    if (dto.getNumero() != null) persona.setNumero(dto.getNumero());
    if (dto.getCorreoInstitucional() != null) persona.setCorreoInstitucional(dto.getCorreoInstitucional());
    if (dto.getTelefonoInstitucional() != null) persona.setTelefonoInstitucional(dto.getTelefonoInstitucional());
    if (dto.getEnlaceSigep() != null) persona.setEnlaceSigep(dto.getEnlaceSigep());
    if (dto.getEstado() != null) persona.setEstado(dto.getEstado());
    if (dto.getNumeroHijos() != null) persona.setNumeroHijos(dto.getNumeroHijos());
    if (dto.getImagenUrl() != null) persona.setImagenUrl(dto.getImagenUrl());

    // 2️⃣ Salud
    if (dto.getSalud() != null) {
        Salud salud = persona.getRegistrosSalud().stream().findFirst().orElse(new Salud());
        PersonaCompletaDTO.Salud sDTO = dto.getSalud();
        if (sDTO.getDotacion() != null) salud.setDotacion(sDTO.getDotacion());
        if (sDTO.getArl() != null) salud.setArl(sDTO.getArl());
        if (sDTO.getEps() != null) salud.setEps(sDTO.getEps());
        if (sDTO.getAfp() != null) salud.setAfp(sDTO.getAfp());
        if (sDTO.getCcf() != null) salud.setCcf(sDTO.getCcf());
        if (sDTO.getRh() != null) salud.setRh(sDTO.getRh());
        salud.setCarnetVacunacion(sDTO.getCarnetVacunacion());
        salud.setPersona(persona);
        saludRepository.save(salud);
        if (!persona.getRegistrosSalud().contains(salud)) persona.getRegistrosSalud().add(salud);
    }

    // 3️⃣ Formación académica
if (dto.getFormacion() != null) {
    for (PersonaCompletaDTO.Formacion fDTO : dto.getFormacion()) {
        Formacion f = (fDTO.getIdFormacion() != null)
                ? formacionRepository.findById(fDTO.getIdFormacion()).orElse(new Formacion())
                : new Formacion();
        f.setFormacionAcademica(fDTO.getFormacionAcademica());
        f.setGrado(fDTO.getGrado());
        f.setTitulo(fDTO.getTitulo());
        f.setPersona(persona);
        formacionRepository.save(f);

        if (!persona.getFormaciones().contains(f)) // ✅ corregido
            persona.getFormaciones().add(f);
    }
}


    // 4️⃣ Cargo laboral (base)
if (dto.getCargoLaboral() != null) {
    for (PersonaCompletaDTO.CargoLaboral cDTO : dto.getCargoLaboral()) {
        CargoLaboral c = (cDTO.getIdCargo() != null)
                ? cargoLaboralRepository.findById(cDTO.getIdCargo()).orElse(new CargoLaboral())
                : new CargoLaboral();
        c.setCodigo(cDTO.getCodigo());
        c.setCargo(cDTO.getCargo());
        c.setDependencia(cDTO.getDependencia());
        cargoLaboralRepository.save(c);

        // Crear la relación intermedia PersonaCargoLaboral
        PersonaCargoLaboral pcl = new PersonaCargoLaboral();
        pcl.setPersona(persona);
        pcl.setCargo(c);
        // opcional: setear fechas y meses si vienen en DTO

        // 🔹 usar el getter correcto
        persona.getCargosLaborales().add(pcl);
    }
}



    // 5️⃣ PersonaCargoLaboral
    if (dto.getPersonaCargoLaboral() != null) {
        for (PersonaCompletaDTO.PersonaCargoLaboral pclDTO : dto.getPersonaCargoLaboral()) {
            if (pclDTO.getCargoId() != null) {
                PersonaCargoLaboral pcl = (pclDTO.getIdPcl() != null)
                        ? pclRepository.findById(pclDTO.getIdPcl()).orElse(new PersonaCargoLaboral())
                        : new PersonaCargoLaboral();
                pcl.setPersona(persona);
                CargoLaboral cargo = cargoLaboralRepository.findById(pclDTO.getCargoId())
                        .orElseThrow(() -> new RuntimeException("Cargo no encontrado con id " + pclDTO.getCargoId()));
                pcl.setCargo(cargo);
                pcl.setFechaIngreso(pclDTO.getFechaIngreso());
                pcl.setFechaFirmaContrato(pclDTO.getFechaFirmaContrato());
                pcl.setMesesExperiencia(pclDTO.getMesesExperiencia());
                pclRepository.save(pcl);
                if (!persona.getCargosLaborales().contains(pcl)) persona.getCargosLaborales().add(pcl);
            }
        }
    }

    // 6️⃣ Inducción / Examen
    if (dto.getInduccionExamen() != null) {
        for (PersonaCompletaDTO.InduccionExamen ieDTO : dto.getInduccionExamen()) {
            if (ieDTO.getPersonaCargoId() != null) {
                PersonaCargoLaboral pcl = pclRepository.findById(ieDTO.getPersonaCargoId()).orElse(null);
                if (pcl != null) {
                    InduccionExamen ie = (ieDTO.getIdInduccion() != null)
                            ? induccionExamenRepository.findById(ieDTO.getIdInduccion()).orElse(new InduccionExamen())
                            : new InduccionExamen();
                    ie.setPersonaCargoLaboral(pcl);
                    ie.setInduccion(ieDTO.getInduccion());
                    ie.setExamenIngreso(ieDTO.getExamenIngreso());
                    ie.setFechaEgreso(ieDTO.getFechaEgreso());
                    induccionExamenRepository.save(ie);
                    if (!pcl.getInduccionesExamen().contains(ie)) pcl.getInduccionesExamen().add(ie);
                }
            }
        }
    }

    // 7️⃣ Enfermedades
    if (dto.getEnfermedad() != null) {
        for (PersonaCompletaDTO.Enfermedad eDTO : dto.getEnfermedad()) {
            Enfermedad e = (eDTO.getIdEnfermedad() != null)
                    ? enfermedadRepository.findById(eDTO.getIdEnfermedad()).orElse(new Enfermedad())
                    : new Enfermedad();
            e.setNombre(eDTO.getNombre());
            e.setPersona(persona);
            enfermedadRepository.save(e);
            if (!persona.getEnfermedades().contains(e)) persona.getEnfermedades().add(e);
        }
    }

    // 8️⃣ Alergias
    if (dto.getAlergia() != null) {
        for (PersonaCompletaDTO.Alergia aDTO : dto.getAlergia()) {
            Alergia a = (aDTO.getIdAlergia() != null)
                    ? alergiaRepository.findById(aDTO.getIdAlergia()).orElse(new Alergia())
                    : new Alergia();
            a.setNombre(aDTO.getNombre());
            a.setPersona(persona);
            alergiaRepository.save(a);
            if (!persona.getAlergias().contains(a)) persona.getAlergias().add(a);
        }
    }

    // 9️⃣ Medicamentos
    if (dto.getMedicamento() != null) {
        for (PersonaCompletaDTO.Medicamento mDTO : dto.getMedicamento()) {
            Medicamento m = (mDTO.getIdMedicamento() != null)
                    ? medicamentoRepository.findById(mDTO.getIdMedicamento()).orElse(new Medicamento())
                    : new Medicamento();
            m.setNombre(mDTO.getNombre());
            m.setPersona(persona);
            medicamentoRepository.save(m);
            if (!persona.getMedicamentos().contains(m)) persona.getMedicamentos().add(m);
        }
    }

    // 🔟 Riesgo / Procedencia
if (dto.getRiesgoProcedencia() != null) {
    for (PersonaCompletaDTO.RiesgoProcedencia rpDTO : dto.getRiesgoProcedencia()) {
        RiesgoProcedencia rp = (rpDTO.getIdRiesgo() != null)
                ? riesgoProcedenciaRepository.findById(rpDTO.getIdRiesgo()).orElse(new RiesgoProcedencia())
                : new RiesgoProcedencia();
        rp.setMedioTransporte(rpDTO.getMedioTransporte());
        rp.setProcedenciaTrabajador(rpDTO.getProcedenciaTrabajador());
        rp.setRiesgo(rpDTO.getRiesgo());
        rp.setPersona(persona);
        riesgoProcedenciaRepository.save(rp);
        if (!persona.getRiesgoProcedencias().contains(rp))  // ⚠️ getter de Persona
            persona.getRiesgoProcedencias().add(rp);
    }
}


   
// 1️⃣1️⃣ Contacto de emergencia
if (dto.getContactoEmergencia() != null) {
    for (PersonaCompletaDTO.ContactoEmergencia ceDTO : dto.getContactoEmergencia()) {
        ContactoEmergencia ce = (ceDTO.getIdContacto() != null)
                ? contactoEmergenciaRepository.findById(ceDTO.getIdContacto()).orElse(new ContactoEmergencia())
                : new ContactoEmergencia();
        ce.setNombreContactoEmergencia(ceDTO.getNombreContactoEmergencia());
        ce.setParentesco(ceDTO.getParentesco());
        ce.setTelefonoContactoEmergencia(ceDTO.getTelefonoContactoEmergencia());
        ce.setPersona(persona);
        contactoEmergenciaRepository.save(ce);
        // ✅ Aquí usamos el getter correcto
        if (!persona.getContactosEmergencia().contains(ce)) 
            persona.getContactosEmergencia().add(ce);
    }
}



    // ✅ Guardar persona final
    personaRepository.save(persona);
}







  

    @Transactional(readOnly = true)
    public PersonaCompletaDTO buscarDTOporId(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        PersonaCompletaDTO dto = new PersonaCompletaDTO();
        // ========================
        // DATOS PERSONALES
        // ========================
        dto.setId(persona.getId());
        dto.setNombres(persona.getNombres());
        dto.setApellidos(persona.getApellidos());
        dto.setCedula(persona.getCedula());
        dto.setLugarExpedicion(persona.getLugarExpedicion());
        dto.setFechaNacimiento(persona.getFechaNacimiento());
        dto.setDireccion(persona.getDireccion());
        dto.setSexo(persona.getSexo());
        dto.setNumero(persona.getNumero());
        dto.setCorreoInstitucional(persona.getCorreoInstitucional());
        dto.setTelefonoInstitucional(persona.getTelefonoInstitucional());
        dto.setEnlaceSigep(persona.getEnlaceSigep());
        dto.setEstado(persona.getEstado());
        dto.setNumeroHijos(persona.getNumeroHijos());
        dto.setImagenUrl(persona.getImagenUrl());

        // ========================
        // FORMACION
        // ========================
        List<PersonaCompletaDTO.Formacion> formaciones = persona.getFormaciones().stream().map(f -> {
            PersonaCompletaDTO.Formacion fDTO = new PersonaCompletaDTO.Formacion();
            fDTO.setIdFormacion(f.getIdFormacion());
            fDTO.setN(f.getPersona().getNumero().longValue());
            fDTO.setFormacionAcademica(f.getFormacionAcademica());
            fDTO.setGrado(f.getGrado());
            fDTO.setTitulo(f.getTitulo());
            return fDTO;
        }).toList();
        dto.setFormacion(formaciones);

        // ========================
        // SALUD (1:1)
        // ========================
        if (!persona.getRegistrosSalud().isEmpty()) {
            Salud s = persona.getRegistrosSalud().stream().findFirst().orElse(null);
            if (s != null) {
                PersonaCompletaDTO.Salud sDTO = new PersonaCompletaDTO.Salud();
                sDTO.setIdSalud(s.getIdSalud());
                sDTO.setDotacion(s.getDotacion());
                sDTO.setArl(s.getArl());
                sDTO.setEps(s.getEps());
                sDTO.setAfp(s.getAfp());
                sDTO.setCcf(s.getCcf());
                sDTO.setRh(s.getRh());
                sDTO.setCarnetVacunacion(s.getCarnetVacunacion());
                dto.setSalud(sDTO);
            }
        }

        // ========================
// CARGOS LABORALES
// ========================
List<PersonaCompletaDTO.CargoLaboral> cargosDTO = persona.getCargosLaborales().stream()
        .map(pcl -> {
            PersonaCompletaDTO.CargoLaboral cDTO = new PersonaCompletaDTO.CargoLaboral();
            cDTO.setIdCargo(pcl.getCargo().getId()); // <-- CORREGIDO
            cDTO.setCargo(pcl.getCargo().getCargo());
            cDTO.setCodigo(pcl.getCargo().getCodigo());
            cDTO.setDependencia(pcl.getCargo().getDependencia());
            return cDTO;
        }).toList();
dto.setCargoLaboral(cargosDTO);


 // ========================
// ALERGIAS
// ========================
List<PersonaCompletaDTO.Alergia> alergiasDTO = persona.getAlergias().stream().map(a -> {
    PersonaCompletaDTO.Alergia aDTO = new PersonaCompletaDTO.Alergia();
    aDTO.setIdAlergia(a.getId()); // <-- CORREGIDO
    aDTO.setN(persona.getNumero().longValue());
    aDTO.setNombre(a.getNombre());
    return aDTO;
}).toList();
dto.setAlergia(alergiasDTO);

        // ========================
        // ENFERMEDADES
        // ========================
        List<PersonaCompletaDTO.Enfermedad> enfermedadesDTO = persona.getEnfermedades().stream().map(e -> {
            PersonaCompletaDTO.Enfermedad eDTO = new PersonaCompletaDTO.Enfermedad();
            eDTO.setIdEnfermedad(e.getId());
            eDTO.setN(persona.getNumero().longValue());
            eDTO.setNombre(e.getNombre());
            return eDTO;
        }).toList();
        dto.setEnfermedad(enfermedadesDTO);

        // ========================
// MEDICAMENTOS
// ========================
List<PersonaCompletaDTO.Medicamento> medicamentosDTO = persona.getMedicamentos().stream().map(m -> {
    PersonaCompletaDTO.Medicamento mDTO = new PersonaCompletaDTO.Medicamento();
    mDTO.setIdMedicamento(m.getId()); // <-- CORREGIDO
    mDTO.setNombre(m.getNombre());
    return mDTO;
}).toList();
dto.setMedicamento(medicamentosDTO);

// ========================
// CONTACTO EMERGENCIA
// ========================
List<PersonaCompletaDTO.ContactoEmergencia> contactosDTO = persona.getContactosEmergencia().stream().map(c -> {
    PersonaCompletaDTO.ContactoEmergencia cDTO = new PersonaCompletaDTO.ContactoEmergencia();
    cDTO.setIdContacto(c.getIdContacto());
    cDTO.setNombreContactoEmergencia(c.getNombreContactoEmergencia());
    cDTO.setTelefonoContactoEmergencia(c.getTelefonoContactoEmergencia());
    cDTO.setParentesco(c.getParentesco());
    return cDTO;
}).toList();
dto.setContactoEmergencia(contactosDTO);


// ========================
// RIESGO PROCEDENCIA
// ========================
List<PersonaCompletaDTO.RiesgoProcedencia> riesgosDTO = persona.getRiesgoProcedencias().stream().map(r -> {
    PersonaCompletaDTO.RiesgoProcedencia rDTO = new PersonaCompletaDTO.RiesgoProcedencia();
    rDTO.setIdRiesgo(r.getIdRiesgo());
    rDTO.setRiesgo(r.getRiesgo());
    rDTO.setMedioTransporte(r.getMedioTransporte());
    rDTO.setProcedenciaTrabajador(r.getProcedenciaTrabajador());
    return rDTO;
}).toList();
dto.setRiesgoProcedencia(riesgosDTO);


// ========================
// INDUCCION / EXAMEN
// ========================
List<PersonaCompletaDTO.InduccionExamen> induccionesDTO = persona.getCargosLaborales().stream()
    .filter(pcl -> pcl.getInduccionesExamen() != null && !pcl.getInduccionesExamen().isEmpty())
    .flatMap(pcl -> pcl.getInduccionesExamen().stream())
    .map(i -> {
        PersonaCompletaDTO.InduccionExamen iDTO = new PersonaCompletaDTO.InduccionExamen();
        iDTO.setIdInduccion(i.getIdInduccion());
        iDTO.setInduccion(i.getInduccion());
        iDTO.setExamenIngreso(i.getExamenIngreso());
        iDTO.setFechaEgreso(i.getFechaEgreso());
        return iDTO;
    })
    .toList();
dto.setInduccionExamen(induccionesDTO);


// ========================
// CARGOS LABORALES (con datos de PersonaCargoLaboral)
// ========================
List<PersonaCompletaDTO.PersonaCargoLaboral> cargosLaboralesDTO = persona.getCargosLaborales().stream()
    .map(pcl -> {
        PersonaCompletaDTO.PersonaCargoLaboral pclDTO = new PersonaCompletaDTO.PersonaCargoLaboral();
        pclDTO.setIdPcl(pcl.getId()); // <-- usa getId()
        pclDTO.setPersonaId(pcl.getPersona() != null ? pcl.getPersona().getId() : null); // <-- persona.getId()
        pclDTO.setCargoId(pcl.getCargo() != null ? pcl.getCargo().getId() : null);       // <-- cargo.getId()
        pclDTO.setFechaIngreso(pcl.getFechaIngreso());
        pclDTO.setFechaFirmaContrato(pcl.getFechaFirmaContrato());
        pclDTO.setMesesExperiencia(pcl.getMesesExperiencia());
        return pclDTO;
    }).toList();

dto.setPersonaCargoLaboral(cargosLaboralesDTO);





        return dto;
    }









    // Reordenar todos los números
    @Transactional
public void reordenarNumeros() {
    List<Persona> todasLasPersonas = personaRepository.findAllByOrderByIdAsc();
    int numero = 1;
    for (Persona persona : todasLasPersonas) {
        persona.setNumero(numero++);
    }
    personaRepository.saveAll(todasLasPersonas);
    System.out.println("✅ Números reordenados correctamente. Total: " + todasLasPersonas.size());
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
