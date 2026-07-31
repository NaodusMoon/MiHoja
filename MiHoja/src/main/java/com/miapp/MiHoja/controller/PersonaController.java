package com.miapp.MiHoja.controller;

import com.miapp.MiHoja.dto.PersonaCompletaDTO;
import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.service.CampoPersonalizadoService;
import com.miapp.MiHoja.service.PersonaService;
import com.miapp.MiHoja.service.query.PersonaConsultaService;
import com.miapp.MiHoja.service.support.QueryStringService;
import com.miapp.MiHoja.service.view.PersonaDetalleView;
import com.miapp.MiHoja.service.view.PersonaViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class PersonaController {

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private CampoPersonalizadoService campoPersonalizadoService;

    @Autowired
    private PersonaConsultaService personaConsultaService;

    @Autowired
    private PersonaViewService personaViewService;

    @Autowired
    private QueryStringService queryStringService;

    @GetMapping("/consultar")
    public String consultar(@RequestParam(required = false) String nombre,
                            @RequestParam(required = false) String apellido,
                            @RequestParam(required = false) String cedula,
                            @RequestParam(required = false) List<String> lugarExpedicion,
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
        return "redirect:" + frontendUrl;
    }

    @GetMapping("/insertar")
    public String insertar(@RequestParam Map<String, String> params, Model model) {
        model.addAttribute("filtrosQuery", queryStringService.buildFromMap(params, "edit"));
        model.addAttribute("paginaActual", "insertar");
        model.addAttribute("camposBaseVisibles", campoPersonalizadoService.listarCamposBaseVisibles(campoPersonalizadoService.camposBasePredeterminados()));
        model.addAttribute("camposCustom", campoPersonalizadoService.listarActivos());
        return "insertar";
    }

    @GetMapping("/muestra/{id}")
    public String mostrarDatos(@PathVariable Long id,
                               @RequestParam Map<String, String> params,
                               @RequestParam(name = "edit", required = false, defaultValue = "false") boolean edit,
                               Model model) {
        model.addAttribute("paginaActual", "consultar");
        model.addAttribute("filtros", params != null ? params : Collections.emptyMap());

        Persona persona = personaService.obtenerPersonaConRelaciones(id);
        if (persona == null) {
            model.addAttribute("mensajeError", "No se encontro el registro con ID " + id);
            return "error";
        }

        PersonaDetalleView detalle = personaViewService.construirDetalle(persona);

        model.addAttribute("persona", persona);
        model.addAttribute("formacion", detalle.formacion());
        model.addAttribute("pcl", detalle.personaCargoLaboral());
        model.addAttribute("cargoLaboral", detalle.cargoLaboral());
        model.addAttribute("induccionExamen", detalle.induccionExamen());
        model.addAttribute("salud", detalle.salud());
        model.addAttribute("riesgoProcedencia", detalle.riesgoProcedencia());
        model.addAttribute("contactoEmergencia", detalle.contactoEmergencia());
        model.addAttribute("enfermedades", detalle.enfermedades());
        model.addAttribute("alergias", detalle.alergias());
        model.addAttribute("medicamentos", detalle.medicamentos());
        model.addAttribute("camposCustom", campoPersonalizadoService.listarActivos());
        model.addAttribute("valoresCustom", campoPersonalizadoService.mapaValoresPorNombre(persona.getId()));
        model.addAttribute("editMode", edit);

        if (edit) {
            PersonaCompletaDTO personaEditable = inicializarColeccionesDTO(personaService.buscarDTOporId(id));
            model.addAttribute("personaEditable", personaEditable);
            model.addAttribute("valoresCustomEdit", campoPersonalizadoService.mapaValoresPorPersona(id));
        }

        model.addAttribute("filtrosQuery", queryStringService.buildFromMap(params, "edit"));
        return "Muestra_Datos";
    }

    @GetMapping("/muestra_datos")
    public String mostrarDatos(@RequestParam Long id, Model model) {
        Persona persona = personaService.buscarPorId(id);
        model.addAttribute("persona", persona);
        return "redirect:/muestra/" + id;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPersona(@PathVariable Long id, @RequestParam Map<String, String> params) {
        personaService.eliminarPersonaYReordenar(id);
        String query = queryStringService.buildFromMap(params, "id");
        return query.isBlank() ? "redirect:/consultar" : "redirect:/consultar?" + query;
    }

    @PostMapping("/eliminar-multiples")
    public String eliminarMultiples(@RequestParam("selectedIds") List<Long> ids,
                                    RedirectAttributes redirectAttributes) {
        if (ids != null && !ids.isEmpty()) {
            PersonaService.DeletionSummary summary = personaService.eliminarVariosConResumen(ids);
            if (summary.getEliminados() > 0) {
                personaService.reordenarNumeros();
            }
            if (summary.getFallidos().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensajeExito", "Eliminados: " + summary.getEliminados());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "Eliminados: " + summary.getEliminados() + " | Fallidos IDs: " + summary.getFallidos());
            }
        }
        return "redirect:/consultar";
    }

    @PostMapping("/mantenimiento/limpiar-duplicados")
    public String limpiarDuplicadosExistentes(RedirectAttributes redirectAttributes) {
        PersonaService.CleanupSummary summary = personaService.limpiarDuplicadosExistentes();
        String mensaje = String.format(
                "Limpieza completada. Personas revisadas: %d, alergias eliminadas: %d, medicamentos eliminados: %d, enfermedades eliminadas: %d",
                summary.getPersonasRevisadas(),
                summary.getAlergiasEliminadas(),
                summary.getMedicamentosEliminados(),
                summary.getEnfermedadesEliminadas()
        );
        redirectAttributes.addFlashAttribute("mensajeExito", mensaje);
        return "redirect:/consultar";
    }

    @PostMapping("/insertar")
    public String insertarPersona(@ModelAttribute Persona persona,
                                  @RequestParam Map<String, String> params,
                                  Model model) {
        try {
            Persona nuevaPersona = personaService.guardarPersona(persona);
            campoPersonalizadoService.guardarValoresDesdeFormulario(nuevaPersona.getId(), params);
            String query = queryStringService.buildFromMap(params);
            return query.isBlank() ? "redirect:/consultar" : "redirect:/consultar?" + query;
        } catch (Exception exception) {
            System.err.println("Error al insertar persona: " + exception.getMessage());
            model.addAttribute("mensajeError", "Error al insertar la persona: " + exception.getMessage());
            return "error";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        PersonaCompletaDTO persona = inicializarColeccionesDTO(personaService.buscarDTOporId(id));
        model.addAttribute("persona", persona);
        model.addAttribute("personaEditable", persona);
        model.addAttribute("camposCustom", campoPersonalizadoService.listarActivos());
        model.addAttribute("valoresCustom", campoPersonalizadoService.mapaValoresPorPersona(id));
        return "editar_persona";
    }

    @PostMapping("/editar/{id}")
    public String guardarEdicion(@PathVariable Long id,
                                 @ModelAttribute PersonaCompletaDTO personaDTO,
                                 @RequestParam(name = "returnQuery", required = false) String returnQuery,
                                 @RequestParam Map<String, String> params,
                                 RedirectAttributes redirectAttributes) {
        personaDTO.setId(id);
        if (personaDTO.getId() == null) {
            throw new IllegalArgumentException("El ID no puede ser null al guardar la edicion");
        }

        personaService.guardarDTO(personaDTO);
        campoPersonalizadoService.guardarValoresDesdeFormulario(id, params);
        redirectAttributes.addFlashAttribute("mensajeExito", "Registro actualizado correctamente.");

        String query = queryStringService.sanitizeReturnQuery(returnQuery);
        if (query.isBlank()) {
            return "redirect:/muestra/" + id + "?edit=false";
        }
        return "redirect:/muestra/" + id + "?" + query + "&edit=false";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarPersona(@PathVariable Long id,
                                    @ModelAttribute Persona personaActualizada,
                                    @RequestParam Map<String, String> params,
                                    Model model) {
        try {
            personaService.actualizarPersona(id, personaActualizada);
            String query = queryStringService.buildFromMap(params);
            return query.isBlank() ? "redirect:/consultar" : "redirect:/consultar?" + query;
        } catch (Exception exception) {
            System.err.println("Error al actualizar persona: " + exception.getMessage());
            model.addAttribute("mensajeError", "Error al actualizar la persona: " + exception.getMessage());
            return "error";
        }
    }

    private PersonaCompletaDTO inicializarColeccionesDTO(PersonaCompletaDTO persona) {
        if (persona.getFormacion() == null || persona.getFormacion().isEmpty()) {
            persona.setFormacion(List.of(new PersonaCompletaDTO.Formacion()));
        }
        if (persona.getPersonaCargoLaboral() == null || persona.getPersonaCargoLaboral().isEmpty()) {
            persona.setPersonaCargoLaboral(List.of(new PersonaCompletaDTO.PersonaCargoLaboral()));
        }
        if (persona.getInduccionExamen() == null || persona.getInduccionExamen().isEmpty()) {
            persona.setInduccionExamen(List.of(new PersonaCompletaDTO.InduccionExamen()));
        }
        if (persona.getRiesgoProcedencia() == null || persona.getRiesgoProcedencia().isEmpty()) {
            persona.setRiesgoProcedencia(List.of(new PersonaCompletaDTO.RiesgoProcedencia()));
        }
        if (persona.getContactoEmergencia() == null || persona.getContactoEmergencia().isEmpty()) {
            persona.setContactoEmergencia(List.of(new PersonaCompletaDTO.ContactoEmergencia()));
        }
        if (persona.getEnfermedad() == null || persona.getEnfermedad().isEmpty()) {
            persona.setEnfermedad(List.of(new PersonaCompletaDTO.Enfermedad()));
        }
        if (persona.getMedicamento() == null || persona.getMedicamento().isEmpty()) {
            persona.setMedicamento(List.of(new PersonaCompletaDTO.Medicamento()));
        }
        if (persona.getAlergia() == null || persona.getAlergia().isEmpty()) {
            persona.setAlergia(List.of(new PersonaCompletaDTO.Alergia()));
        }
        if (persona.getSalud() == null) {
            persona.setSalud(new PersonaCompletaDTO.Salud());
        }
        return persona;
    }
}
