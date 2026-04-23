package com.miapp.MiHoja.service.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "local", matchIfMissing = true)
public class LocalImageStorageService implements ImageStorageService {

    private static final Path UPLOADS_DIR = Paths.get(System.getProperty("user.dir"), "uploads");

    @Override
    public String save(MultipartFile file) throws IOException {
        if (!Files.exists(UPLOADS_DIR)) {
            Files.createDirectories(UPLOADS_DIR);
        }
        String extension = extension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;
        Path destination = UPLOADS_DIR.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + filename;
    }

    private String extension(String originalName) {
        if (originalName == null) {
            return "";
        }
        int idx = originalName.lastIndexOf('.');
        return idx >= 0 ? originalName.substring(idx) : "";
    }
}
