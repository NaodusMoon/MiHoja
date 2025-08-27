package com.miapp.MiHoja.controller;

import com.miapp.MiHoja.model.*;
import com.miapp.MiHoja.service.PersonaService;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Pattern;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Controller
@RequestMapping("/api")
public class InsercionController {

    @Autowired
    private PersonaService personaService;

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
        ie.setInduccion(Boolean.parseBoolean(params.getOrDefault("induccion", "false")));
        ie.setExamenIngreso(Boolean.parseBoolean(params.getOrDefault("examen", "false")));

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
        salud.setCarnetVacunacion(Boolean.parseBoolean(params.getOrDefault("carnetVacunacion", "false")));
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









      @PostMapping(value = "/insertar/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<String> insertarDesdeArchivo(@RequestParam("file") MultipartFile file) {
    try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> colIndex = mapearColumnasConJaroWinkler(headerRow);

        // ✅ Log para ver qué columnas fueron mapeadas
        System.out.println("📌 Mapeo detectado: " + colIndex);

        // ✅ 1. PRIMERO: Guardar solo personas
        List<Persona> personasLote = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || esFilaVacia(row)) continue;

            Persona persona = new Persona();
            persona.setNombres(getCellValue(row, colIndex.get("nombres")));
            persona.setApellidos(getCellValue(row, colIndex.get("apellidos")));

            String cedulaExcel = getCellValue(row, colIndex.get("cedula"));
            if (cedulaExcel == null || cedulaExcel.trim().isEmpty() || cedulaExcel.equalsIgnoreCase("NO DISPONIBLE")) {
                persona.setCedula("NO DISPONIBLE_" + UUID.randomUUID().toString().substring(0, 8));
            } else {
                persona.setCedula(cedulaExcel.trim());
            }

            persona.setLugarExpedicion(getCellValue(row, colIndex.get("lugarExpedicion")));
            persona.setFechaNacimiento(parseFecha(getCellValue(row, colIndex.get("fechaNacimiento"))));
            persona.setDireccion(getCellValue(row, colIndex.get("direccion")));
            persona.setSexo(getCellValue(row, colIndex.get("sexo")));
            persona.setCorreoInstitucional(getCellValue(row, colIndex.get("correoInstitucional")));
            persona.setTelefonoInstitucional(getCellValue(row, colIndex.get("telefonoInstitucional")));
            persona.setEnlaceSigep(getCellValue(row, colIndex.get("enlaceSigep")));

