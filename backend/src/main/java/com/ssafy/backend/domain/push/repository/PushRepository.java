package com.ssafy.backend.domain.push.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.backend.common.enums.PushType;
import com.ssafy.backend.domain.push.entity.Push;

// DB에 저장하기 위한 Repository
@Repository
public interface PushRepository extends JpaRepository<Push, Long> {
    List<Push> findByReceiver_UserIdOrderByCreatedAtDesc(Long receiverId);

    // SSE 재연결 시 마지막으로 받은 알림(pushId) 이후의 알림을 다시 보낼 때 쓴다
    List<Push> findTop50ByReceiver_UserIdAndPushIdGreaterThanOrderByPushIdAsc(Long receiverId, Long pushId);

    @Modifying
    @Query("DELETE FROM Push p WHERE p.pushType = :pushType AND p.receiver.userId = :receiverId AND p.sender.userId = :senderId")
    void deleteByPushTypeAndSenderUserIdAndReceiverUserId(@Param("pushType") PushType pushType,
                                                          @Param("senderId") Long senderId,
                                                          @Param("receiverId") Long receiverId);
}




