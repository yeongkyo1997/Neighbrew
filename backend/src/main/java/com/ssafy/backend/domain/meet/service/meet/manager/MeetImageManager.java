package com.ssafy.backend.domain.meet.service.meet.manager;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.common.enums.UploadType;
import com.ssafy.backend.domain.meet.dto.MeetRequestDto;
import com.ssafy.backend.domain.storage.service.s3.S3Service;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetImageManager {
    private static final String DEFAULT_IMAGE = "no image";

    private final S3Service s3Service;

    public void setHostAndImage(MeetRequestDto meetRequestDto, Long userId, MultipartFile multipartFile)
            throws IOException {
        meetRequestDto.setHostId(userId);
        meetRequestDto.setImgSrc(
                multipartFile != null ? s3Service.upload(UploadType.MEET, multipartFile) : DEFAULT_IMAGE);
    }

    public void updateImage(MeetRequestDto meetRequestDto, MultipartFile multipartFile, String prevMeetImgSrc)
            throws IOException {
        if (multipartFile != null) {
            if (!prevMeetImgSrc.equals(DEFAULT_IMAGE)) {
                s3Service.deleteImg(prevMeetImgSrc);
            }
            meetRequestDto.setImgSrc(s3Service.upload(UploadType.MEET, multipartFile));
        } else {
            // 클라이언트가 보낸 URL을 그대로 저장하면 이후 삭제 시 다른 사람의 S3 객체가 지워질 수 있으므로
            // 기본 이미지로 변경하는 경우만 허용하고, 그 외에는 기존 이미지를 유지한다.
            meetRequestDto.setImgSrc(
                    DEFAULT_IMAGE.equals(meetRequestDto.getImgSrc()) ? DEFAULT_IMAGE : prevMeetImgSrc);
        }
    }

    public void deleteImageIfNotDefault(String imagePath) {
        if (!DEFAULT_IMAGE.equals(imagePath)) {
            s3Service.deleteImg(imagePath);
        }
    }
}