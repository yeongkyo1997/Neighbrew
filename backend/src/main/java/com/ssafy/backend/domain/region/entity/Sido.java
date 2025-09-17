package com.ssafy.backend.domain.region.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sidoCode;

    @Column(length = 30)
    private String sidoName;
}
