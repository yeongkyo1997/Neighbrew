package com.ssafy.backend.domain.drink.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.domain.drink.entity.Drink;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long> {
    // 술 이름과 태그로 조회
    Page<Drink> findByNameContainsAndTagId(String name, Long tagId, Pageable pageable);

    // 술 태그로 조회
    Page<Drink> findByTagId(Long tagId, Pageable pageable);

    // 술 이름으로 조회
    Page<Drink> findByNameContains(String name, Pageable pageable);

}

