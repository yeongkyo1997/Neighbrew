package com.ssafy.backend.common.service;

import org.springframework.stereotype.Service;

import com.ssafy.backend.common.entity.Tag;
import com.ssafy.backend.common.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag findById(Long tagId) {
        return tagRepository.findById(tagId).orElseThrow(
                () -> new IllegalArgumentException("해당 태그를 찾을 수 없습니다."));
    }
}
