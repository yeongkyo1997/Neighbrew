package com.ssafy.backend.domain.drink.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.drink.entity.DrinkFestival;
import com.ssafy.backend.domain.drink.repository.DrinkFestivalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrinkFestivalService {
    private final DrinkFestivalRepository drinkFestivalRepository;

    public List<DrinkFestival> getAllFestival() {
        return drinkFestivalRepository.findAll();
    }
}
