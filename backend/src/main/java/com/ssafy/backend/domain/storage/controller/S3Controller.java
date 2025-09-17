package com.ssafy.backend.domain.storage.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.common.enums.UploadType;
import com.ssafy.backend.domain.storage.service.s3.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/img")
public class S3Controller {
    private final S3Service s3UploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart("image") MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok(s3UploadService.upload(UploadType.USERPROFILE, multipartFile));
    }
}