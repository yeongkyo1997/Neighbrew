package com.ssafy.backend.domain.drink.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

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
