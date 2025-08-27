package com.miapp.MiHoja.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    // 📂 Carpeta "uploads" en la raíz del proyecto (fuera de src)
    private static final Path CARPETA_UPLOADS = Paths.get(System.getProperty("user.dir"), "uploads");

    @PostMapping("/subir")
    public ResponseEntity<Map<String, Object>> subirImagen(@RequestParam("imagen") MultipartFile file) {
        Map<String, Object> resp = new HashMap<>();

        if (file.isEmpty()) {
            resp.put("error", "Archivo vacío");
            return ResponseEntity.badRequest().body(resp);
        }

        try {
            // Crear carpeta si no existe
            if (!Files.exists(CARPETA_UPLOADS)) {
                Files.createDirectories(CARPETA_UPLOADS);
                System.out.println("📂 Carpeta creada: " + CARPETA_UPLOADS.toAbsolutePath());
            }

            // Obtener extensión del archivo original
            String extension = "";
            String originalName = file.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            // Generar nombre único
            String nombreArchivo = UUID.randomUUID().toString() + extension;

            // Guardar archivo físicamente en la carpeta
            Path rutaArchivo = CARPETA_UPLOADS.resolve(nombreArchivo);
            Files.copy(file.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("✅ Imagen guardada en: " + rutaArchivo.toAbsolutePath());

            // URL pública que se usará en el frontend
            String urlPublica = "/uploads/" + nombreArchivo;

            resp.put("url", urlPublica);
            return ResponseEntity.ok(resp);

        } catch (IOException e) {
            e.printStackTrace();
            resp.put("error", "Error al guardar la imagen: " + e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }
}
