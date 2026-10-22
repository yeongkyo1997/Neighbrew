package com.ssafy.backend.domain.meet.service.meet.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetAccepted;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetApplied;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetCreated;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetDeleted;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetEnded;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetImageDiscarded;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetImageUploaded;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetRejected;
import com.ssafy.backend.domain.meet.service.meet.event.MeetEvents.MeetUpdated;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetImageManager;
import com.ssafy.backend.domain.meet.service.meet.manager.MeetPushManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 되돌릴 수 없는 부작용(SSE 푸시, S3 삭제)은 DB 커밋이 확정된 뒤에만 실행한다.
// 푸시 알림은 Push를 DB에 저장하므로, 이미 끝난 트랜잭션에 합류하지 않도록 REQUIRES_NEW로 연다.
// 커밋은 이미 끝났으므로 여기서 난 예외는 요청 실패로 돌리지 않고 로그만 남긴다.
@Slf4j
@Component
@RequiredArgsConstructor
public class MeetEventListener {
    private final MeetPushManager pushManager;
    private final MeetImageManager imageManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMeetCreated(MeetCreated event) {
        runSafely("모임 생성 알림", () -> pushManager.notifyFollowersAboutMeetCreation(event.host(), event.meet()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMeetUpdated(MeetUpdated event) {
        runSafely("모임 수정 알림",
                  () -> pushManager.notifyMeetUpdate(event.host(), event.meet(), event.meetUsers()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMeetDeleted(MeetDeleted event) {
        runSafely("모임 삭제 알림", () -> pushManager.notifyMeetDeletion(event.meet(), event.meetUsers()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMeetApplied(MeetApplied event) {
        runSafely("모임 신청 알림", () -> pushManager.notifyMeetApplication(event.applicant(), event.host(),
                                                                     event.meetName(), event.meetId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMeetAccepted(MeetAccepted event) {
        runSafely("모임 승인 알림", () -> pushManager.notifyMeetAcceptance(event.host(), event.user(),
                                                                    event.meetName(), event.meetId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMeetRejected(MeetRejected event) {
        runSafely("모임 거절 알림",
                  () -> pushManager.notifyMeetRejection(event.host(), event.user(), event.meetName()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMeetEnded(MeetEnded event) {
        runSafely("모임 평가 알림", () -> pushManager.notifyMeetEvaluation(event.meet(), event.meetUsers()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMeetImageDiscarded(MeetImageDiscarded event) {
        runSafely("이전 모임 이미지 삭제", () -> imageManager.deleteImageIfNotDefault(event.imagePath()));
    }

    // DB에 반영되지 않은 업로드는 어디에서도 참조하지 않으므로 지운다.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onMeetImageUploadRolledBack(MeetImageUploaded event) {
        runSafely("롤백된 모임 이미지 삭제", () -> imageManager.deleteImageIfNotDefault(event.imagePath()));
    }

    private void runSafely(String action, Runnable task) {
        try {
            task.run();
        } catch (RuntimeException e) {
            log.error("{} 실패", action, e);
        }
    }
}
