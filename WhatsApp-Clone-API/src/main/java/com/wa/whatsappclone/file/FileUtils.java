package com.wa.whatsappclone.file;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {

    private FileUtils() {}

    public static byte[] readFileFromLocation (String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return new byte[0];
        }
        try {
            Path path = Path.of(filePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.warn("Error reading file {}", filePath);
        }
        return new byte[0];
    }

}
