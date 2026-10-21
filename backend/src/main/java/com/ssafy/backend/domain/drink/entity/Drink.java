package com.ssafy.backend.domain.drink.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(indexes = @Index(name = "idx_drink_name", columnList = "name"))
public class Drink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long drinkId;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, columnDefinition = "varchar(255) default 'no image'")
    private String image = "no image";

    @Column(nullable = false, columnDefinition = "float default 0.0")
    private Float degree = 0.0f;

    @Lob
    private String description;

    // 주종 태그 id
    private Long tagId;

    @PrePersist
    public void prePersist() {
        if (this.image == null) {this.image = "no image";}
        if (this.degree == null) {this.degree = 0.0f;}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Drink drink = (Drink) o;
        return Objects.equals(drinkId, drink.drinkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(drinkId);
    }
}
