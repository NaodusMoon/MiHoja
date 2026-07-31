package com.miapp.MiHoja.controller;

import com.miapp.MiHoja.model.*;
import com.miapp.MiHoja.repository.ContactoEmergenciaRepository;
import com.miapp.MiHoja.repository.FormacionRepository;
import com.miapp.MiHoja.repository.InduccionExamenRepository;
import com.miapp.MiHoja.repository.PersonaRepository;
import com.miapp.MiHoja.repository.PersonaCargoLaboralRepository;
import com.miapp.MiHoja.repository.RiesgoProcedenciaRepository;
import com.miapp.MiHoja.repository.SaludRepository;
import com.miapp.MiHoja.service.CampoPersonalizadoService;
import com.miapp.MiHoja.service.PersonaService;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
@Controller
@RequestMapping("/api")
public class InsercionController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private FormacionRepository formacionRepository;

    @Autowired
    private RiesgoProcedenciaRepository riesgoProcedenciaRepository;

    @Autowired
    private SaludRepository saludRepository;

    @Autowired
    private ContactoEmergenciaRepository contactoEmergenciaRepository;

    @Autowired
    private PersonaCargoLaboralRepository personaCargoLaboralRepository;

    @Autowired
    private InduccionExamenRepository induccionExamenRepository;

    @Autowired
    private CampoPersonalizadoService campoPersonalizadoService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final List<DateTimeFormatter> FORMATOS_FECHA = Arrays.asList(
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        new DateTimeFormatterBuilder()
            .appendPattern("d/M/")
            .appendValueReduced(ChronoField.YEAR, 2, 2, 1900)
            .toFormatter()
    );

    private static final List<String> CAMPOS_ORIGINALES = Arrays.asList(
        "nombres","apellidos","cedula","lugarExpedicion","fechaNacimiento",
        "direccion","sexo","numero","correoInstitucional","telefonoInstitucional",
        "enlaceSigep","formacionAcademica","grado","titulo","cargo","codigo","dependencia",
        "fechaIngreso","fechaFirmaContrato","mesesExperiencia","induccion","examen","fechaEgreso",
        "riesgo","medioTransporte","procedencia","dotacion","arl","eps","afp","ccf","rh",
        "carnetVacunacion","nombreEmergencia","parentesco","telefonoEmergencia",
        "enfermedades","alergias","medicamentos"
    );

    private static final List<String> CAMPOS_ESPERADOS;
    static {
        CAMPOS_ESPERADOS = new ArrayList<>(CAMPOS_ORIGINALES);
    }

    private static final Map<String, String> ALIAS_COLUMNAS = new HashMap<>();
    static {
        ALIAS_COLUMNAS.put("examen de ingreso", "examen");
        ALIAS_COLUMNAS.put("examen", "examen");
        ALIAS_COLUMNAS.put("induccion", "induccion");
        ALIAS_COLUMNAS.put("fecha de ingreso", "fechaIngreso");
        ALIAS_COLUMNAS.put("fecha ingreso", "fechaIngreso");
        ALIAS_COLUMNAS.put("dotacion", "dotacion");
        ALIAS_COLUMNAS.put("arl", "arl");
        ALIAS_COLUMNAS.put("eps", "eps");
        ALIAS_COLUMNAS.put("afp", "afp");
        ALIAS_COLUMNAS.put("ccf", "ccf");
        ALIAS_COLUMNAS.put("rh", "rh");
        ALIAS_COLUMNAS.put("carnet de vacunacion", "carnetVacunacion");
        ALIAS_COLUMNAS.put("medicamentos", "medicamentos");
        ALIAS_COLUMNAS.put("medicamento", "medicamentos");
        ALIAS_COLUMNAS.put("alergias", "alergias");
        ALIAS_COLUMNAS.put("enfermedades", "enfermedades");
        ALIAS_COLUMNAS.put("fecha de firma de contrato", "fechaFirmaContrato");
        ALIAS_COLUMNAS.put("fecha firma contrato", "fechaFirmaContrato");
        ALIAS_COLUMNAS.put("en caso de emergencia llamar a", "nombreEmergencia");
        ALIAS_COLUMNAS.put("parentesco", "parentesco");
        ALIAS_COLUMNAS.put("numero telefonico del contacto", "telefonoEmergencia");
        ALIAS_COLUMNAS.put("telefono emergencia", "telefonoEmergencia");
        ALIAS_COLUMNAS.put("cargo", "cargo");
        ALIAS_COLUMNAS.put("dependencia", "dependencia");
        ALIAS_COLUMNAS.put("fecha de nacimiento", "fechaNacimiento");
        ALIAS_COLUMNAS.put("fecha nacimiento", "fechaNacimiento");
        ALIAS_COLUMNAS.put("fecha de egreso", "fechaEgreso");
    }

    private static final Map<String, Map<String, Object>> UPLOAD_JOBS = new ConcurrentHashMap<>();



