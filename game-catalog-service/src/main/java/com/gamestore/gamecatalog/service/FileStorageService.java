package com.gamestore.gamecatalog.service;

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
    
    @Value("${file.upload-dir:uploads/game-images}")
    private String uploadDir;
    
    @Value("${server.port:3002}")
    private String serverPort;
    
    /**
     * Guarda una imagen de juego y retorna la URL pública
     * @param file Archivo a guardar
     * @param gameId ID del juego para organizar los archivos
     * @return URL pública del archivo guardado
     * @throws IOException Si hay error al guardar el archivo
     */
    public String storeGameImage(MultipartFile file, Long gameId) throws IOException {
        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        // Validar tipo de archivo (solo imágenes)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }
        
        // Validar tamaño (máximo 10MB para imágenes de juegos)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("El archivo es demasiado grande. Máximo 10MB");
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
        String filename = "game_" + gameId + "_" + UUID.randomUUID().toString() + extension;
        
        // Guardar archivo
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Imagen de juego guardada: {}", filePath);
        
        // Retornar URL pública
        return "http://localhost:" + serverPort + "/api/files/game-images/" + filename;
    }
    
    /**
     * Elimina una imagen de juego
     * @param filename Nombre del archivo a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean deleteGameImage(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Imagen de juego eliminada: {}", filePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Error al eliminar imagen de juego: {}", filename, e);
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