            personasLote.add(persona);
        }

        // ✅ Guardar personas primero para que tengan N e ID generados
        personasLote = personaService.guardarPersonasEnLote(personasLote);

        // ✅ Reordenar números SOLO para las nuevas personas (optimizado)
        personaService.reordenarNumeros();


        // ✅ 2. SEGUNDO: Crear relaciones usando personas ya persistidas (con N asignado)
        List<Formacion> formacionesLote = new ArrayList<>();
        List<PersonaCargoLaboral> cargosLote = new ArrayList<>();
        List<InduccionExamen> induccionesLote = new ArrayList<>();
        List<RiesgoProcedencia> riesgosLote = new ArrayList<>();
        List<Salud> saludLote = new ArrayList<>();
        List<ContactoEmergencia> contactosLote = new ArrayList<>();
        List<Enfermedad> enfermedadesLote = new ArrayList<>();
        List<Alergia> alergiasLote = new ArrayList<>();

        // ✅ Pre-cargar todos los cargos existentes en un mapa (evita consultas repetidas)
        Map<String, CargoLaboral> cargosCache = personaService.obtenerTodosLosCargosComoMapa();

        // ✅ Nuevo índice sincronizado (evita el problema de filas vacías)
        int indexPersona = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || esFilaVacia(row)) continue;

            Persona persona = personasLote.get(indexPersona);
            indexPersona++;

            System.out.println("📌 Persona guardada con N: " + persona.getNumero());

            // Formación
            Formacion formacion = new Formacion();
            formacion.setPersona(persona);
            formacion.setFormacionAcademica(getCellValue(row, colIndex.get("formacionAcademica")));
            formacion.setGrado(getCellValue(row, colIndex.get("grado")));
            formacion.setTitulo(getCellValue(row, colIndex.get("titulo")));
            formacionesLote.add(formacion);
 
            // Cargo optimizado con cache
            String keyCargo = (getCellValue(row, colIndex.get("cargo")) + "|" +
                               getCellValue(row, colIndex.get("codigo")) + "|" +
                               getCellValue(row, colIndex.get("dependencia"))).toUpperCase();

            CargoLaboral cargo = cargosCache.computeIfAbsent(keyCargo, k -> {
                CargoLaboral nuevo = new CargoLaboral();
                nuevo.setCargo(getCellValue(row, colIndex.get("cargo")));
                nuevo.setCodigo(getCellValue(row, colIndex.get("codigo")));
                nuevo.setDependencia(getCellValue(row, colIndex.get("dependencia")));
                return nuevo;
            });

            PersonaCargoLaboral pcl = new PersonaCargoLaboral();
            pcl.setPersona(persona);
            pcl.setCargo(cargo);

            // ✅ Log para depurar fechaFirmaContrato
            String fechaFirmaRaw = getCellValue(row, colIndex.get("fechaFirmaContrato"));
            LocalDate fechaFirmaParseada = parseFecha(fechaFirmaRaw);
            System.out.println("📌 Fila " + i +
                    " | Valor crudo Excel FechaFirmaContrato: '" + fechaFirmaRaw + "'" +
                    " | Parseado: " + fechaFirmaParseada);

            pcl.setFechaIngreso(parseFecha(getCellValue(row, colIndex.get("fechaIngreso"))));
            pcl.setFechaFirmaContrato(fechaFirmaParseada);
            pcl.setMesesExperiencia(parseInteger(getCellValue(row, colIndex.get("mesesExperiencia"))));
            cargosLote.add(pcl);

            // Inducción
            InduccionExamen ie = new InduccionExamen();
            ie.setPersonaCargoLaboral(pcl);
            ie.setInduccion(parseBooleanCustom(getCellValue(row, colIndex.get("induccion"))));
            ie.setExamenIngreso(parseBooleanCustom(getCellValue(row, colIndex.get("examen"))));
            ie.setFechaEgreso(parseFecha(getCellValue(row, colIndex.get("fechaEgreso"))));
            induccionesLote.add(ie);

            // Riesgo
            RiesgoProcedencia rp = new RiesgoProcedencia();
            rp.setPersona(persona);
            rp.setRiesgo(getCellValue(row, colIndex.get("riesgo")));
            rp.setMedioTransporte(getCellValue(row, colIndex.get("medioTransporte")));
            rp.setProcedenciaTrabajador(getCellValue(row, colIndex.get("procedencia")));
            riesgosLote.add(rp);

            // Salud
            Salud salud = new Salud();
            salud.setPersona(persona);
            salud.setDotacion(getCellValue(row, colIndex.get("dotacion")));
            salud.setArl(getCellValue(row, colIndex.get("arl")));
            salud.setEps(getCellValue(row, colIndex.get("eps")));
            salud.setAfp(getCellValue(row, colIndex.get("afp")));
            salud.setCcf(getCellValue(row, colIndex.get("ccf")));
            salud.setRh(getCellValue(row, colIndex.get("rh")));
            salud.setCarnetVacunacion(parseBooleanCustom(getCellValue(row, colIndex.get("carnetVacunacion"))));
            saludLote.add(salud);

            // Contacto
            ContactoEmergencia contacto = new ContactoEmergencia();
            contacto.setPersona(persona);
            contacto.setNombreContactoEmergencia(getCellValue(row, colIndex.get("nombreEmergencia")));
            contacto.setParentesco(getCellValue(row, colIndex.get("parentesco")));
            contacto.setTelefonoContactoEmergencia(getCellValue(row, colIndex.get("telefonoEmergencia")));
            contactosLote.add(contacto);

            // Enfermedades
            String enfermedadesRaw = getCellValue(row, colIndex.get("enfermedades"));
            if (enfermedadesRaw != null && !enfermedadesRaw.isEmpty()) {
                Arrays.stream(enfermedadesRaw.split(",")).forEach(nombre -> {
                    nombre = nombre.trim();
                    if (!nombre.isEmpty()) {
                        Enfermedad enf = new Enfermedad();
                        enf.setPersona(persona);
                        enf.setNombre(nombre);
                        enfermedadesLote.add(enf);
                    }
                });
            }

            // Alergias
            String alergiasRaw = getCellValue(row, colIndex.get("alergias"));
            if (alergiasRaw != null && !alergiasRaw.isEmpty()) {
                Arrays.stream(alergiasRaw.split(",")).forEach(nombre -> {
                    nombre = nombre.trim();
                    if (!nombre.isEmpty()) {
                        Alergia alergia = new Alergia();
                        alergia.setPersona(persona);
                        alergia.setNombre(nombre);
                        alergiasLote.add(alergia);
                    }
                });
            }
        }

        // ✅ 3. Guardar nuevas relaciones en lote
        personaService.guardarCargosEnLote(new ArrayList<>(cargosCache.values()));
        personaService.guardarFormacionesEnLote(formacionesLote);
        personaService.guardarPersonaCargoEnLote(cargosLote);

        cargosLote.forEach(c ->
            System.out.println("✅ Guardado en lote: " +
                c.getPersona().getCedula() +
                " | FechaFirmaContrato=" + c.getFechaFirmaContrato())
        );

        personaService.guardarInduccionesEnLote(induccionesLote);
        personaService.guardarRiesgosEnLote(riesgosLote);
        personaService.guardarSaludEnLote(saludLote);
        personaService.guardarContactosEnLote(contactosLote);
        personaService.guardarEnfermedadesEnLote(enfermedadesLote);
        personaService.guardarAlergiasEnLote(alergiasLote);

        return ResponseEntity.ok("✅ Archivo cargado e información insertada en lote correctamente.");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("❌ Error al procesar el archivo Excel: " + e.getMessage());
    }
}







        // === MÉTODOS AUXILIARES ===

        private boolean parseBooleanCustom(String valor) {
    if (valor == null) return false;
    valor = valor.trim().toLowerCase();
    return valor.equals("true") || valor.equals("si") || valor.equals("sí") || valor.equals("1") || valor.equals("x");
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
