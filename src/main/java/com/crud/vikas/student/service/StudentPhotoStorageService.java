package com.crud.vikas.student.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class StudentPhotoStorageService {

    private static final Path UPLOAD_DIR = Paths.get("uploads").toAbsolutePath().normalize();

    public String storePhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Student photo is required.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed for student photo.");
        }

        String originalName = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "photo"));
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String storedName = UUID.randomUUID() + extension;
        Path target = UPLOAD_DIR.resolve(storedName);

        try {
            Files.createDirectories(UPLOAD_DIR);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return storedName;
        } catch (IOException ex) {
            throw new UncheckedIOException("Unable to store student photo.", ex);
        }
    }

    public String publicPath(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "/assets/student-placeholder.svg";
        }
        return "/uploads/" + fileName;
    }
}
