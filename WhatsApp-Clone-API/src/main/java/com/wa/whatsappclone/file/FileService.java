package com.wa.whatsappclone.file;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    @Value("${application.file.uploads.media-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @NotNull MultipartFile sourceFile,
            @NotNull String currentUserId
    ) {
        final String fileUploadSubPath = "users" + separator + currentUserId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(
            @NotNull MultipartFile sourceFile,
            @NotNull String fileUploadSubPath
    ) {
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean isFolderCreated = targetFolder.mkdirs();
            if (!isFolderCreated) {
                log.warn("Could not create the target folder {}", targetFolder);
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalUploadPath + separator + System.currentTimeMillis() + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to {}", targetPath);
            return targetFilePath;
        } catch (IOException e){
            log.error("File was not saved {}", e.getMessage());
        }
        return null;
    }

    private String getFileExtension(@Nullable String originalFilename) {
        if (originalFilename == null | originalFilename.isEmpty()) {
            return "";
        }
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return originalFilename.substring(lastDotIndex).toLowerCase();
    }
}
