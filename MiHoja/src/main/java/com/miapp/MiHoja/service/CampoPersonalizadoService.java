package com.miapp.MiHoja.service;

import com.miapp.MiHoja.model.CampoBaseMeta;
import com.miapp.MiHoja.model.CampoPersonalizado;
import com.miapp.MiHoja.model.CampoPersonalizadoMeta;
import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.model.PersonaCampoValor;
import com.miapp.MiHoja.repository.CampoBaseMetaRepository;
import com.miapp.MiHoja.repository.CampoPersonalizadoMetaRepository;
import com.miapp.MiHoja.repository.CampoPersonalizadoRepository;
import com.miapp.MiHoja.repository.PersonaCampoValorRepository;
import com.miapp.MiHoja.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CampoPersonalizadoService {

    @Autowired
    private CampoPersonalizadoRepository campoRepository;

    @Autowired
    private CampoBaseMetaRepository campoBaseMetaRepository;

    @Autowired
    private CampoPersonalizadoMetaRepository campoMetaRepository;

    @Autowired
    private PersonaCampoValorRepository valorRepository;

    @Autowired
    private PersonaRepository personaRepository;

    private String limpio(String value) {
        if (value == null) return "NO DISPONIBLE";
        String out = value.trim();
        return out.isEmpty() ? "NO DISPONIBLE" : out;
    }

    private String limpiarNombreCampo(String value) {
        if (value == null) {
            throw new IllegalArgumentException("El nombre del campo es obligatorio.");
        }
        String base = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        String normalizado = base.trim()
                .replaceAll("[^A-Za-z0-9_\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre del campo es obligatorio.");
        }
        String[] partes = normalizado.split(" ");
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < partes.length; i++) {
            String parte = partes[i];
            if (parte.isBlank()) continue;
            String lower = parte.toLowerCase(Locale.ROOT);
            if (resultado.isEmpty()) {
                resultado.append(lower);
            } else {
                resultado.append(Character.toUpperCase(lower.charAt(0)));
                if (lower.length() > 1) {
                    resultado.append(lower.substring(1));
                }
            }
        }
        if (resultado.isEmpty()) {
            throw new IllegalArgumentException("El nombre del campo es invalido.");
        }
        return resultado.toString();
    }

    private String limpiarEtiqueta(String etiqueta, String nombreCampo) {
        if (etiqueta == null || etiqueta.isBlank()) {
            return nombreCampo.replaceAll("([a-z])([A-Z])", "$1 $2")
                    .replace('_', ' ')
                    .trim();
        }
        return etiqueta.trim().replaceAll("\\s+", " ");
    }

    private String normalizarTipoDato(String tipoDato) {
        if (tipoDato == null || tipoDato.isBlank()) {
            return "texto";
        }
        String normalizado = tipoDato.trim().toLowerCase(Locale.ROOT);
        return switch (normalizado) {
            case "texto", "textarea", "numero", "fecha", "booleano" -> normalizado;
            default -> "texto";
        };
    }

    public List<Map<String, String>> camposBasePredeterminados() {
        return List.of(
                campoBase("nombres", "Nombres", "texto"),
                campoBase("apellidos", "Apellidos", "texto"),
                campoBase("cedula", "Cedula", "texto"),
                campoBase("lugarExpedicion", "Lugar de expedicion", "texto"),
                campoBase("fechaNacimiento", "Fecha de nacimiento", "fecha"),
                campoBase("estado", "Estado", "texto"),
                campoBase("numero_hijos", "Numero de hijos", "numero"),
                campoBase("formacionAcademica", "Formacion academica", "texto"),
                campoBase("codigo", "Codigo", "texto"),
                campoBase("grado", "Grado", "texto"),
                campoBase("cargo", "Cargo", "texto"),
                campoBase("dependencia", "Dependencia", "texto"),
                campoBase("enlaceSigep", "Enlace SIGEP", "texto"),
                campoBase("correoInstitucional", "Correo institucional", "texto"),
                campoBase("telefonoInstitucional", "Telefono institucional", "texto"),
                campoBase("direccion", "Direccion", "texto"),
                campoBase("sexo", "Sexo", "texto"),
                campoBase("titulo", "Titulo", "texto"),
                campoBase("mesesExperiencia", "Meses de experiencia", "numero"),
                campoBase("medioTransporte", "Medio de transporte", "texto"),
                campoBase("procedencia", "Procedencia", "texto"),
                campoBase("riesgo", "Riesgo", "texto"),
                campoBase("examen", "Examen", "booleano"),
                campoBase("induccion", "Induccion", "booleano"),
                campoBase("fechaIngreso", "Fecha de ingreso", "fecha"),
                campoBase("fechaEgreso", "Fecha de egreso", "fecha"),
                campoBase("dotacion", "Dotacion", "texto"),
                campoBase("arl", "ARL", "texto"),
                campoBase("eps", "EPS", "texto"),
                campoBase("afp", "AFP", "texto"),
                campoBase("ccf", "CCF", "texto"),
                campoBase("rh", "RH", "texto"),
                campoBase("carnetVacunacion", "Carnet de vacunacion", "booleano"),
                campoBase("enfermedades", "Enfermedades", "textarea"),
                campoBase("alergias", "Alergias", "textarea"),
                campoBase("medicamentos", "Medicamentos", "textarea"),
                campoBase("fechaFirmaContrato", "Fecha firma contrato", "fecha"),
                campoBase("nombreEmergencia", "Nombre emergencia", "texto"),
                campoBase("parentesco", "Parentesco", "texto"),
                campoBase("telefonoEmergencia", "Telefono emergencia", "texto"),
                campoBase("imagen_url", "Imagen URL", "texto")
        );
    }

    @Transactional(readOnly = true)
    public List<CampoPersonalizado> listarActivos() {
        return ordenarCampos(hidratarMetadatos(campoRepository.findByActivoTrueOrderByNombreAsc()));
    }

    @Transactional(readOnly = true)
    public List<CampoPersonalizado> listarTodos() {
        return ordenarCampos(hidratarMetadatos(campoRepository.findAllByOrderByNombreAsc()));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarCamposBaseConfigurados(List<Map<String, String>> camposBase) {
        Map<String, CampoBaseMeta> metas = new HashMap<>();
        for (CampoBaseMeta meta : campoBaseMetaRepository.findAll()) {
            metas.put(meta.getNombreCampo(), meta);
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map<String, String> campo : camposBase) {
            String nombre = campo.get("nombre");
            CampoBaseMeta meta = metas.get(nombre);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("nombre", nombre);
            item.put("etiqueta", meta != null && meta.getEtiqueta() != null && !meta.getEtiqueta().isBlank()
                    ? meta.getEtiqueta()
                    : campo.get("etiqueta"));
            item.put("tipoDato", meta != null && meta.getTipoDato() != null && !meta.getTipoDato().isBlank()
                    ? meta.getTipoDato()
                    : campo.get("tipoDato"));
            item.put("orden", meta != null && meta.getOrdenMostrar() != null ? meta.getOrdenMostrar() : resultado.size());
            item.put("oculto", meta != null && Boolean.TRUE.equals(meta.getOculto()));
            item.put("origen", "base");
            resultado.add(item);
        }
        resultado.sort(Comparator.comparingInt(item -> ((Number) item.getOrDefault("orden", 0)).intValue()));
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarCamposBaseVisibles(List<Map<String, String>> camposBase) {
        return listarCamposBaseConfigurados(camposBase).stream()
                .filter(item -> !Boolean.TRUE.equals(item.get("oculto")))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarCamposConfigurables(List<Map<String, String>> camposBase) {
        List<Map<String, Object>> items = new ArrayList<>(listarCamposBaseVisibles(camposBase));
        for (CampoPersonalizado campo : listarTodos()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", campo.getId());
            item.put("nombre", campo.getNombre());
            item.put("etiqueta", campo.getEtiquetaVisible());
            item.put("tipoDato", campo.getTipoDatoNormalizado());
            item.put("orden", extraerOrdenPersonalizado(campo.getId()));
            item.put("origen", Boolean.TRUE.equals(campo.getActivo()) ? "personalizado" : "inactivo");
            items.add(item);
        }
        items.sort(Comparator.comparingInt(item -> ((Number) item.getOrDefault("orden", Integer.MAX_VALUE)).intValue()));
        return items;
    }

    @Transactional
    public CampoPersonalizado crearCampo(String nombre) {
        return crearCampo(nombre, null, "texto");
    }

    @Transactional
    public CampoPersonalizado crearCampo(String nombre, String etiqueta, String tipoDato) {
        String finalNombre = limpiarNombreCampo(nombre);
        Optional<CampoPersonalizado> existente = campoRepository.findFirstByNombreIgnoreCase(finalNombre);
        CampoPersonalizado campo = existente.orElseGet(CampoPersonalizado::new);
        campo.setNombre(finalNombre);
        if (etiqueta != null || campo.getEtiqueta() == null || campo.getEtiqueta().isBlank()) {
            campo.setEtiqueta(limpiarEtiqueta(etiqueta, finalNombre));
        }
        if (!existente.isPresent() || tipoDato != null) {
            campo.setTipoDato(normalizarTipoDato(tipoDato));
        }
        campo.setActivo(true);
        CampoPersonalizado guardado = campoRepository.save(campo);
        guardarMeta(guardado.getId(), guardado.getEtiquetaVisible(), guardado.getTipoDatoNormalizado(), siguienteOrden());
        return guardado;
    }

    @Transactional
    public CampoPersonalizado actualizarCampo(Long campoId, String etiqueta, String tipoDato) {
        CampoPersonalizado campo = campoRepository.findById(campoId)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));
        campo.setEtiqueta(limpiarEtiqueta(etiqueta, campo.getNombre()));
        campo.setTipoDato(normalizarTipoDato(tipoDato));
        CampoPersonalizadoMeta metaActual = campoMetaRepository.findById(campo.getId()).orElse(null);
        guardarMeta(
                campo.getId(),
                campo.getEtiquetaVisible(),
                campo.getTipoDatoNormalizado(),
                metaActual != null ? metaActual.getOrdenMostrar() : siguienteOrden()
        );
        return campo;
    }

    @Transactional
    public Map<String, String> actualizarCampoBase(String nombreCampo, String etiqueta, String tipoDato) {
        CampoBaseMeta meta = campoBaseMetaRepository.findById(nombreCampo)
                .orElseGet(CampoBaseMeta::new);
        meta.setNombreCampo(nombreCampo);
        meta.setEtiqueta(limpiarEtiqueta(etiqueta, nombreCampo));
        meta.setTipoDato(normalizarTipoDato(tipoDato));
        if (meta.getOrdenMostrar() == null) {
            meta.setOrdenMostrar(siguienteOrdenBase(camposBasePredeterminados()));
        }
        if (meta.getOculto() == null) {
            meta.setOculto(false);
        }
        meta.setActualizadoEn(LocalDateTime.now());
        campoBaseMetaRepository.save(meta);

        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("nombre", nombreCampo);
        respuesta.put("etiqueta", meta.getEtiqueta());
        respuesta.put("tipoDato", meta.getTipoDato());
        return respuesta;
    }

    @Transactional
    public void desactivarCampo(Long campoId) {
        CampoPersonalizado campo = campoRepository.findById(campoId)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));
        campo.setActivo(false);
        campoRepository.save(campo);
    }

    @Transactional
    public void ocultarCampoBase(String nombreCampo) {
        CampoBaseMeta meta = campoBaseMetaRepository.findById(nombreCampo).orElseGet(CampoBaseMeta::new);
        meta.setNombreCampo(nombreCampo);
        if (meta.getTipoDato() == null || meta.getTipoDato().isBlank()) {
            meta.setTipoDato("texto");
        }
        if (meta.getOrdenMostrar() == null) {
            meta.setOrdenMostrar(siguienteOrdenBase(camposBasePredeterminados()));
        }
        meta.setOculto(true);
        meta.setActualizadoEn(LocalDateTime.now());
        campoBaseMetaRepository.save(meta);
    }

    @Transactional
    public void reordenarCampos(List<Map<String, String>> items, List<Map<String, String>> camposBase) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int orden = 0;
        for (Map<String, String> item : items) {
            String origen = item.get("origen");
            String key = item.get("key");
            if (origen == null || key == null) {
                continue;
            }

            if ("base".equalsIgnoreCase(origen)) {
                CampoBaseMeta meta = campoBaseMetaRepository.findById(key).orElseGet(CampoBaseMeta::new);
                meta.setNombreCampo(key);
                Map<String, String> base = camposBase.stream()
                        .filter(c -> key.equals(c.get("nombre")))
                        .findFirst()
                        .orElseGet(() -> campoBase(key, key, "texto"));
                if (meta.getEtiqueta() == null || meta.getEtiqueta().isBlank()) {
                    meta.setEtiqueta(base.get("etiqueta"));
                }
                if (meta.getTipoDato() == null || meta.getTipoDato().isBlank()) {
                    meta.setTipoDato(base.get("tipoDato"));
                }
                meta.setOculto(false);
                meta.setOrdenMostrar(orden++);
                meta.setActualizadoEn(LocalDateTime.now());
                campoBaseMetaRepository.save(meta);
                continue;
            }

            if ("personalizado".equalsIgnoreCase(origen)) {
                Long campoId = Long.parseLong(key);
                CampoPersonalizadoMeta meta = campoMetaRepository.findById(campoId).orElseGet(CampoPersonalizadoMeta::new);
                meta.setCampoId(campoId);
                if (meta.getEtiqueta() == null || meta.getEtiqueta().isBlank()) {
                    campoRepository.findById(campoId).ifPresent(campo -> meta.setEtiqueta(campo.getEtiquetaVisible()));
                }
                if (meta.getTipoDato() == null || meta.getTipoDato().isBlank()) {
                    campoRepository.findById(campoId).ifPresent(campo -> meta.setTipoDato(campo.getTipoDatoNormalizado()));
                }
                meta.setOrdenMostrar(orden++);
                meta.setActualizadoEn(LocalDateTime.now());
                campoMetaRepository.save(meta);
            }
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, String> mapaValoresPorPersona(Long personaId) {
        Map<Long, String> mapa = new HashMap<>();
        for (PersonaCampoValor valor : valorRepository.findByPersonaId(personaId)) {
            mapa.put(valor.getCampo().getId(), limpio(valor.getValor()));
        }
        return mapa;
    }

    @Transactional(readOnly = true)
    public Map<String, String> mapaValoresPorNombre(Long personaId) {
        Map<String, String> mapa = new LinkedHashMap<>();
        for (PersonaCampoValor valor : valorRepository.findByPersonaId(personaId)) {
            mapa.put(valor.getCampo().getNombre(), limpio(valor.getValor()));
        }
        return mapa;
    }

    @Transactional
    public void guardarValoresDesdeFormulario(Long personaId, Map<String, String> params) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        for (CampoPersonalizado campo : listarActivos()) {
            String key = "campo_custom_" + campo.getId();
            String valor = limpio(params.get(key));
            PersonaCampoValor row = valorRepository.findFirstByPersonaIdAndCampoId(personaId, campo.getId())
                    .orElseGet(PersonaCampoValor::new);
            row.setPersona(persona);
            row.setCampo(campo);
            row.setValor(valor);
            valorRepository.save(row);
        }
    }

    @Transactional
    public void guardarValorPorNombre(Persona persona, String nombreCampo, String valor) {
        if (persona == null || persona.getId() == null || nombreCampo == null) return;
        Optional<CampoPersonalizado> campoOpt = campoRepository.findFirstByNombreIgnoreCase(nombreCampo);
        if (campoOpt.isEmpty()) return;

        CampoPersonalizado campo = campoOpt.get();
        if (!Boolean.TRUE.equals(campo.getActivo())) return;

        PersonaCampoValor row = valorRepository.findFirstByPersonaIdAndCampoId(persona.getId(), campo.getId())
                .orElseGet(PersonaCampoValor::new);
        row.setPersona(persona);
        row.setCampo(campo);
        row.setValor(limpio(valor));
        valorRepository.save(row);
    }

    private List<CampoPersonalizado> hidratarMetadatos(List<CampoPersonalizado> campos) {
        if (campos == null || campos.isEmpty()) {
            return campos;
        }
        Map<Long, CampoPersonalizadoMeta> metas = new HashMap<>();
        for (CampoPersonalizadoMeta meta : campoMetaRepository.findAll()) {
            metas.put(meta.getCampoId(), meta);
        }
        for (CampoPersonalizado campo : campos) {
            CampoPersonalizadoMeta meta = metas.get(campo.getId());
            if (meta != null) {
                campo.setEtiqueta(meta.getEtiqueta());
                campo.setTipoDato(meta.getTipoDato());
            }
        }
        return campos;
    }

    private void guardarMeta(Long campoId, String etiqueta, String tipoDato, Integer ordenMostrar) {
        if (campoId == null) {
            return;
        }
        CampoPersonalizadoMeta meta = campoMetaRepository.findById(campoId)
                .orElseGet(CampoPersonalizadoMeta::new);
        meta.setCampoId(campoId);
        meta.setEtiqueta(etiqueta);
        meta.setTipoDato(normalizarTipoDato(tipoDato));
        if (meta.getOrdenMostrar() == null) {
            meta.setOrdenMostrar(ordenMostrar);
        }
        meta.setActualizadoEn(LocalDateTime.now());
        campoMetaRepository.save(meta);
    }

    private List<CampoPersonalizado> ordenarCampos(List<CampoPersonalizado> campos) {
        if (campos == null || campos.isEmpty()) {
            return campos;
        }
        campos.sort(Comparator
                .comparingInt((CampoPersonalizado campo) -> extraerOrdenPersonalizado(campo.getId()))
                .thenComparing(CampoPersonalizado::getNombre, String.CASE_INSENSITIVE_ORDER));
        return campos;
    }

    private int extraerOrdenPersonalizado(Long campoId) {
        return campoMetaRepository.findById(campoId)
                .map(CampoPersonalizadoMeta::getOrdenMostrar)
                .orElse(Integer.MAX_VALUE / 2);
    }

    private int siguienteOrden() {
        return campoMetaRepository.findAll().stream()
                .map(CampoPersonalizadoMeta::getOrdenMostrar)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(-1) + 1;
    }

    private int siguienteOrdenBase(List<Map<String, String>> camposBase) {
        return campoBaseMetaRepository.findAll().stream()
                .map(CampoBaseMeta::getOrdenMostrar)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(camposBase.size() - 1) + 1;
    }

    private Map<String, String> campoBase(String nombre, String etiqueta, String tipoDato) {
        Map<String, String> campo = new LinkedHashMap<>();
        campo.put("nombre", nombre);
        campo.put("etiqueta", etiqueta);
        campo.put("tipoDato", tipoDato);
        return campo;
    }
}
