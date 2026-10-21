package com.ssafy.backend.domain.meet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssafy.backend.common.entity.Tag;
import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.drink.dto.DrinkResponseDto;
import com.ssafy.backend.domain.drink.entity.Drink;
import com.ssafy.backend.domain.meet.dto.MeetRequestDto;
import com.ssafy.backend.domain.meet.dto.MeetResponseDto;
import com.ssafy.backend.domain.region.dto.GugunResponseDto;
import com.ssafy.backend.domain.region.dto.SidoResponseDto;
import com.ssafy.backend.domain.region.entity.Gugun;
import com.ssafy.backend.domain.region.entity.Sido;
import com.ssafy.backend.domain.user.dto.PublicUserResponseDto;
import com.ssafy.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Meet {
    @Id
    @Setter
    @GeneratedValue
    private Long meetId;

    @Column(nullable = false, length = 100)
    private String meetName;

    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "hostId", referencedColumnName = "userId")
    @Setter
    private User host;

    //현재 참여 인원
    @Column(nullable = false, columnDefinition = "int default 1")
    @Setter
    private Integer nowParticipants;

    //최대 참여 인원
    @Column(nullable = false, columnDefinition = "int default 8")
    private Integer maxParticipants;

    //모임날짜
    private LocalDateTime meetDate;

    @ManyToOne
    @JoinColumn(name = "tagId")
    @Setter
    private Tag tag;

    @Column(nullable = false)
    private Integer sidoCode;
    @Column(nullable = false)
    private Integer gugunCode;

    private Integer minAge;
    private Integer maxAge;
    private Float minLiverPoint;

    //술ID
    @OneToOne
    @JoinColumn(name = "drinkId")
    @Setter
    private Drink drink;

    //미팅 이미지 url
    @Lob
    private String imgSrc;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    // 채팅과 모임 간의 양방향 일대일 연관관계 - 연관관계 주인은 chatRoom
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    @Setter
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Setter
    @JsonIgnore
    private MeetStatus meetStatus;

    public void update(Meet meet) {
        this.meetName = meet.getMeetName();
        this.description = meet.getDescription();
        this.maxParticipants = meet.getMaxParticipants();
        this.meetDate = meet.getMeetDate();
        this.tag = meet.getTag();
        this.sidoCode = meet.getSidoCode();
        this.gugunCode = meet.getGugunCode();
        this.minAge = meet.getMinAge();
        this.maxAge = meet.getMaxAge();
        this.minLiverPoint = meet.getMinLiverPoint();
        this.updatedAt = LocalDateTime.now();
        this.drink = meet.getDrink();
        this.imgSrc = meet.getImgSrc();
    }

    public MeetRequestDto toDto() {
        return MeetRequestDto.builder().meetId(meetId).meetName(meetName).description(description).hostId(
                host.getUserId()).nowParticipants(nowParticipants).maxParticipants(maxParticipants).meetDate(
                meetDate).tagId(tag.getTagId()).sidoCode(sidoCode).gugunCode(gugunCode).minAge(minAge).maxAge(
                maxAge).minLiverPoint(minLiverPoint).drink(drink).imgSrc(imgSrc).chatRoomId(
                chatRoom.getChatRoomId()).build();
    }

    public MeetResponseDto toMeetResponseDto(Sido sido, Gugun gugun) {
        return MeetResponseDto.builder().meetId(meetId).meetName(meetName).description(description).host(
                                      PublicUserResponseDto.fromEntity(host)).nowParticipants(nowParticipants).maxParticipants(
                                      maxParticipants).meetDate(meetDate).tagId(tag.getTagId()).sido(SidoResponseDto.fromEntity(sido))
                              .gugun(GugunResponseDto.fromEntity(gugun)).minAge(minAge).maxAge(maxAge)
                              .minLiverPoint(minLiverPoint).drink(DrinkResponseDto.fromEntity(drink)).imgSrc(
                        imgSrc).chatRoomId(chatRoom.getChatRoomId()).meetStatus(meetStatus).build();
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        meetStatus = MeetStatus.WAITING;
    }
}
