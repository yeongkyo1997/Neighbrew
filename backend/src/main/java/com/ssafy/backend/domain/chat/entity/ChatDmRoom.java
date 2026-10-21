package com.ssafy.backend.domain.chat.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;

import com.ssafy.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatDmRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatDmRoomId;

    @Lob
    @Column(nullable = false)
    private String chatDmRoomName;

    @OneToOne
    @JoinColumn(name = "user1_id")
    private User user1;

    @OneToOne
    @JoinColumn(name = "user2_id")
    private User user2;

    @Column(columnDefinition = "TIMESTAMP")
    @Setter
    private LocalDateTime user1AttendTime;

    @Column(columnDefinition = "TIMESTAMP")
    @Setter
    private LocalDateTime user2AttendTime;

    @Column(columnDefinition = "TIMESTAMP")
    @Setter
    private LocalDateTime lastMessageTime;
}
