package com.miapp.MiHoja.controller;

import com.miapp.MiHoja.model.*;
import com.miapp.MiHoja.service.PersonaService;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Pattern;

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

    // === Inserción desde formulario web manual ===
    @PostMapping("/insertar")
    public String insertarDesdeFormulario(@RequestParam Map<String, String> params, RedirectAttributes redirectAttrs) {
        try {
            Persona persona = new Persona();
            persona.setNombres(params.get("nombres"));
            persona.setApellidos(params.get("apellidos"));
            persona.setCedula(params.get("cedula"));
            persona.setLugarExpedicion(params.get("lugarExpedicion"));
            persona.setFechaNacimiento(LocalDate.parse(params.get("fechaNacimiento")));
            persona.setDireccion(params.get("direccion"));
            persona.setSexo(params.get("sexo"));
            persona.setCorreoInstitucional(params.get("correoInstitucional"));
            persona.setTelefonoInstitucional(params.get("telefonoInstitucional"));
            persona.setEnlaceSigep(params.get("enlaceSigep"));
            personaService.guardarConNumero(persona);

            // Formación Académica
            Formacion formacion = new Formacion();
            formacion.setPersona(persona);
            formacion.setFormacionAcademica(params.get("formacionAcademica"));
            formacion.setGrado(params.get("grado"));
            formacion.setTitulo(params.get("titulo"));
            personaService.guardarFormacion(formacion);

            // Cargo laboral
            CargoLaboral cargo = personaService.obtenerOCrearCargo(
                params.get("cargo"), params.get("codigo"), params.get("dependencia")
            );

            PersonaCargoLaboral pcl = new PersonaCargoLaboral();
            pcl.setPersona(persona);
            pcl.setCargo(cargo);
            pcl.setFechaIngreso(LocalDate.parse(params.get("fechaIngreso")));
            pcl.setFechaFirmaContrato(LocalDate.parse(params.get("fechaFirmaContrato")));
            pcl.setMesesExperiencia(Integer.parseInt(params.get("mesesExperiencia")));
            personaService.guardarPersonaCargo(pcl);

            // Inducción y examen
            InduccionExamen ie = new InduccionExamen();
            ie.setPersonaCargoLaboral(pcl);
            ie.setInduccion(Boolean.parseBoolean(params.get("induccion")));
            ie.setExamenIngreso(Boolean.parseBoolean(params.get("examen")));
            ie.setFechaEgreso(LocalDate.parse(params.get("fechaEgreso")));
            personaService.guardarInduccion(ie);

            // Riesgo y procedencia
            RiesgoProcedencia rp = new RiesgoProcedencia();
            rp.setPersona(persona);
            rp.setRiesgo(params.get("riesgo"));
            rp.setMedioTransporte(params.get("medioTransporte"));
            rp.setProcedenciaTrabajador(params.get("procedencia"));
            personaService.guardarRiesgo(rp);

            // Salud
            Salud salud = new Salud();
            salud.setPersona(persona);
            salud.setDotacion(params.get("dotacion"));
            salud.setArl(params.get("arl"));
            salud.setEps(params.get("eps"));
            salud.setAfp(params.get("afp"));
            salud.setCcf(params.get("ccf"));
            salud.setRh(params.get("rh"));
            salud.setCarnetVacunacion(Boolean.parseBoolean(params.get("carnetVacunacion")));
            personaService.guardarSalud(salud);

            // Contacto de emergencia
            ContactoEmergencia contacto = new ContactoEmergencia();
            contacto.setPersona(persona);
            contacto.setNombreContactoEmergencia(params.get("nombreEmergencia"));
            contacto.setParentesco(params.get("parentesco"));
            contacto.setTelefonoContactoEmergencia(params.get("telefonoEmergencia"));
            personaService.guardarContactoEmergencia(contacto);

            // Enfermedades, Alergias y Medicamentos se gestionan en la siguiente parte...

            // Agrega mensaje de éxito
            redirectAttrs.addFlashAttribute("mensajeExito", "✅ Persona y datos relacionados guardados correctamente.");
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

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || esFilaVacia(row)) continue;


                Persona persona = new Persona();
                persona.setNombres(getCellValue(row, colIndex.get("nombres")));
                persona.setApellidos(getCellValue(row, colIndex.get("apellidos")));
                persona.setCedula(getCellValue(row, colIndex.get("cedula")));
                persona.setLugarExpedicion(getCellValue(row, colIndex.get("lugarExpedicion")));
                persona.setFechaNacimiento(parseFecha(getCellValue(row, colIndex.get("fechaNacimiento"))));
                persona.setDireccion(getCellValue(row, colIndex.get("direccion")));
                persona.setSexo(getCellValue(row, colIndex.get("sexo")));
                persona.setNumero(parseInteger(getCellValue(row, colIndex.get("numero"))));
                persona.setCorreoInstitucional(getCellValue(row, colIndex.get("correoInstitucional")));
                persona.setTelefonoInstitucional(getCellValue(row, colIndex.get("telefonoInstitucional")));
                persona.setEnlaceSigep(getCellValue(row, colIndex.get("enlaceSigep")));
                personaService.guardarConNumero(persona);

                // Formación
                Formacion formacion = new Formacion();
                formacion.setPersona(persona);
                formacion.setFormacionAcademica(getCellValue(row, colIndex.get("formacionAcademica")));
                formacion.setGrado(getCellValue(row, colIndex.get("grado")));
                formacion.setTitulo(getCellValue(row, colIndex.get("titulo")));
                personaService.guardarFormacion(formacion);

                // Cargo laboral
                CargoLaboral cargo = personaService.obtenerOCrearCargo(
                    getCellValue(row, colIndex.get("cargo")),
                    getCellValue(row, colIndex.get("codigo")),
                    getCellValue(row, colIndex.get("dependencia"))
                );

                PersonaCargoLaboral pcl = new PersonaCargoLaboral();
                pcl.setPersona(persona);
                pcl.setCargo(cargo);
                pcl.setFechaIngreso(parseFecha(getCellValue(row, colIndex.get("fechaIngreso"))));
                pcl.setFechaFirmaContrato(parseFecha(getCellValue(row, colIndex.get("fechaFirmaContrato"))));
                pcl.setMesesExperiencia(parseInteger(getCellValue(row, colIndex.get("mesesExperiencia"))));
                personaService.guardarPersonaCargo(pcl);

                // Inducción y examen
                InduccionExamen ie = new InduccionExamen();
                ie.setPersonaCargoLaboral(pcl);
                ie.setInduccion(Boolean.parseBoolean(getCellValue(row, colIndex.get("induccion"))));
                ie.setExamenIngreso(Boolean.parseBoolean(getCellValue(row, colIndex.get("examen"))));
                ie.setFechaEgreso(parseFecha(getCellValue(row, colIndex.get("fechaEgreso"))));
                personaService.guardarInduccion(ie);

                // Riesgo y procedencia
                RiesgoProcedencia rp = new RiesgoProcedencia();
                rp.setPersona(persona);
                rp.setRiesgo(getCellValue(row, colIndex.get("riesgo")));
                rp.setMedioTransporte(getCellValue(row, colIndex.get("medioTransporte")));
                rp.setProcedenciaTrabajador(getCellValue(row, colIndex.get("procedencia")));
                personaService.guardarRiesgo(rp);

                // Salud
                Salud salud = new Salud();
                salud.setPersona(persona);
                salud.setDotacion(getCellValue(row, colIndex.get("dotacion")));
                salud.setArl(getCellValue(row, colIndex.get("arl")));
                salud.setEps(getCellValue(row, colIndex.get("eps")));
                salud.setAfp(getCellValue(row, colIndex.get("afp")));
                salud.setCcf(getCellValue(row, colIndex.get("ccf")));
                salud.setRh(getCellValue(row, colIndex.get("rh")));
                salud.setCarnetVacunacion(Boolean.parseBoolean(getCellValue(row, colIndex.get("carnetVacunacion"))));
                personaService.guardarSalud(salud);

                // Contacto emergencia
                ContactoEmergencia contacto = new ContactoEmergencia();
                contacto.setPersona(persona);
                contacto.setNombreContactoEmergencia(getCellValue(row, colIndex.get("nombreEmergencia")));
                contacto.setParentesco(getCellValue(row, colIndex.get("parentesco")));
                contacto.setTelefonoContactoEmergencia(getCellValue(row, colIndex.get("telefonoEmergencia")));
                personaService.guardarContactoEmergencia(contacto);

                // === Enfermedades ===
                Map<String, Enfermedad> mapaEnfermedades = new HashMap<>();
                String enfermedadesRaw = getCellValue(row, colIndex.get("enfermedades"));
                if (!enfermedadesRaw.isEmpty()) {
                    Arrays.stream(enfermedadesRaw.split(",")).forEach(nombre -> {
                        nombre = nombre.trim();
                        if (!nombre.isEmpty()) {
                            Enfermedad enf = new Enfermedad();
                            enf.setPersona(persona);
                            enf.setNombre(nombre);
                            personaService.guardarEnfermedad(enf);
                            mapaEnfermedades.put(nombre.toLowerCase(), enf);
                        }
                    });
                }

                // === Alergias ===
                String alergiasRaw = getCellValue(row, colIndex.get("alergias"));
                if (!alergiasRaw.isEmpty()) {
                    Arrays.stream(alergiasRaw.split(",")).forEach(nombre -> {
                        nombre = nombre.trim();
                        if (!nombre.isEmpty()) {
                            Alergia alergia = new Alergia();
                            alergia.setPersona(persona);
                            alergia.setNombre(nombre);
                            personaService.guardarAlergia(alergia);
                        }
                    });
                }

                // === Medicamentos ===
                String medicamentosRaw = getCellValue(row, colIndex.get("medicamentos"));
                if (!medicamentosRaw.isEmpty()) {
                    String[] entradas = medicamentosRaw.split(",");
                    for (String entrada : entradas) {
                        entrada = entrada.trim();
                        if (entrada.contains(":")) {
                            String[] partes = entrada.split(":", 2);
                            String enfKey = partes[0].trim().toLowerCase();
                            Enfermedad enf = mapaEnfermedades.get(enfKey);
                            if (enf != null) {
                                String[] meds = partes[1].split("\\|");
                                for (String nomMed : meds) {
                                    nomMed = nomMed.trim();
                                    if (!nomMed.isEmpty()) {
                                        Medicamento med = personaService.obtenerOCrearMedicamento(nomMed, persona);
                                        personaService.asociarMedicamentoAEnfermedad(med, enf);
                                    }
                                }
                            }
                        } else {
                            if (!entrada.isEmpty()) {
                                Medicamento med = personaService.obtenerOCrearMedicamento(entrada, persona);
                                if (mapaEnfermedades.size() == 1) {
                                    Enfermedad unica = mapaEnfermedades.values().iterator().next();
                                    personaService.asociarMedicamentoAEnfermedad(med, unica);
                                } else {
                                    Enfermedad generica = mapaEnfermedades.get("no especificada");
                                    if (generica == null) {
                                        generica = new Enfermedad();
                                        generica.setPersona(persona);
                                        generica.setNombre("No especificada");
                                        personaService.guardarEnfermedad(generica);
                                        mapaEnfermedades.put("no especificada", generica);
                                    }
                                    personaService.asociarMedicamentoAEnfermedad(med, generica);
                                }
                            }
                        }
                    }
                }
            }

            return ResponseEntity.ok("✅ Archivo cargado e información insertada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Error al procesar el archivo Excel: " + e.getMessage());
        }
    }

        // === MÉTODOS AUXILIARES ===

    private Map<String, Integer> mapearColumnasConJaroWinkler(Row headerRow) {
        Map<String, Integer> mapeo = new HashMap<>();
        JaroWinklerSimilarity jw = new JaroWinklerSimilarity();

        for (Cell cell : headerRow) {
            String valor = getCellValue(cell);
            String valorOriginal = valor.trim().toLowerCase(); // sin normalizar
            String normalizado = normalizarNombreColumna(valorOriginal);
            double maxSimilitud = 0.80;
            String campoEncontrado = null;

            // Buscar alias exactos primero
            if (ALIAS_COLUMNAS.containsKey(valorOriginal)) {
                campoEncontrado = ALIAS_COLUMNAS.get(valorOriginal);
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
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private LocalDate parseFecha(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        for (DateTimeFormatter formato : FORMATOS_FECHA) {
            try {
                return LocalDate.parse(valor.trim(), formato);
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
