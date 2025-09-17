package com.ssafy.backend.common.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
