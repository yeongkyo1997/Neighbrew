package com.ssafy.backend.domain.storage.service.s3.manager;

import org.springframework.stereotype.Component;

import com.ssafy.backend.common.enums.UploadType;

@Component
public class S3PathManager {

    public String generateUploadPath(UploadType uploadType, String fileName) {
        return uploadType.name() + "/" + fileName;
    }
}