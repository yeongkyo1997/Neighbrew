package com.ssafy.backend.domain.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.backend.domain.storage.entity.S3File;

public interface S3Repository extends JpaRepository<S3File, Long> {
    void deleteByUploadFileUrl(String imgSrc);
}
