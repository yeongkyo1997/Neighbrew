package com.ssafy.backend.domain.region.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.region.entity.Sido;
import com.ssafy.backend.domain.region.repository.SidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SidoService {

    private final SidoRepository sidoRepository;

    public List<Sido> findAll() {
        return sidoRepository.findAll();
    }

    public Sido findById(Integer id) {
        return sidoRepository.findById(id).orElse(null);
    }
}
