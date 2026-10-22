package com.ssafy.backend.domain.meet.service.meet.manager;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.common.enums.UploadType;
import com.ssafy.backend.domain.storage.service.s3.S3Service;

import lombok.RequiredArgsConstructor;

// S3 삭제는 되돌릴 수 없으므로 이 클래스는 업로드만 즉시 하고,
// 삭제는 MeetEventListener가 커밋(또는 롤백) 뒤에 deleteImageIfNotDefault로 처리한다.
@Component
@RequiredArgsConstructor
public class MeetImageManager {
    private static final String DEFAULT_IMAGE = "no image";

    private final S3Service s3Service;

    public String uploadImageIfExists(MultipartFile multipartFile) throws IOException {
        return multipartFile != null ? s3Service.upload(UploadType.MEET, multipartFile) : DEFAULT_IMAGE;
    }

    // 수정 후 모임 이미지 경로. 새 파일이 있으면 올린 경로, 없으면 기본 이미지나 기존 이미지.
    public String resolveUpdatedImage(String requestedImgSrc, MultipartFile multipartFile, String prevMeetImgSrc)
            throws IOException {
        if (multipartFile != null) {
            return s3Service.upload(UploadType.MEET, multipartFile);
        }
        // 클라이언트가 보낸 URL을 그대로 저장하면 이후 삭제 시 다른 사람의 S3 객체가 지워질 수 있으므로
        // 기본 이미지로 변경하는 경우만 허용하고, 그 외에는 기존 이미지를 유지한다.
        return DEFAULT_IMAGE.equals(requestedImgSrc) ? DEFAULT_IMAGE : prevMeetImgSrc;
    }

    public void deleteImageIfNotDefault(String imagePath) {
        if (imagePath != null && !DEFAULT_IMAGE.equals(imagePath)) {
            s3Service.deleteImg(imagePath);
        }
    }
}
