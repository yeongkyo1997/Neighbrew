package com.ssafy.backend.domain.meet.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.domain.meet.entity.Meet;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {
    // 참여 인원을 바꾸는 요청(승인, 나가기)이 같은 모임에서 동시에 실행되지 않도록 행 잠금을 건다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Meet m where m.meetId = :meetId")
    Optional<Meet> findByIdForUpdate(@Param("meetId") Long meetId);

    @EntityGraph(attributePaths = {"host", "tag", "drink"})
    Page<Meet> findByMeetStatusNotOrderByCreatedAtDesc(MeetStatus meetStatus, Pageable pageable);

    @EntityGraph(attributePaths = {"host", "tag", "drink"})
    Page<Meet> findByTagTagIdAndMeetStatusNotOrderByCreatedAtDesc(Long tagId, MeetStatus meetStatus,
                                                                  Pageable pageable);

    // 현재시간보다 모임시간이 빠르고 아직 종료 처리되지 않은 모임
    @Query("SELECT m FROM Meet m WHERE m.meetDate < CURRENT_TIMESTAMP"
           + " AND (m.meetStatus IS NULL OR m.meetStatus <> com.ssafy.backend.common.enums.MeetStatus.END)")
    List<Meet> findMeetByMeetDateBefore();
}

