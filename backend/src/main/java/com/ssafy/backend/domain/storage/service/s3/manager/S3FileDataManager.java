package com.ssafy.backend.domain.storage.service.s3.manager;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.ssafy.backend.domain.storage.entity.S3File;
import com.ssafy.backend.domain.storage.repository.S3Repository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3FileDataManager {
    private final S3Repository s3Repository;

    public void saveFileRecord(String originalFileName, String uploadFileName, String uploadFilePath,
                               String uploadFileUrl) {
        S3File s3File = S3File.builder()
                              .originalFileName(originalFileName)
                              .uploadFileName(uploadFileName)
                              .uploadFilePath(uploadFilePath)
                              .uploadFileUrl(uploadFileUrl)
                              .build();

        s3Repository.save(s3File);
    }

    @Transactional
    public void deleteFileRecord(String uploadFileUrl) {
        s3Repository.deleteByUploadFileUrl(uploadFileUrl);
    }
}