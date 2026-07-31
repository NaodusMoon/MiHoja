package com.miapp.MiHoja.service.storage;

import com.miapp.MiHoja.config.StorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "supabase")
public class SupabaseImageStorageService implements ImageStorageService {

    private final StorageProperties properties;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public SupabaseImageStorageService(StorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public String save(MultipartFile file) throws IOException {
        String supabaseUrl = required(properties.getSupabase().getUrl(), "SUPABASE_URL");
        String serviceKey = required(properties.getSupabase().getServiceKey(), "SUPABASE_SERVICE_KEY");
        String bucket = required(properties.getSupabase().getBucket(), "SUPABASE_STORAGE_BUCKET");

        String extension = extension(file.getOriginalFilename());
        String objectKey = "uploads/" + UUID.randomUUID() + extension;
        String encodedKey = URLEncoder.encode(objectKey, StandardCharsets.UTF_8).replace("+", "%20");
        String uploadUrl = trimTrailingSlash(supabaseUrl) + "/storage/v1/object/" + bucket + "/" + encodedKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("apikey", serviceKey)
                .header("Authorization", "Bearer " + serviceKey)
                .header("x-upsert", "true")
                .header("Content-Type", contentType(file))
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Error al subir imagen a Supabase Storage: " + response.body());
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IOException("Carga a Supabase interrumpida", interrupted);
        }

        if (properties.getSupabase().isPublicBucket()) {
            return trimTrailingSlash(supabaseUrl) + "/storage/v1/object/public/" + bucket + "/" + objectKey;
        }
        return objectKey;
    }

    private String required(String value, String keyName) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falta configuracion de almacenamiento: " + keyName);
        }
        return value;
    }

    private String extension(String originalName) {
        if (originalName == null) {
            return "";
        }
        int idx = originalName.lastIndexOf('.');
        return idx >= 0 ? originalName.substring(idx) : "";
    }

    private String contentType(MultipartFile file) {
        return file.getContentType() != null ? file.getContentType() : "application/octet-stream";
    }

    private String trimTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
