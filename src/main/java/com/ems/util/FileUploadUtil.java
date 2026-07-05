package com.ems.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

/**
 * Utility class for handling profile picture uploads.
 */
public class FileUploadUtil {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    /**
     * Saves an uploaded file to the given directory and returns the saved filename.
     *
     * @param uploadDir the directory where the file should be stored
     * @param file      the multipart file
     * @return the saved filename (UUID + original extension)
     */
    public static String saveFile(String uploadDir, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String savedFilename = UUID.randomUUID() + "." + extension;

        Path uploadPath = Path.of(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(savedFilename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return savedFilename;
    }

    /**
     * Deletes an existing file silently (no exception if not found).
     */
    public static void deleteFile(String uploadDir, String filename) {
        if (filename == null || filename.isBlank()) return;
        try {
            Path path = Path.of(uploadDir).resolve(filename);
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    private static void validateFile(MultipartFile file) throws IOException {
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IOException("File size exceeds the maximum allowed limit of 5MB.");
        }
        String contentType = file.getContentType();
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(contentType)) return;
        }
        throw new IOException("Only JPEG, PNG, GIF, and WebP images are allowed.");
    }

    private static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
