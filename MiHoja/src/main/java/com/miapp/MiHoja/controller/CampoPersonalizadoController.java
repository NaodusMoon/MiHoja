package com.miapp.MiHoja.controller;

import com.miapp.MiHoja.service.CampoPersonalizadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CampoPersonalizadoController {

    @Autowired
    private CampoPersonalizadoService campoService;

    @GetMapping("/configuracion-campos")
    public String configuracionCampos(Model model) {
        model.addAttribute("paginaActual", "configuracion");
        model.addAttribute("camposFormulario", List.of(
                "nombres", "apellidos", "cedula", "lugarExpedicion", "fechaNacimiento",
                "estado", "numero_hijos", "formacionAcademica", "codigo", "grado",
                "cargo", "dependencia", "enlaceSigep", "correoInstitucional", "telefonoInstitucional",
                "direccion", "sexo", "titulo", "mesesExperiencia", "medioTransporte",
                "procedencia", "riesgo", "examen", "induccion", "fechaIngreso",
                "fechaEgreso", "dotacion", "arl", "eps", "afp",
                "ccf", "rh", "carnetVacunacion", "enfermedades", "alergias",
                "medicamentos", "fechaFirmaContrato", "nombreEmergencia", "parentesco", "telefonoEmergencia", "imagen_url"
        ));
        return "configuracion_campos";
    }

    @PostMapping("/configuracion-campos/agregar")
    public String agregarCampo(@RequestParam("nombre") String nombre,
                               RedirectAttributes redirectAttributes) {
        try {
            campoService.crearCampo(nombre);
            redirectAttributes.addFlashAttribute("mensajeExito", "Campo guardado correctamente.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", "No fue posible guardar el campo: " + ex.getMessage());
        }
        return "redirect:/configuracion-campos";
    }

    @PostMapping("/configuracion-campos/{id}/eliminar")
    public String eliminarCampo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            campoService.desactivarCampo(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Campo desactivado correctamente.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", "No fue posible desactivar el campo: " + ex.getMessage());
        }
        return "redirect:/configuracion-campos";
    }
}
