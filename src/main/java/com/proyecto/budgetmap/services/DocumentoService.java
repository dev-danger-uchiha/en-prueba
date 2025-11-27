package com.proyecto.budgetmap.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import java.util.UUID;

@Service
public class DocumentoService {

    public String saveFile(MultipartFile file, String folder) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String uploadPath = "uploads/" + folder;

        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path path = Paths.get(uploadPath + "/" + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return path.toString();
    }
}