@PostMapping("/insertar")
public String insertarDesdeFormulario(
        @RequestParam Map<String, String> params,
        RedirectAttributes redirectAttrs) {

    try {
        // === Persona ===
        Persona persona = new Persona();
        persona.setNombres(params.getOrDefault("nombres", ""));
        persona.setApellidos(params.getOrDefault("apellidos", ""));
        persona.setCedula(params.getOrDefault("cedula", ""));
        persona.setLugarExpedicion(params.getOrDefault("lugarExpedicion", ""));

        String fechaNacStr = params.get("fechaNacimiento");
        if (fechaNacStr != null && !fechaNacStr.isBlank()) {
            persona.setFechaNacimiento(LocalDate.parse(fechaNacStr));
        }

        persona.setDireccion(params.getOrDefault("direccion", ""));
        persona.setSexo(params.getOrDefault("sexo", ""));
        persona.setCorreoInstitucional(params.getOrDefault("correoInstitucional", ""));
        persona.setTelefonoInstitucional(params.getOrDefault("telefonoInstitucional", ""));
        persona.setEnlaceSigep(params.getOrDefault("enlaceSigep", ""));
        persona.setEstado(params.getOrDefault("estado", ""));

        String hijosStr = params.get("numero_hijos");
        persona.setNumeroHijos(
                (hijosStr != null && !hijosStr.trim().isEmpty()) ? Integer.parseInt(hijosStr) : 0
        );

        persona.setImagenUrl(params.getOrDefault("imagen_url", ""));
        personaService.guardarConNumero(persona);
        campoPersonalizadoService.guardarValoresDesdeFormulario(persona.getId(), params);

        // === Formación ===
        Formacion formacion = new Formacion();
        formacion.setPersona(persona);
        formacion.setFormacionAcademica(params.getOrDefault("formacionAcademica", ""));
        formacion.setGrado(params.getOrDefault("grado", ""));
        formacion.setTitulo(params.getOrDefault("titulo", ""));
        personaService.guardarFormacion(formacion);

        // === Cargo laboral ===
        CargoLaboral cargo = personaService.obtenerOCrearCargo(
                params.getOrDefault("cargo", ""),
                params.getOrDefault("codigo", ""),
                params.getOrDefault("dependencia", "")
        );

        PersonaCargoLaboral pcl = new PersonaCargoLaboral();
        pcl.setPersona(persona);
        pcl.setCargo(cargo);

        String fechaIngresoStr = params.get("fechaIngreso");
        if (fechaIngresoStr != null && !fechaIngresoStr.isBlank()) {
            pcl.setFechaIngreso(LocalDate.parse(fechaIngresoStr));
        }

        String fechaFirmaStr = params.get("fechaFirmaContrato");
        if (fechaFirmaStr != null && !fechaFirmaStr.isBlank()) {
            pcl.setFechaFirmaContrato(LocalDate.parse(fechaFirmaStr));
        }

        String mesesStr = params.get("mesesExperiencia");
        pcl.setMesesExperiencia(
                (mesesStr != null && !mesesStr.trim().isEmpty()) ? Integer.parseInt(mesesStr) : 0
        );
        personaService.guardarPersonaCargo(pcl);

        // === Inducción y Examen ===
        InduccionExamen ie = new InduccionExamen();
        ie.setPersonaCargoLaboral(pcl);
        ie.setInduccion(parseBooleanCustom(params.get("induccion")));
        ie.setExamenIngreso(parseBooleanCustom(params.get("examen")));

        String fechaEgresoStr = params.get("fechaEgreso");
        if (fechaEgresoStr != null && !fechaEgresoStr.isBlank()) {
            ie.setFechaEgreso(LocalDate.parse(fechaEgresoStr));
        }
        personaService.guardarInduccion(ie);

        // === Riesgo y Procedencia ===
        RiesgoProcedencia rp = new RiesgoProcedencia();
        rp.setPersona(persona);
        rp.setRiesgo(params.getOrDefault("riesgo", ""));
        rp.setMedioTransporte(params.getOrDefault("medioTransporte", ""));

        String procedencia = params.getOrDefault("procedencia", "");
        if ("Otro".equalsIgnoreCase(procedencia)) {
            rp.setProcedenciaTrabajador(params.getOrDefault("otraProcedencia", ""));
        } else {
            rp.setProcedenciaTrabajador(procedencia);
        }
        personaService.guardarRiesgo(rp);

        // === Salud ===
        Salud salud = new Salud();
        salud.setPersona(persona);
        salud.setDotacion(params.getOrDefault("dotacion", ""));
        salud.setArl(params.getOrDefault("arl", ""));
        salud.setEps(params.getOrDefault("eps", ""));
        salud.setAfp(params.getOrDefault("afp", ""));
        salud.setCcf(params.getOrDefault("ccf", ""));
        salud.setRh(params.getOrDefault("rh", ""));
        salud.setCarnetVacunacion(parseBooleanCustom(params.get("carnetVacunacion")));
        personaService.guardarSalud(salud);

        // === Contacto de emergencia ===
        ContactoEmergencia contacto = new ContactoEmergencia();
        contacto.setPersona(persona);
        contacto.setNombreContactoEmergencia(params.getOrDefault("nombreEmergencia", ""));
        contacto.setParentesco(params.getOrDefault("parentesco", ""));
        contacto.setTelefonoContactoEmergencia(params.getOrDefault("telefonoEmergencia", ""));
        personaService.guardarContactoEmergencia(contacto);

        // === Enfermedades (independientes) ===
        String enfermedadesStr = params.get("enfermedades");
        if (enfermedadesStr != null && !enfermedadesStr.isBlank()) {
            String[] enfermedades = enfermedadesStr.split(",");
            for (String enf : enfermedades) {
                if (!enf.trim().isEmpty()) {
                    Enfermedad enfermedad = personaService.obtenerOCrearEnfermedad(enf.trim(), persona);
                    personaService.guardarEnfermedad(enfermedad);
                }
            }
        }

        // === Alergias (independientes) ===
        String alergiasStr = params.get("alergias");
        if (alergiasStr != null && !alergiasStr.isBlank()) {
            String[] alergias = alergiasStr.split(",");
            for (String aler : alergias) {
                if (!aler.trim().isEmpty()) {
                    Alergia alergia = new Alergia();
                    alergia.setPersona(persona);
                    alergia.setNombre(aler.trim());
                    personaService.guardarAlergia(alergia);
                }
            }
        }

        // === Medicamentos (independientes) ===
        String medicamentosStr = params.get("medicamentos");
        if (medicamentosStr != null && !medicamentosStr.isBlank()) {
            String[] medicamentos = medicamentosStr.split(",");
            for (String med : medicamentos) {
                if (!med.trim().isEmpty()) {
                    Medicamento medicamento = personaService.obtenerOCrearMedicamento(med.trim(), persona);
                    personaService.guardarMedicamento(medicamento);
                }
            }
        }

        redirectAttrs.addFlashAttribute("mensajeExito", "✅ Persona y todos los datos guardados correctamente.");

    } catch (Exception e) {
        e.printStackTrace();
        redirectAttrs.addFlashAttribute("mensajeError", "❌ Error al guardar los datos: " + e.getMessage());
    }

    return "redirect:/insertar";
}









      @Transactional
