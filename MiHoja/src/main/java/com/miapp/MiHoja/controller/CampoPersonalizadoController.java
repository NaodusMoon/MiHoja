package com.miapp.MiHoja.controller;

import com.miapp.MiHoja.service.CampoPersonalizadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CampoPersonalizadoController {

    @Autowired
    private CampoPersonalizadoService campoService;

    @GetMapping("/configuracion-campos")
    public String configuracionCampos(Model model) {
        model.addAttribute("paginaActual", "configuracion");
        model.addAttribute("camposBaseTotal", campoService.camposBasePredeterminados().size());
        model.addAttribute("camposPersonalizadosTotal", campoService.listarTodos().size());
        model.addAttribute("camposConfigurables", campoService.listarCamposConfigurables(campoService.camposBasePredeterminados()));
        return "configuracion_campos";
    }

    @PostMapping("/configuracion-campos/agregar")
    public String agregarCampo(@RequestParam("nombre") String nombre,
                               @RequestParam(name = "etiqueta", required = false) String etiqueta,
                               @RequestParam(name = "tipoDato", required = false) String tipoDato,
                               RedirectAttributes redirectAttributes) {
        try {
            campoService.crearCampo(nombre, etiqueta, tipoDato);
            redirectAttributes.addFlashAttribute("mensajeExito", "Campo guardado correctamente.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", "No fue posible guardar el campo: " + ex.getMessage());
        }
        return "redirect:/configuracion-campos";
    }

    @PostMapping("/configuracion-campos/{id}/actualizar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarCampo(@PathVariable Long id,
                                                               @RequestParam("etiqueta") String etiqueta,
                                                               @RequestParam("tipoDato") String tipoDato) {
        try {
            var campo = campoService.actualizarCampo(id, etiqueta, tipoDato);
            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "id", campo.getId(),
                    "etiqueta", campo.getEtiquetaVisible(),
                    "tipoDato", campo.getTipoDatoNormalizado()
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/configuracion-campos/base/{nombre}/actualizar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarCampoBase(@PathVariable String nombre,
                                                                   @RequestParam("etiqueta") String etiqueta,
                                                                   @RequestParam("tipoDato") String tipoDato) {
        try {
            Map<String, String> campo = campoService.actualizarCampoBase(nombre, etiqueta, tipoDato);
            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "nombre", campo.get("nombre"),
                    "etiqueta", campo.get("etiqueta"),
                    "tipoDato", campo.get("tipoDato")
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/configuracion-campos/{id}/eliminar")
    public Object eliminarCampo(@PathVariable Long id,
                                @RequestHeader(name = "X-Requested-With", required = false) String requestedWith,
                                RedirectAttributes redirectAttributes) {
        try {
            campoService.desactivarCampo(id);
            if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
                return ResponseEntity.ok(Map.of(
                        "ok", true,
                        "id", id
                ));
            }
            redirectAttributes.addFlashAttribute("mensajeExito", "Campo desactivado correctamente.");
        } catch (Exception ex) {
            if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "ok", false,
                        "message", ex.getMessage()
                ));
            }
            redirectAttributes.addFlashAttribute("mensajeError", "No fue posible desactivar el campo: " + ex.getMessage());
        }
        return "redirect:/configuracion-campos";
    }

    @PostMapping("/configuracion-campos/base/{nombre}/eliminar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarCampoBase(@PathVariable String nombre) {
        try {
            campoService.ocultarCampoBase(nombre);
            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "nombre", nombre
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/configuracion-campos/reordenar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reordenarCampos(@RequestBody List<Map<String, String>> campos) {
        try {
            campoService.reordenarCampos(campos, campoService.camposBasePredeterminados());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", ex.getMessage()
            ));
        }
    }
}
