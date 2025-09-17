package com.ssafy.backend.domain.chat.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssafy.backend.domain.meet.entity.Meet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @Lob
    @Column(nullable = false)
    private String chatRoomName;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ChatRoomUser> users;

    @OneToOne(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonIgnore
    private Meet meet;
}
