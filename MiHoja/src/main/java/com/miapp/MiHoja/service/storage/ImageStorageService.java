package com.miapp.MiHoja.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    String save(MultipartFile file) throws IOException;
}
