package com.ssafy.backend.domain.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;
import com.ssafy.backend.domain.user.dto.PublicUserResponseDto;
import com.ssafy.backend.domain.user.dto.UserUpdateDto;

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
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
// 계정은 provider와 provider 안의 고유 ID로 식별한다. (이메일은 동의에 따라 바뀌거나 없을 수 있다)
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_user_oauth", columnNames = { "o_auth_provider", "o_auth_id" }))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    // provider가 준 이메일 (계정 조회에는 쓰지 않는다. 이메일 제공에 동의하지 않으면 null)
    @Column(length = 100)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(name = "o_auth_provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    @Column(name = "o_auth_id", nullable = false, length = 100)
    private String oAuthId;

    @Column(nullable = false, length = 20)
    private String name;

    private LocalDate birth;

    @Lob
    private String intro;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false, columnDefinition = "float default 40.0")
    private Float liverPoint;

    @Column(nullable = false, columnDefinition = "varchar(255) default 'no image'")
    private String profile;

    @PrePersist
    public void prePersist() {
        this.liverPoint = 40.0f;
        this.profile = "no image";
        this.birth = LocalDate.parse("2005-01-01");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 엔티티가 응답에 그대로 실려도(Push, ChatDmRoom 등) 공개 프로필만 직렬화되도록 한다.
    @JsonValue
    public PublicUserResponseDto toPublicResponse() {
        return PublicUserResponseDto.fromEntity(this);
    }

    public void updateFromDto(UserUpdateDto updateDto) {
        if (updateDto.getNickname() != null) {
            this.nickname = updateDto.getNickname();
        }
        if (updateDto.getIntro() != null) {
            this.intro = updateDto.getIntro();
        }

        if (updateDto.getBirth() != null) {
            this.birth = updateDto.getBirth();
        }
        // profile은 /img API로만, liverPoint는 평가로만 변경한다.
    }

    public void updateImg(String url) {
        this.profile = url;
    }

    public void updateLiverPoint(Float newLiverPoint) {
        this.liverPoint = newLiverPoint;
    }
}
