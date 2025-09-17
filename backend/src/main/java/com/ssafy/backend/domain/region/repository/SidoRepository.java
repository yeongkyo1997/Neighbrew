package com.ssafy.backend.domain.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.domain.region.entity.Sido;

@Repository
public interface SidoRepository extends JpaRepository<Sido, Integer> {
}
