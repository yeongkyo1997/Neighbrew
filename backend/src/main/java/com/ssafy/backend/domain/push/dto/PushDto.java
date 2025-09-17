package com.ssafy.backend.domain.push.dto;

import java.time.LocalDateTime;

import com.ssafy.backend.common.enums.PushType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PushDto {
    private Long pushId;
    private String userName;
    private PushType pushType;
    private String content;
    private String url;
    private boolean isRead;
    private Long receiver_id;
    private Long sender_id;
    private LocalDateTime createdAt;
}
