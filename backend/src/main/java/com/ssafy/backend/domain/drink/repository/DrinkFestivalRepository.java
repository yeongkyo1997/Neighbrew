package com.ssafy.backend.domain.drink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.domain.drink.entity.DrinkFestival;

@Repository
public interface DrinkFestivalRepository extends JpaRepository<DrinkFestival, Long> {
}
