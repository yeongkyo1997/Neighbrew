package com.ssafy.backend.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.ssafy.backend.domain.user.entity.User;

import lombok.Getter;

@Entity
@Getter
public class TagUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagUserId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tagId")
    private Tag tag;
}
