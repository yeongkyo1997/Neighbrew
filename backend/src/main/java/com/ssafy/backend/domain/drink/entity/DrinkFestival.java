package com.ssafy.backend.domain.drink.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Getter;

@Entity
@Getter
public class DrinkFestival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long drinkFestivalId;

    @Column(length = 50)
    private String name;

    @Lob
    private String image;

    @Lob
    private String redirectUri;
}
