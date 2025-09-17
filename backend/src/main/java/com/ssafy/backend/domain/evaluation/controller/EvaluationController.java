package com.ssafy.backend.domain.evaluation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.common.util.JwtUtil;
import com.ssafy.backend.domain.evaluation.dto.EvaluationRequestDto;
import com.ssafy.backend.domain.evaluation.service.evaluation.EvaluationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/evaluation")
public class EvaluationController {
    private final EvaluationService evaluationService;

    @PostMapping("")
    public ResponseEntity<?> goodEvaluation(@RequestBody EvaluationRequestDto evaluationRequestDto,
                                            @RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.parseUserIdFromToken(token);
        return ResponseEntity.ok(evaluationService.calculateScoreByMeetId(evaluationRequestDto, userId));
    }
}