@PostMapping(value = "/insertar/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<String> insertarDesdeArchivo(@RequestParam("file") MultipartFile file) {
    int creados = 0;
    int actualizados = 0;
    int filasConError = 0;
    List<String> detalleErrores = new ArrayList<>();

    if (file == null || file.isEmpty()) {
        return ResponseEntity.badRequest().body("Error: no se recibió archivo o está vacío.");
    }

    try (InputStream is = file.getInputStream()) {
        return ResponseEntity.ok(procesarArchivoExcel(is));
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Error al procesar el archivo Excel: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }
}

@PostMapping(value = "/upload-jobs/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Map<String, Object>> iniciarCargaExcelEnSegundoPlano(@RequestParam("file") MultipartFile file) {
    if (file == null || file.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of(
                "ok", false,
                "message", "No se recibiÃ³ archivo o estÃ¡ vacÃ­o."
        ));
    }

    try {
        byte[] contenido = file.getBytes();
        String jobId = UUID.randomUUID().toString();
        Map<String, Object> estado = new ConcurrentHashMap<>();
        estado.put("id", jobId);
        estado.put("status", "processing");
        estado.put("title", "Procesando carga");
        estado.put("message", "Importando datos del archivo en segundo plano...");
        UPLOAD_JOBS.put(jobId, estado);

        CompletableFuture.runAsync(() -> {
            try (InputStream is = new ByteArrayInputStream(contenido)) {
                String resumen = procesarArchivoExcel(is);
                estado.put("status", "completed");
                estado.put("title", "Carga completada");
                estado.put("message", resumen);
            } catch (Exception ex) {
                estado.put("status", "failed");
                estado.put("title", "Carga interrumpida");
                estado.put("message", ex.getMessage());
            }
        });

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "ok", true,
                "jobId", jobId,
                "message", "La importaciÃ³n quedÃ³ ejecutÃ¡ndose en segundo plano."
        ));
    } catch (Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of(
                "ok", false,
                "message", ex.getMessage()
        ));
    }
}

