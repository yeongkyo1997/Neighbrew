package com.ssafy.backend.domain.storage.service.s3;

import java.io.IOException;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ssafy.backend.common.enums.UploadType;
import com.ssafy.backend.domain.storage.service.s3.manager.S3ClientManager;
import com.ssafy.backend.domain.storage.service.s3.manager.S3FileDataManager;
import com.ssafy.backend.domain.storage.service.s3.manager.S3MetadataManager;
import com.ssafy.backend.domain.storage.service.s3.manager.S3PathManager;
import com.ssafy.backend.domain.storage.service.s3.util.S3FilenameGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3FilenameGenerator filenameGenerator;
    private final S3ClientManager clientManager;
    private final S3MetadataManager metadataManager;
    private final S3FileDataManager fileDataManager;
    private final S3PathManager pathManager;

    public String upload(UploadType uploadType, MultipartFile multipartFile) throws IOException {
        String originalFileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String uploadFileName = filenameGenerator.generateUUIDFileName(originalFileName);

        ObjectMetadata metadata = metadataManager.createMetadata(multipartFile);
        String uploadPath = pathManager.generateUploadPath(uploadType, uploadFileName);

        String uploadFileUrl = clientManager.uploadFile(uploadPath, multipartFile.getInputStream(), metadata);

        fileDataManager.saveFileRecord(originalFileName, uploadFileName, uploadType.name(), uploadFileUrl);

        return uploadFileUrl;
    }

    public void deleteImg(String imgSrc) {
        String objectKey = clientManager.parseObjectKeyFromUrl(imgSrc);
        if (objectKey == null) {
            return;
        }

        if (clientManager.doesObjectExist(objectKey)) {
            clientManager.deleteObject(objectKey);
            fileDataManager.deleteFileRecord(imgSrc);
        }
    }

    public String getUUIDFileName(String fileName) {
        return filenameGenerator.generateUUIDFileName(fileName);
    }
}