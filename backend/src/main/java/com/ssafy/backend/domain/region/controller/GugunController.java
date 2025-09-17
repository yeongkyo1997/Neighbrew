package com.ssafy.backend.domain.region.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.domain.region.dto.GugunResponseDto;
import com.ssafy.backend.domain.region.service.GugunService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gugun")
public class GugunController {
    private final GugunService gugunService;

    @GetMapping("/{sidoCode}")
    public List<GugunResponseDto> getGugunsBySidoCode(@PathVariable Integer sidoCode) {
        return gugunService.getGugunsBySidoCode(sidoCode);
    }
}
