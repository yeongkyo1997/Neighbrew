package com.ssafy.backend.domain.region.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.domain.region.entity.Sido;
import com.ssafy.backend.domain.region.service.SidoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sido")
public class SidoController {
    private final SidoService sidoService;

    @GetMapping
    public ResponseEntity<List<Sido>> getAllSido() {
        return ResponseEntity.ok(sidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sido> getSidoById(@PathVariable Integer id) {
        return ResponseEntity.ok(sidoService.findById(id));
    }
}
