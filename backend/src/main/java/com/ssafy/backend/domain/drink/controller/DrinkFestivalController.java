package com.ssafy.backend.domain.drink.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.domain.drink.service.DrinkFestivalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drinkFestival")
public class DrinkFestivalController {
    private final DrinkFestivalService drinkFestivalService;

    @GetMapping("/all")
    public ResponseEntity<?> getReviewsByDrinkId() {
        return ResponseEntity.ok(drinkFestivalService.getAllFestival());
    }
}
