package com.ssafy.backend.domain.meet.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.common.enums.MeetStatus;
import com.ssafy.backend.domain.meet.entity.Meet;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {
    @Query("select m.imgSrc from Meet m where m.meetId = :meetId")
    String findImgSrcByMeetId(@Param("meetId") Long meetId);

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