@GetMapping(value = "/upload-jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Map<String, Object>> consultarCargaExcel(@PathVariable String jobId) {
    Map<String, Object> estado = UPLOAD_JOBS.get(jobId);
    if (estado == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "ok", false,
                "message", "No se encontrÃ³ la carga solicitada."
        ));
    }
    return ResponseEntity.ok(estado);
}

private String procesarArchivoExcel(InputStream is) throws Exception {
    int creados = 0;
    int actualizados = 0;
    int filasConError = 0;
    List<String> detalleErrores = new ArrayList<>();

    try (Workbook workbook = WorkbookFactory.create(is)) {
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("El archivo no tiene encabezados.");
        }

        Map<String, Integer> colIndex = mapearColumnasConJaroWinkler(headerRow);
        Integer idxCarro = buscarColumna(headerRow, "carro");
        Integer idxMoto = buscarColumna(headerRow, "moto");
        Map<Integer, String> camposCustomPorColumna = sincronizarCamposPersonalizadosDesdeEncabezado(headerRow, colIndex, idxCarro, idxMoto);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || esFilaVacia(row)) {
                continue;
            }

            try {
                String nombresRaw = normalizarTexto(getCellValue(row, colIndex.get("nombres")));
                String apellidosRaw = normalizarTexto(getCellValue(row, colIndex.get("apellidos")));
                if (esNoDisponible(apellidosRaw) && !esNoDisponible(nombresRaw)) {
                    String[] partes = dividirNombreCompleto(nombresRaw);
                    nombresRaw = partes[0];
                    apellidosRaw = partes[1];
                }

                String cedulaRaw = normalizarTexto(getCellValue(row, colIndex.get("cedula")));
                String correoRaw = normalizarTexto(getCellValue(row, colIndex.get("correoInstitucional")));
                String telefonoRaw = normalizarTexto(getCellValue(row, colIndex.get("telefonoInstitucional")));
                String transporteRaw = resolverTransporte(
                        getCellValue(row, colIndex.get("medioTransporte")),
                        getCellValue(row, idxCarro),
                        getCellValue(row, idxMoto)
                );

                Optional<Persona> existente = buscarPersonaExistente(cedulaRaw, correoRaw, telefonoRaw);
                Persona persona;
                boolean esNuevo = existente.isEmpty();

                if (esNuevo) {
                    persona = new Persona();
                    persona.setNombres(valorFinal(nombresRaw));
                    persona.setApellidos(valorFinal(apellidosRaw));
                    persona.setCedula(generarCedulaSiFalta(cedulaRaw));
                    persona.setLugarExpedicion(valorFinal(normalizarTexto(getCellValue(row, colIndex.get("lugarExpedicion")))));
                    persona.setFechaNacimiento(parseFecha(getCellValue(row, colIndex.get("fechaNacimiento"))));
                    persona.setDireccion(valorFinal(normalizarTexto(getCellValue(row, colIndex.get("direccion")))));
                    persona.setSexo(valorFinal(normalizarTexto(getCellValue(row, colIndex.get("sexo")))));
                    persona.setCorreoInstitucional(valorFinal(correoRaw));
                    persona.setTelefonoInstitucional(valorFinal(telefonoRaw));
                    persona.setEnlaceSigep(valorFinal(normalizarTexto(getCellValue(row, colIndex.get("enlaceSigep")))));
                    persona.setEstado("NO DISPONIBLE");
                    persona.setNumeroHijos(0);
                    persona = personaService.guardarConNumero(persona);
                    persona = personaService.obtenerPersonaConRelaciones(persona.getId());
                    creados++;
                } else {
                    persona = personaService.obtenerPersonaConRelaciones(existente.get().getId());
                    persona.setNombres(completarSiFalta(persona.getNombres(), nombresRaw));
                    persona.setApellidos(completarSiFalta(persona.getApellidos(), apellidosRaw));
                    persona.setCedula(completarCedula(persona.getCedula(), cedulaRaw));
                    persona.setLugarExpedicion(completarSiFalta(persona.getLugarExpedicion(), normalizarTexto(getCellValue(row, colIndex.get("lugarExpedicion")))));
                    if (persona.getFechaNacimiento() == null) {
                        persona.setFechaNacimiento(parseFecha(getCellValue(row, colIndex.get("fechaNacimiento"))));
                    }
                    persona.setDireccion(completarSiFalta(persona.getDireccion(), normalizarTexto(getCellValue(row, colIndex.get("direccion")))));
                    persona.setSexo(completarSiFalta(persona.getSexo(), normalizarTexto(getCellValue(row, colIndex.get("sexo")))));
                    persona.setCorreoInstitucional(completarSiFalta(persona.getCorreoInstitucional(), correoRaw));
                    persona.setTelefonoInstitucional(completarSiFalta(persona.getTelefonoInstitucional(), telefonoRaw));
                    persona.setEnlaceSigep(completarSiFalta(persona.getEnlaceSigep(), normalizarTexto(getCellValue(row, colIndex.get("enlaceSigep")))));
                    personaService.guardar(persona);
                    actualizados++;
                }

                Set<Formacion> formacionesPersona = persona.getFormaciones() == null ? Collections.emptySet() : persona.getFormaciones();
                Formacion formacion = formacionesPersona.stream().findFirst().orElseGet(Formacion::new);
                formacion.setPersona(persona);
                formacion.setFormacionAcademica(completarSiFalta(formacion.getFormacionAcademica(), normalizarTexto(getCellValue(row, colIndex.get("formacionAcademica")))));
                formacion.setGrado(completarSiFalta(formacion.getGrado(), normalizarTexto(getCellValue(row, colIndex.get("grado")))));
                formacion.setTitulo(completarSiFalta(formacion.getTitulo(), normalizarTexto(getCellValue(row, colIndex.get("titulo")))));
                formacionRepository.save(formacion);

                Set<RiesgoProcedencia> riesgosPersona = persona.getRiesgoProcedencias() == null ? Collections.emptySet() : persona.getRiesgoProcedencias();
                RiesgoProcedencia riesgo = riesgosPersona.stream().findFirst().orElseGet(RiesgoProcedencia::new);
                riesgo.setPersona(persona);
                riesgo.setRiesgo(completarSiFalta(riesgo.getRiesgo(), normalizarTexto(getCellValue(row, colIndex.get("riesgo")))));
                riesgo.setMedioTransporte(completarSiFalta(riesgo.getMedioTransporte(), transporteRaw));
                riesgo.setProcedenciaTrabajador(completarSiFalta(riesgo.getProcedenciaTrabajador(), normalizarTexto(getCellValue(row, colIndex.get("procedencia")))));
                riesgoProcedenciaRepository.save(riesgo);

                Set<Salud> saludPersona = persona.getRegistrosSalud() == null ? Collections.emptySet() : persona.getRegistrosSalud();
                Salud salud = saludPersona.stream().findFirst().orElseGet(Salud::new);
                salud.setPersona(persona);
                salud.setDotacion(completarSiFalta(salud.getDotacion(), normalizarTexto(getCellValue(row, colIndex.get("dotacion")))));
                salud.setArl(completarSiFalta(salud.getArl(), normalizarTexto(getCellValue(row, colIndex.get("arl")))));
                salud.setEps(completarSiFalta(salud.getEps(), normalizarTexto(getCellValue(row, colIndex.get("eps")))));
                salud.setAfp(completarSiFalta(salud.getAfp(), normalizarTexto(getCellValue(row, colIndex.get("afp")))));
                salud.setCcf(completarSiFalta(salud.getCcf(), normalizarTexto(getCellValue(row, colIndex.get("ccf")))));
                salud.setRh(completarSiFalta(salud.getRh(), normalizarTexto(getCellValue(row, colIndex.get("rh")))));
                if (salud.getCarnetVacunacion() == null) {
                    salud.setCarnetVacunacion(parseBooleanCustom(getCellValue(row, colIndex.get("carnetVacunacion"))));
                }
                saludRepository.save(salud);

                Set<ContactoEmergencia> contactosPersona = persona.getContactosEmergencia() == null ? Collections.emptySet() : persona.getContactosEmergencia();
                ContactoEmergencia contacto = contactosPersona.stream().findFirst().orElseGet(ContactoEmergencia::new);
                contacto.setPersona(persona);
                contacto.setNombreContactoEmergencia(completarSiFalta(contacto.getNombreContactoEmergencia(), normalizarTexto(getCellValue(row, colIndex.get("nombreEmergencia")))));
                contacto.setParentesco(completarSiFalta(contacto.getParentesco(), normalizarTexto(getCellValue(row, colIndex.get("parentesco")))));
                contacto.setTelefonoContactoEmergencia(completarSiFalta(contacto.getTelefonoContactoEmergencia(), normalizarTexto(getCellValue(row, colIndex.get("telefonoEmergencia")))));
                contactoEmergenciaRepository.save(contacto);

                String cargoValor = valorFinal(normalizarTexto(getCellValue(row, colIndex.get("cargo"))));
                String codigoValor = valorFinal(normalizarTexto(getCellValue(row, colIndex.get("codigo"))));
                String dependenciaValor = valorFinal(normalizarTexto(getCellValue(row, colIndex.get("dependencia"))));
                CargoLaboral cargo = personaService.obtenerOCrearCargo(cargoValor, codigoValor, dependenciaValor);

                Set<PersonaCargoLaboral> cargosPersona = persona.getCargosLaborales() == null ? Collections.emptySet() : persona.getCargosLaborales();
                PersonaCargoLaboral pcl = cargosPersona.stream().findFirst().orElseGet(PersonaCargoLaboral::new);
                pcl.setPersona(persona);
                pcl.setCargo(cargo);
                if (pcl.getFechaIngreso() == null) {
                    pcl.setFechaIngreso(parseFecha(getCellValue(row, colIndex.get("fechaIngreso"))));
                }
                if (pcl.getFechaFirmaContrato() == null) {
                    pcl.setFechaFirmaContrato(parseFecha(getCellValue(row, colIndex.get("fechaFirmaContrato"))));
                }
                if (pcl.getMesesExperiencia() == null) {
                    Integer meses = parseInteger(getCellValue(row, colIndex.get("mesesExperiencia")));
                    if (meses != null) {
                        pcl.setMesesExperiencia(meses);
                    }
                }
                pcl = personaCargoLaboralRepository.save(pcl);

                InduccionExamen induccion = induccionExamenRepository.findFirstByPersonaCargoLaboralId(pcl.getId())
                        .orElseGet(InduccionExamen::new);
                induccion.setPersonaCargoLaboral(pcl);
                if (induccion.getInduccion() == null) {
                    induccion.setInduccion(parseBooleanCustom(getCellValue(row, colIndex.get("induccion"))));
                }
                if (induccion.getExamenIngreso() == null) {
                    induccion.setExamenIngreso(parseBooleanCustom(getCellValue(row, colIndex.get("examen"))));
                }
                if (induccion.getFechaEgreso() == null) {
                    induccion.setFechaEgreso(parseFecha(getCellValue(row, colIndex.get("fechaEgreso"))));
                }
                induccionExamenRepository.save(induccion);

                guardarEnfermedadesDesdeExcel(persona, getCellValue(row, colIndex.get("enfermedades")));
                guardarAlergiasDesdeExcel(persona, getCellValue(row, colIndex.get("alergias")));
                guardarMedicamentosDesdeExcel(persona, getCellValue(row, colIndex.get("medicamentos")));
                guardarCamposPersonalizadosDesdeExcel(persona, row, camposCustomPorColumna);
            } catch (Exception filaEx) {
                filasConError++;
                if (detalleErrores.size() < 10) {
                    detalleErrores.add("Fila " + (i + 1) + ": " + filaEx.getMessage());
                }
            }
        }

        StringBuilder resumen = new StringBuilder("Carga completada. Creados: ")
                .append(creados)
                .append(" | Actualizados: ")
                .append(actualizados)
                .append(" | Filas con error: ")
                .append(filasConError);
        if (!detalleErrores.isEmpty()) {
            resumen.append(" | Detalle: ").append(String.join(" || ", detalleErrores));
        }
        return resumen.toString();
    }
}
        // === MÉTODOS AUXILIARES ===

    private Integer buscarColumna(Row headerRow, String clave) {
        if (headerRow == null) return null;
        String objetivo = normalizarNombreColumna(clave);
        for (Cell cell : headerRow) {
            String nombre = normalizarNombreColumna(getCellValue(cell));
            if (nombre.contains(objetivo)) return cell.getColumnIndex();
        }
        return null;
    }

    private Optional<Persona> buscarPersonaExistente(String cedula, String correo, String telefono) {
        if (!esNoDisponible(cedula)) {
            Optional<Persona> porCedula = personaRepository.findFirstByCedulaIgnoreCase(cedula);
            if (porCedula.isPresent()) return porCedula;
        }
        return Optional.empty();
    }

    private String valorFinal(String value) {
        return esNoDisponible(value) ? "NO DISPONIBLE" : value;
    }

    private String completarSiFalta(String actual, String nuevo) {
        if (!esNoDisponible(actual)) return actual;
        return valorFinal(nuevo);
    }

    private String completarCedula(String actual, String nuevo) {
        if (!esNoDisponible(actual) && !actual.startsWith("NO DISPONIBLE_")) return actual;
        return generarCedulaSiFalta(nuevo);
    }

    private String generarCedulaSiFalta(String cedula) {
        if (esNoDisponible(cedula)) return "NO DISPONIBLE_" + UUID.randomUUID().toString().substring(0, 8);
        return cedula;
    }

    private String normalizarTexto(String value) {
        if (value == null) return "NO DISPONIBLE";
        String limpio = value.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        return limpio.isEmpty() ? "NO DISPONIBLE" : limpio;
    }

    private boolean esNoDisponible(String value) {
        if (value == null) return true;
        String v = value.trim();
        return v.isEmpty() || v.equalsIgnoreCase("NO DISPONIBLE") || v.equalsIgnoreCase("N/A")
                || v.equalsIgnoreCase("NA") || v.equalsIgnoreCase("NULL");
    }

    private String[] dividirNombreCompleto(String nombreCompleto) {
        String limpio = normalizarTexto(nombreCompleto);
        if (esNoDisponible(limpio)) return new String[]{"NO DISPONIBLE", "NO DISPONIBLE"};
        String[] partes = limpio.split(" ");
        if (partes.length < 2) return new String[]{limpio, "NO DISPONIBLE"};
        int mitad = Math.max(1, partes.length / 2);
        String nombres = String.join(" ", Arrays.copyOfRange(partes, 0, mitad));
        String apellidos = String.join(" ", Arrays.copyOfRange(partes, mitad, partes.length));
        return new String[]{valorFinal(nombres), valorFinal(apellidos)};
    }

    private String resolverTransporte(String medioTexto, String marcaCarro, String marcaMoto) {
        String texto = normalizarNombreColumna(medioTexto);
        boolean carro = texto.contains("carro") || texto.contains("auto");
        boolean moto = texto.contains("moto");
        carro = carro || marcaTransporte(marcaCarro);
        moto = moto || marcaTransporte(marcaMoto);
        if (carro && moto) return "CARRO,MOTO";
        if (carro) return "CARRO";
        if (moto) return "MOTO";
        if (texto.contains("ninguno") || texto.contains("ningun")) return "NINGUNO";
        return "NO DISPONIBLE";
    }

    private boolean marcaTransporte(String valor) {
        if (valor == null) return false;
        String v = normalizarNombreColumna(valor);
        return v.equals("x") || v.equals("si") || v.equals("1") || v.equals("true");
    }

        private boolean parseBooleanCustom(String valor) {
    if (valor == null) return false;
    valor = valor.trim().toLowerCase();
    return valor.equals("true") || valor.equals("si") || valor.equals("sí") || valor.equals("1") || valor.equals("x");
}


    private Map<Integer, String> sincronizarCamposPersonalizadosDesdeEncabezado(Row headerRow,
                                                                                Map<String, Integer> colIndex,
                                                                                Integer idxCarro,
                                                                                Integer idxMoto) {
        Map<Integer, String> camposCustomPorColumna = new LinkedHashMap<>();
        if (headerRow == null) return camposCustomPorColumna;
        Set<Integer> columnasSistema = new HashSet<>(colIndex.values());
        if (idxCarro != null) columnasSistema.add(idxCarro);
        if (idxMoto != null) columnasSistema.add(idxMoto);

        for (Cell cell : headerRow) {
            if (columnasSistema.contains(cell.getColumnIndex())) continue;
            String nombre = getCellValue(cell);
            if (nombre == null || nombre.trim().isEmpty()) continue;
            CampoPersonalizado campo = campoPersonalizadoService.crearCampo(nombre.trim());
            camposCustomPorColumna.put(cell.getColumnIndex(), campo.getNombre());
        }
        return camposCustomPorColumna;
    }

    private void guardarCamposPersonalizadosDesdeExcel(Persona persona,
                                                       Row row,
                                                       Map<Integer, String> camposCustomPorColumna) {
        if (persona == null || row == null || camposCustomPorColumna == null || camposCustomPorColumna.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, String> entry : camposCustomPorColumna.entrySet()) {
            String valor = getCellValue(row, entry.getKey());
            if (valor == null || valor.trim().isEmpty()) {
                continue;
            }
            campoPersonalizadoService.guardarValorPorNombre(persona, entry.getValue(), valor.trim());
        }
    }

    private void guardarEnfermedadesDesdeExcel(Persona persona, String valorCelda) {
        Set<Enfermedad> actuales = persona.getEnfermedades() == null ? Collections.emptySet() : persona.getEnfermedades();
        for (String nombre : dividirValores(valorCelda)) {
            boolean existe = actuales.stream()
                    .anyMatch(e -> e.getNombre() != null && e.getNombre().equalsIgnoreCase(nombre));
            if (!existe) {
                Enfermedad enfermedad = new Enfermedad();
                enfermedad.setPersona(persona);
                enfermedad.setNombre(nombre);
                personaService.guardarEnfermedad(enfermedad);
            }
        }
    }

    private void guardarAlergiasDesdeExcel(Persona persona, String valorCelda) {
        Set<Alergia> actuales = persona.getAlergias() == null ? Collections.emptySet() : persona.getAlergias();
        for (String nombre : dividirValores(valorCelda)) {
            boolean existe = actuales.stream()
                    .anyMatch(a -> a.getNombre() != null && a.getNombre().equalsIgnoreCase(nombre));
            if (!existe) {
                Alergia alergia = new Alergia();
                alergia.setPersona(persona);
                alergia.setNombre(nombre);
                personaService.guardarAlergia(alergia);
            }
        }
    }

    private void guardarMedicamentosDesdeExcel(Persona persona, String valorCelda) {
        Set<Medicamento> actuales = persona.getMedicamentos() == null ? Collections.emptySet() : persona.getMedicamentos();
        for (String nombre : dividirValores(valorCelda)) {
            boolean existe = actuales.stream()
                    .anyMatch(m -> m.getNombre() != null && m.getNombre().equalsIgnoreCase(nombre));
            if (!existe) {
                Medicamento medicamento = new Medicamento();
                medicamento.setPersona(persona);
                medicamento.setNombre(nombre);
                personaService.guardarMedicamento(medicamento);
            }
        }
    }

    private List<String> dividirValores(String valorCelda) {
        String normalizado = normalizarTexto(valorCelda);
        if (esNoDisponible(normalizado)) return Collections.emptyList();
        String[] partes = normalizado.split("[,;\\n]+");
        List<String> valores = new ArrayList<>();
        for (String parte : partes) {
            String limpio = parte.trim();
            if (!limpio.isEmpty() && !esNoDisponible(limpio)) {
                valores.add(limpio);
            }
        }
        return valores;
    }

    private Map<String, Integer> mapearColumnasConJaroWinkler(Row headerRow) {
        Map<String, Integer> mapeo = new HashMap<>();
        JaroWinklerSimilarity jw = new JaroWinklerSimilarity();

        for (Cell cell : headerRow) {
            String valor = getCellValue(cell);
            String valorOriginal = valor.trim().toLowerCase(); // sin normalizar
            String normalizado = normalizarNombreColumna(valorOriginal);
            double maxSimilitud = 0.70;
            String campoEncontrado = null;

            // Buscar alias exactos primero
            String normalizadoAlias = normalizarNombreColumna(valorOriginal);
if (ALIAS_COLUMNAS.containsKey(valorOriginal) || ALIAS_COLUMNAS.containsKey(normalizadoAlias)) {
    campoEncontrado = ALIAS_COLUMNAS.getOrDefault(valorOriginal, ALIAS_COLUMNAS.get(normalizadoAlias));
    mapeo.put(campoEncontrado, cell.getColumnIndex());
    continue;
}

            // Comparar contra todos los campos esperados
            for (String campoEsperado : CAMPOS_ESPERADOS) {
                double sim = jw.apply(normalizado, campoEsperado.toLowerCase());
                if (sim > maxSimilitud) {
                    maxSimilitud = sim;
                    campoEncontrado = campoEsperado;
                }
            }

            if (campoEncontrado != null) {
                mapeo.put(campoEncontrado, cell.getColumnIndex());
            }
        }

        return mapeo;
    }

    private String getCellValue(Row row, Integer index) {
        if (index == null || row == null) return "";
        Cell cell = row.getCell(index);
        return getCellValue(cell);
    }

    private String getCellValue(Cell cell) {
    if (cell == null) return "";

    // ✅ Si es numérico y es fecha (Excel Date)
    if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
        return cell.getLocalDateTimeCellValue().toLocalDate().toString(); // yyyy-MM-dd
    }

    // ✅ Si es texto normal
    DataFormatter formatter = new DataFormatter();
    return formatter.formatCellValue(cell).trim();
}


    private LocalDate parseFecha(String valor) {
    if (valor == null) return null;
    valor = valor.trim();

    // ✅ Ignorar valores explícitamente no válidos
    if (valor.isEmpty() || valor.equalsIgnoreCase("NO DISPONIBLE") || valor.equalsIgnoreCase("SI")) {
        return null;
    }

    for (DateTimeFormatter formato : FORMATOS_FECHA) {
        try {
            return LocalDate.parse(valor, formato);
        } catch (DateTimeParseException ignored) {}
    }

    return null;
}


    private Integer parseInteger(String valor) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String normalizarNombreColumna(String nombre) {
        if (nombre == null) return "";
        String limpio = Normalizer.normalize(nombre, Normalizer.Form.NFD);
        limpio = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(limpio).replaceAll("");
        limpio = limpio.toLowerCase().replaceAll("[^a-z0-9]", "");
        return limpio;
    }

    private boolean esFilaVacia(Row row) {
    if (row == null) return true;
    for (Cell cell : row) {
        if (cell != null && cell.getCellType() != CellType.BLANK) {
            String valor = getCellValue(cell);
            if (valor != null && !valor.trim().isEmpty()) {
                return false;
            }
        }
    }
    return true;
}

}


