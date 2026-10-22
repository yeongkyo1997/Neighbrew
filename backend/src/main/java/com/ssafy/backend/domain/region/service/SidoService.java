package com.ssafy.backend.domain.region.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.region.entity.Sido;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SidoService {

    private final RegionCache regionCache;

    public List<Sido> findAll() {
        return regionCache.getAllSidos();
    }

    public Sido findById(Integer id) {
        return regionCache.findSido(id).orElse(null);
    }
}
