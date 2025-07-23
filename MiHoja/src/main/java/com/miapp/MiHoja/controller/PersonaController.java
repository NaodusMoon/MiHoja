package com.miapp.MiHoja.controller;

import com.miapp.MiHoja.dto.PersonaConCargo;
import com.miapp.MiHoja.model.*;
import com.miapp.MiHoja.repository.PersonaRepository;
import com.miapp.MiHoja.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class PersonaController {

    @Autowired
    private PersonaRepository repo;

    @Autowired
    private PersonaService personaService;

        @GetMapping("/consultar")
public String consultar(@RequestParam(required = false) String nombre,
                        @RequestParam(required = false) String apellido,
                        @RequestParam(required = false) String cedula,
                        @RequestParam(required = false) String lugarExpedicion,
                        @RequestParam(required = false) String direccion,
                        @RequestParam(required = false) String sexo,
                        @RequestParam(required = false) String correo,
                        @RequestParam(required = false) String telefono,
                        @RequestParam(required = false) String enlaceSigep,
                        @RequestParam(required = false) List<String> formacion,
                        @RequestParam(required = false) List<String> grado,
                        @RequestParam(required = false) List<String> cargo,
                        @RequestParam(required = false) List<String> dependencia,
                        @RequestParam(required = false) List<String> rh,
                        @RequestParam(required = false) List<String> eps,
                        @RequestParam(required = false) List<String> afp,
                        @RequestParam(required = false) List<String> carnetVacunacion,
                        @RequestParam(required = false) List<String> riesgo,
                        @RequestParam(required = false) List<String> medioTransporte,
                        @RequestParam(required = false) List<String> procedencia,
                        @RequestParam(required = false) List<String> induccion,
                        @RequestParam(required = false) List<String> examen,
                        @RequestParam(required = false) List<String> mesesExperiencia,
                        @RequestParam(required = false) List<String> dotacion,
                        Model model) {

    model.addAttribute("paginaActual", "consultar");

    System.out.println("✅ Filtros recibidos:");
    System.out.println("Nombre: " + nombre + " | Apellido: " + apellido + " | Cedula: " + cedula);
    System.out.println("Sexo: " + sexo + " | Cargo: " + cargo + " | Formación: " + formacion);

    List<PersonaConCargo> personas = repo.consultarPersonasConCargo();
    System.out.println("🔍 Personas recuperadas: " + personas.size());
    personas.forEach(p -> System.out.println(
            "➡ ID:" + p.getId() +
                    " | Nombre:" + p.getNombres() +
                    " | Apellido:" + p.getApellidos() +
                    " | Cargo:" + p.getCargo() +
                    " | Formación:" + p.getFormacion()
    ));

    if (nombre != null && !nombre.trim().isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getNombres() != null &&
                        normalizar(p.getNombres()).contains(normalizar(nombre)))
                .collect(Collectors.toList());
    }
    if (apellido != null && !apellido.trim().isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getApellidos() != null &&
                        normalizar(p.getApellidos()).contains(normalizar(apellido)))
                .collect(Collectors.toList());
    }
    if (cedula != null && !cedula.trim().isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getCedula() != null &&
                        p.getCedula().equalsIgnoreCase(cedula))
                .collect(Collectors.toList());
    }
    if (lugarExpedicion != null && !lugarExpedicion.trim().isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getLugarExpedicion() != null &&
                        normalizar(p.getLugarExpedicion()).contains(normalizar(lugarExpedicion)))
                .collect(Collectors.toList());
    }
    if (direccion != null && !direccion.trim().isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getDireccion() != null &&
                        normalizar(p.getDireccion()).contains(normalizar(direccion)))
                .collect(Collectors.toList());
    }
    if (sexo != null && !sexo.trim().isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getSexo() != null &&
                        p.getSexo().equalsIgnoreCase(sexo))
                .collect(Collectors.toList());
    }
    if (correo != null && !correo.trim().isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getCorreoInstitucional() != null &&
                        p.getCorreoInstitucional().equalsIgnoreCase(correo))
                .collect(Collectors.toList());
    }
    if (telefono != null && !telefono.trim().isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getTelefonoInstitucional() != null &&
                        p.getTelefonoInstitucional().equalsIgnoreCase(telefono))
                .collect(Collectors.toList());
    }
    if (enlaceSigep != null && !enlaceSigep.trim().isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getEnlaceSigep() != null &&
                        normalizar(p.getEnlaceSigep()).contains(normalizar(enlaceSigep)))
                .collect(Collectors.toList());
    }

    if (formacion != null && !formacion.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getFormacion() != null &&
                        formacion.stream().anyMatch(f -> normalizar(p.getFormacion()).contains(normalizar(f))))
                .collect(Collectors.toList());
    }
    if (grado != null && !grado.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getGrado() != null &&
                        grado.stream().anyMatch(g -> normalizar(p.getGrado()).contains(normalizar(g))))
                .collect(Collectors.toList());
    }
    if (cargo != null && !cargo.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getCargo() != null &&
                        cargo.stream().anyMatch(c -> normalizar(p.getCargo()).contains(normalizar(c))))
                .collect(Collectors.toList());
    }
    if (dependencia != null && !dependencia.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getDependencia() != null &&
                        dependencia.stream().anyMatch(d -> normalizar(p.getDependencia()).contains(normalizar(d))))
                .collect(Collectors.toList());
    }
    if (rh != null && !rh.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getRh() != null &&
                        rh.stream().anyMatch(r -> normalizar(p.getRh()).contains(normalizar(r))))
                .collect(Collectors.toList());
    }
    if (eps != null && !eps.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getEps() != null &&
                        eps.stream().anyMatch(e -> normalizar(p.getEps()).contains(normalizar(e))))
                .collect(Collectors.toList());
    }
    if (afp != null && !afp.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getAfp() != null &&
                        afp.stream().anyMatch(a -> normalizar(p.getAfp()).contains(normalizar(a))))
                .collect(Collectors.toList());
    }
    if (carnetVacunacion != null && !carnetVacunacion.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getCarnetVacunacion() != null &&
                        carnetVacunacion.stream().anyMatch(cv -> normalizar(p.getCarnetVacunacion()).contains(normalizar(cv))))
                .collect(Collectors.toList());
    }
    if (riesgo != null && !riesgo.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getRiesgo() != null &&
                        riesgo.stream().anyMatch(r -> normalizar(p.getRiesgo()).contains(normalizar(r))))
                .collect(Collectors.toList());
    }
    if (medioTransporte != null && !medioTransporte.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getMedioTransporte() != null &&
                        medioTransporte.stream().anyMatch(mt -> normalizar(p.getMedioTransporte()).contains(normalizar(mt))))
                .collect(Collectors.toList());
    }
    if (procedencia != null && !procedencia.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getProcedencia() != null &&
                        procedencia.stream().anyMatch(pr -> normalizar(p.getProcedencia()).contains(normalizar(pr))))
                .collect(Collectors.toList());
    }
    if (induccion != null && !induccion.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getInduccion() != null &&
                        induccion.stream().anyMatch(i -> normalizar(p.getInduccion()).contains(normalizar(i))))
                .collect(Collectors.toList());
    }
    if (examen != null && !examen.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getExamen() != null &&
                        examen.stream().anyMatch(ex -> normalizar(p.getExamen()).contains(normalizar(ex))))
                .collect(Collectors.toList());
    }
    if (mesesExperiencia != null && !mesesExperiencia.isEmpty()) {
        personas = personas.stream()
                .filter(p -> {
                    if (p.getMesesExperiencia() == null) return false;
                    int meses;
                    try {
                        meses = Integer.parseInt(p.getMesesExperiencia().trim());
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    for (String rango : mesesExperiencia) {
                        switch (rango) {
                            case "0-12":
                                if (meses >= 0 && meses <= 12) return true;
                                break;
                            case "13-60":
                                if (meses >= 13 && meses <= 60) return true;
                                break;
                            case "61-120":
                                if (meses >= 61 && meses <= 120) return true;
                                break;
                            case "121+":
                                if (meses >= 121) return true;
                                break;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
    if (dotacion != null && !dotacion.isEmpty()) {
        personas = personas.stream()
                .filter(p -> p.getDotacion() != null &&
                        dotacion.stream().anyMatch(d -> normalizar(p.getDotacion()).contains(normalizar(d))))
                .collect(Collectors.toList());
    }

    System.out.println("✅ Personas después de aplicar filtros: " + personas.size());

    personas = personas.stream()
            .sorted(Comparator.comparingInt(p -> Optional.ofNullable(p.getNumero()).orElse(999999)))
            .collect(Collectors.toList());

    model.addAttribute("personas", personas);
    model.addAttribute("nombre", nombre);
    model.addAttribute("apellido", apellido);
    model.addAttribute("cedula", cedula);
    model.addAttribute("lugarExpedicion", lugarExpedicion);
    model.addAttribute("direccion", direccion);
    model.addAttribute("sexo", sexo);
    model.addAttribute("correo", correo);
    model.addAttribute("telefono", telefono);
    model.addAttribute("enlaceSigep", enlaceSigep);
    model.addAttribute("formacion", formacion);
    model.addAttribute("grado", grado);
    model.addAttribute("cargo", cargo);
    model.addAttribute("dependencia", dependencia);
    model.addAttribute("rh", rh);
    model.addAttribute("eps", eps);
    model.addAttribute("afp", afp);
    model.addAttribute("carnetVacunacion", carnetVacunacion);
    model.addAttribute("riesgo", riesgo);
    model.addAttribute("medioTransporte", medioTransporte);
    model.addAttribute("procedencia", procedencia);
    model.addAttribute("induccion", induccion);
    model.addAttribute("examen", examen);
    model.addAttribute("mesesExperiencia", mesesExperiencia);
    model.addAttribute("dotacion", dotacion);

    // ✅ Construir filtrosQuery para mantener filtros en enlaces
    Map<String, Object> filtros = new LinkedHashMap<>();
    if (nombre != null) filtros.put("nombre", nombre);
    if (apellido != null) filtros.put("apellido", apellido);
    if (cedula != null) filtros.put("cedula", cedula);
    if (lugarExpedicion != null) filtros.put("lugarExpedicion", lugarExpedicion);
    if (direccion != null) filtros.put("direccion", direccion);
    if (sexo != null) filtros.put("sexo", sexo);
    if (correo != null) filtros.put("correo", correo);
    if (telefono != null) filtros.put("telefono", telefono);
    if (enlaceSigep != null) filtros.put("enlaceSigep", enlaceSigep);

    if (formacion != null) filtros.put("formacion", String.join(",", formacion));
    if (grado != null) filtros.put("grado", String.join(",", grado));
    if (cargo != null) filtros.put("cargo", String.join(",", cargo));
    if (dependencia != null) filtros.put("dependencia", String.join(",", dependencia));
    if (rh != null) filtros.put("rh", String.join(",", rh));
    if (eps != null) filtros.put("eps", String.join(",", eps));
    if (afp != null) filtros.put("afp", String.join(",", afp));
    if (carnetVacunacion != null) filtros.put("carnetVacunacion", String.join(",", carnetVacunacion));
    if (riesgo != null) filtros.put("riesgo", String.join(",", riesgo));
    if (medioTransporte != null) filtros.put("medioTransporte", String.join(",", medioTransporte));
    if (procedencia != null) filtros.put("procedencia", String.join(",", procedencia));
    if (induccion != null) filtros.put("induccion", String.join(",", induccion));
    if (examen != null) filtros.put("examen", String.join(",", examen));
    if (mesesExperiencia != null) filtros.put("mesesExperiencia", String.join(",", mesesExperiencia));
    if (dotacion != null) filtros.put("dotacion", String.join(",", dotacion));

    String filtrosQuery = filtros.entrySet().stream()
            .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue().toString(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

    model.addAttribute("filtros", filtros);
    model.addAttribute("filtrosQuery", filtrosQuery);

    return "consultar";
}



      @GetMapping("/insertar")
public String insertar(@RequestParam Map<String, String> params, Model model) {
    // Mantener filtros
    String filtrosQuery = params.entrySet().stream()
            .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

    model.addAttribute("filtrosQuery", filtrosQuery);
    model.addAttribute("paginaActual", "insertar");
    return "insertar";
}









    @GetMapping("/muestra/{id}")
    public String mostrarDatos(@PathVariable Long id,
                           @RequestParam Map<String, String> params,
                           Model model) {

                                model.addAttribute("paginaActual", "consultar");
    model.addAttribute("filtros", params != null ? params : Collections.emptyMap());
    Persona persona = personaService.obtenerPersonaConRelaciones(id);

    if (persona == null) {
        model.addAttribute("mensajeError", "No se encontró el registro con ID " + id);
        return "error";
    }

    model.addAttribute("filtros", params != null ? params : Collections.emptyMap());
        Formacion formacion = persona.getFormaciones().stream().findFirst().orElse(null);
        PersonaCargoLaboral pcl = persona.getCargosLaborales().stream().findFirst().orElse(null);
        CargoLaboral cargoLaboral = (pcl != null) ? pcl.getCargo() : null;
        InduccionExamen induccionExamen = (pcl != null && pcl.getInduccionesExamen() != null) ?
                pcl.getInduccionesExamen().stream().findFirst().orElse(null) : null;

        Salud salud = persona.getRegistrosSalud().stream().findFirst().orElse(null);
        RiesgoProcedencia riesgo = persona.getRiesgoProcedencias().stream().findFirst().orElse(null);
        ContactoEmergencia contacto = persona.getContactosEmergencia().stream().findFirst().orElse(null);

        List<String> enfermedades = persona.getEnfermedades().stream()
                .map(Enfermedad::getNombre)
                .collect(Collectors.toList());

        List<String> alergias = persona.getAlergias().stream()
                .map(Alergia::getNombre)
                .collect(Collectors.toList());

        List<String> medicamentos = persona.getEnfermedades().stream()
                .flatMap(e -> e.getMedicamentos().stream())
                .map(Medicamento::getNombre)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("persona", persona);
        model.addAttribute("formacion", formacion);
        model.addAttribute("pcl", pcl);
        model.addAttribute("cargoLaboral", cargoLaboral);
        model.addAttribute("induccionExamen", induccionExamen);
        model.addAttribute("salud", salud);
        model.addAttribute("riesgoProcedencia", riesgo);
        model.addAttribute("contactoEmergencia", contacto);
        model.addAttribute("enfermedades", enfermedades);
        model.addAttribute("alergias", alergias);
        model.addAttribute("medicamentos", medicamentos);

        model.addAttribute("filtros", params != null ? params : Collections.emptyMap());

         // ✅ Para el botón volver con filtros
         String filtrosQuery = params.entrySet().stream()
            .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

    model.addAttribute("filtrosQuery", filtrosQuery);


        return "Muestra_Datos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPersona(@PathVariable Long id, @RequestParam Map<String, String> params) {
        personaService.eliminarPersonaYReordenar(id);

        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getKey().equals("id")) {
                query.append("&")
                        .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }

        String redirectUrl = "redirect:/consultar";
        if (!query.toString().isEmpty()) {
            redirectUrl += "?" + query.substring(1);
        }

        return redirectUrl;
    }

    @PostMapping("/eliminar-multiples")
    public String eliminarMultiples(@RequestParam("selectedIds") List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            personaService.eliminarVarios(ids);
        }
        return "redirect:/consultar";
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalizado = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalizado).replaceAll("");
        normalizado = normalizado.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", "");
        normalizado = normalizado.replaceAll("\\s+", " ");
        normalizado = normalizado.replaceAll("[\\r\\n\\t]", "");
        return normalizado.trim().toLowerCase();
    }

    @PostMapping("/insertar")
    public String insertarPersona(@ModelAttribute Persona persona,
                                  @RequestParam Map<String, String> params,
                                  Model model) {
        try {
            System.out.println("✅ Insertando persona: " + persona.getNombres() + " " + persona.getApellidos());

            Persona nuevaPersona = personaService.guardarPersona(persona);

            System.out.println("✅ Persona insertada con ID: " + nuevaPersona.getId());

            StringBuilder query = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                query.append("&")
                        .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            String redirectUrl = "redirect:/consultar";
            if (!query.toString().isEmpty()) {
                redirectUrl += "?" + query.substring(1);
            }

            return redirectUrl;

        } catch (Exception e) {
            System.err.println("❌ Error al insertar persona: " + e.getMessage());
            model.addAttribute("mensajeError", "Error al insertar la persona: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarPersona(@PathVariable Long id,
                                    @ModelAttribute Persona personaActualizada,
                                    @RequestParam Map<String, String> params,
                                    Model model) {
        try {
            System.out.println("✏️ Actualizando persona con ID: " + id);

            personaService.actualizarPersona(id, personaActualizada);

            StringBuilder query = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                query.append("&")
                        .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            String redirectUrl = "redirect:/consultar";
            if (!query.toString().isEmpty()) {
                redirectUrl += "?" + query.substring(1);
            }

            return redirectUrl;

        } catch (Exception e) {
            System.err.println("❌ Error al actualizar persona: " + e.getMessage());
            model.addAttribute("mensajeError", "Error al actualizar la persona: " + e.getMessage());
            return "error";
        }
    }
}
