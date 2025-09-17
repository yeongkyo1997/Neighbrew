package com.ssafy.backend.domain.region.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.backend.domain.region.entity.Gugun;
import com.ssafy.backend.domain.region.entity.GugunPK;

public interface GugunRepository extends JpaRepository<Gugun, GugunPK> {
    List<Gugun> findBySidoCode(Integer sidoCode);

    Optional<Gugun> findBySidoCodeAndGugunCode(Integer sidoCode, Integer gugunCode);
}
