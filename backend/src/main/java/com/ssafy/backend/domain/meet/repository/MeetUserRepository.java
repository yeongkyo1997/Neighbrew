package com.ssafy.backend.domain.meet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.common.enums.Status;
import com.ssafy.backend.domain.meet.entity.MeetUser;

@Repository
public interface MeetUserRepository extends JpaRepository<MeetUser, Long> {
    @EntityGraph(attributePaths = {"user", "meet", "meet.host", "meet.tag", "meet.drink"})
    List<MeetUser> findByUserUserIdOrderByMeetCreatedAtDesc(Long userId);

    void deleteByMeetMeetId(Long meetId);

    List<MeetUser> findByMeetMeetIdOrderByStatusDesc(Long meetId);

    Optional<MeetUser> findByUserUserIdAndMeetMeetId(Long userId, Long meetId);

    void deleteByUserUserIdAndMeetMeetIdAndStatus(Long userId, Long meetId, Status status);

    @Query("select mu.status from MeetUser mu where mu.user.userId = :userId and mu.meet.meetId = :meetId")
    Status findStatusByUserIdAndMeetId(@Param("userId") Long userId,
                                       @Param("meetId") Long meetId);

    @Query("select mu.status from MeetUser mu where mu.user.userId = :userId and mu.meet.chatRoom.chatRoomId = :chatRoomId")
    Status findStatusByUserIdAndChatRoomId(@Param("userId") Long userId,
                                           @Param("chatRoomId") Long chatRoomId);

    Optional<Long> countByMeetMeetId(Long meetId);
}

