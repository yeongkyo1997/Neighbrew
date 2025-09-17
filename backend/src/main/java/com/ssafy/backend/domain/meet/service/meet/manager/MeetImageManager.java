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
            if (meetRequestDto.getImgSrc() == null) {
                meetRequestDto.setImgSrc(prevMeetImgSrc);
            }
        }
    }

    public void deleteImageIfNotDefault(String imagePath) {
        if (!DEFAULT_IMAGE.equals(imagePath)) {
            s3Service.deleteImg(imagePath);
        }
    }
}