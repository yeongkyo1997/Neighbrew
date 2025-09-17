package com.ssafy.backend.domain.storage.service.s3.manager;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3ClientManager {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(String uploadPath, InputStream inputStream, ObjectMetadata metadata)
            throws IOException {
        amazonS3Client.putObject(new PutObjectRequest(bucket, uploadPath, inputStream, metadata));
        return amazonS3Client.getUrl(bucket, uploadPath).toString();
    }

    public boolean doesObjectExist(String objectKey) {
        return amazonS3Client.doesObjectExist(bucket, objectKey);
    }

    public void deleteObject(String objectKey) {
        amazonS3Client.deleteObject(bucket, objectKey);
    }

    public String parseObjectKeyFromUrl(String imageUrl) {
        String[] urlParts = imageUrl.split("/");
        return urlParts[3] + "/" + urlParts[4];
    }
}