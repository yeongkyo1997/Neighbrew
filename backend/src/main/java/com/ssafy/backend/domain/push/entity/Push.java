package com.ssafy.backend.domain.push.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.push.dto.PushDto;
import com.ssafy.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Push {
    //알림 : "누구 : ~에 대한 알림이 도착했습니다.", 클릭하면 해당 페이지로 이동하도록.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pushId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PushType pushType;

    @Lob
    private String content;

    @Lob
    private String url;

    @Column(name = "push_read_YN", nullable = false)
    private boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @CreatedDate
    private LocalDateTime createdAt;

    public PushDto toDto() {
        return PushDto.builder().pushId(this.pushId).sender_id(this.sender.getUserId()).receiver_id(
                this.receiver.getUserId()).pushType(this.pushType).content(this.content).url(this.url).isRead(
                this.isRead).build();
    }
}