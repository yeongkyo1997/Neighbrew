package com.ssafy.backend.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.backend.common.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
