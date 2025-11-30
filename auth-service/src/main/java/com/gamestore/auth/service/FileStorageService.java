package com.gamestore.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {
    
    @Value("${file.upload-dir:uploads/profile-photos}")
    private String uploadDir;
    
    @Value("${server.port:3001}")
    private String serverPort;
    
    /**
     * Guarda un archivo de foto de perfil y retorna la URL pública
     * @param file Archivo a guardar
     * @param userId ID del usuario para organizar los archivos
     * @return URL pública del archivo guardado
     * @throws IOException Si hay error al guardar el archivo
     */
    public String storeProfilePhoto(MultipartFile file, Long userId) throws IOException {
        log.info("Intentando guardar foto de perfil para usuario {}: nombre={}, tamaño={}, contentType={}", 
            userId, file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            log.warn("Archivo vacío recibido para usuario {}", userId);
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        // Validar tipo de archivo (solo imágenes)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("Tipo de archivo inválido para usuario {}: contentType={}", userId, contentType);
            throw new IllegalArgumentException("El archivo debe ser una imagen. Tipo recibido: " + contentType);
        }
        
        // Validar tamaño (máximo 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            log.warn("Archivo demasiado grande para usuario {}: tamaño={} bytes, máximo={} bytes", 
                userId, file.getSize(), maxSize);
            throw new IllegalArgumentException("El archivo es demasiado grande. Máximo 5MB. Tamaño recibido: " + (file.getSize() / 1024 / 1024) + "MB");
        }
        
        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generar nombre único para el archivo
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = "user_" + userId + "_" + UUID.randomUUID().toString() + extension;
        
        // Guardar archivo
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Archivo guardado: {}", filePath);
        
        // Retornar URL pública
        return "http://localhost:" + serverPort + "/api/files/profile-photos/" + filename;
    }
    
    /**
     * Elimina un archivo de foto de perfil
     * @param filename Nombre del archivo a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean deleteProfilePhoto(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Archivo eliminado: {}", filePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Error al eliminar archivo: {}", filename, e);
            return false;
        }
    }
    
    /**
     * Extrae el nombre del archivo de una URL
     * @param url URL del archivo
     * @return Nombre del archivo o null si no se puede extraer
     */
    public String extractFilenameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        try {
            int lastSlash = url.lastIndexOf("/");
            if (lastSlash != -1 && lastSlash < url.length() - 1) {
                return url.substring(lastSlash + 1);
            }
        } catch (Exception e) {
            log.warn("No se pudo extraer el nombre del archivo de la URL: {}", url);
        }
        return null;
    }
}

