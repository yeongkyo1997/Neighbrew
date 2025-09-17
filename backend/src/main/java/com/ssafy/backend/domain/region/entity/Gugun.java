package com.ssafy.backend.domain.region.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@IdClass(GugunPK.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Gugun {
    @Id
    @Column(name = "gugun_code")
    private Integer gugunCode;

    @Column(length = 30)
    private String gugunName;

    @Id
    @Column(name = "sido_code")
    private Integer sidoCode;
}
