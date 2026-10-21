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

    // 우리 버킷 URL로 시작하는 경우에만 객체 키를 반환하고, 그 외(외부 URL, 잘못된 값)는 null을 반환한다.
    public String parseObjectKeyFromUrl(String imageUrl) {
        String bucketUrl = amazonS3Client.getUrl(bucket, "").toString();
        if (imageUrl == null || !imageUrl.startsWith(bucketUrl) || imageUrl.length() == bucketUrl.length()) {
            return null;
        }
        return imageUrl.substring(bucketUrl.length());
    }
}