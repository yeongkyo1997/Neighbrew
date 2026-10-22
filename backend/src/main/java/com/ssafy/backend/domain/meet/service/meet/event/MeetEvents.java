package com.ssafy.backend.domain.meet.service.meet.event;

import java.util.List;

import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.meet.entity.MeetUser;
import com.ssafy.backend.domain.user.entity.User;

// MeetService가 트랜잭션 안에서 발행하고, MeetEventListener가 커밋(또는 롤백) 뒤에 처리하는 이벤트.
// 커밋 뒤에는 지연 로딩을 할 수 없으므로 즉시 로딩된 값(Meet.host, MeetUser.user)만 담는다.
public final class MeetEvents {
    private MeetEvents() {
    }

    public record MeetCreated(User host, Meet meet) {
    }

    public record MeetUpdated(User host, Meet meet, List<MeetUser> meetUsers) {
    }

    public record MeetDeleted(Meet meet, List<MeetUser> meetUsers) {
    }

    public record MeetApplied(User applicant, User host, String meetName, Long meetId) {
    }

    public record MeetAccepted(User host, User user, String meetName, Long meetId) {
    }

    public record MeetRejected(User host, User user, String meetName) {
    }

    public record MeetEnded(Meet meet, List<MeetUser> meetUsers) {
    }

    // 이번 트랜잭션에서 새로 올린 이미지. 롤백되면 지운다.
    public record MeetImageUploaded(String imagePath) {
    }

    // 더 이상 쓰지 않는 이미지(수정 전 이미지, 삭제된 모임의 이미지). 커밋되면 지운다.
    public record MeetImageDiscarded(String imagePath) {
    }
}
