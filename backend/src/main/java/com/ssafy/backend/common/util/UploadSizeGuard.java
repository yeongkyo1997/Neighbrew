package com.ssafy.backend.common.util;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public final class UploadSizeGuard {
    private static final long MAX_UPLOAD_SIZE = 20L * 1024 * 1024;

    private UploadSizeGuard() {
    }

    public static void check(Optional<MultipartFile> multipartFile) {
        if (multipartFile.isPresent() && multipartFile.get().getSize() > MAX_UPLOAD_SIZE) {
            throw new IllegalArgumentException("파일 업로드 크기는 20MB로 제한되어 있습니다.");
        }
    }
}
