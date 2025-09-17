package com.ssafy.backend.domain.storage.service.s3.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class S3FilenameGenerator {

    public String generateUUIDFileName(String originalFileName) {
        String ext = originalFileName.substring(originalFileName.indexOf(".") + 1);
        return UUID.randomUUID() + "." + ext;
    }
}