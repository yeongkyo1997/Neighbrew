package com.ssafy.backend.domain.region.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ssafy.backend.domain.region.dto.GugunResponseDto;
import com.ssafy.backend.domain.region.repository.GugunRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GugunService {
    private final GugunRepository gugunRepository;

    public List<GugunResponseDto> getGugunsBySidoCode(Integer sidoCode) {
        return gugunRepository.findBySidoCode(sidoCode)
                              .stream().map(GugunResponseDto::fromEntity)
                              .collect(Collectors.toList());
    }
}
