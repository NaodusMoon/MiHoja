package com.miapp.MiHoja.controller;

import com.miapp.MiHoja.service.storage.ImageStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    private static final Set<String> CONTENT_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/webp"
    );

    private final ImageStorageService imageStorageService;

    public ImagenController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/subir")
    public ResponseEntity<Map<String, Object>> subirImagen(@RequestParam("imagen") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "Archivo vacio");
            return ResponseEntity.badRequest().body(response);
        }

        if (file.getContentType() == null || !CONTENT_TYPES.contains(file.getContentType())) {
            response.put("error", "Formato de imagen no soportado");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String publicUrl = imageStorageService.save(file);
            response.put("url", publicUrl);
            return ResponseEntity.ok(response);
        } catch (IOException exception) {
            response.put("error", "Error al guardar la imagen: " + exception.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
