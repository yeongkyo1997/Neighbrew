package com.ssafy.backend.domain.push.service.push;

import com.ssafy.backend.common.enums.PushType;

// 푸시 알림의 종류, 문구, 이동 경로를 한 곳에서 만든다.
// path는 서비스 안의 경로("/"로 시작)만 담고, 도메인은 PushService가 붙인다.
public record PushMessage(PushType pushType, String content, String path) {
    public PushMessage {
        if (path == null || !path.startsWith("/")) {
            throw new IllegalArgumentException("푸시 이동 경로는 /로 시작해야 합니다: " + path);
        }
    }

    public static PushMessage reviewLike(String likerNickname, Long drinkId, Long reviewId) {
        return new PushMessage(PushType.REVIEWLIKE, likerNickname + "님이 회원님의 후기를 좋아합니다. ",
                               drinkReviewPath(drinkId, reviewId));
    }

    public static PushMessage subReview(String commenterNickname, Long drinkId, Long reviewId) {
        return new PushMessage(PushType.SUBREVIEW, commenterNickname + "님이 회원님의 후기에 댓글을 남겼습니다.",
                               drinkReviewPath(drinkId, reviewId));
    }

    public static PushMessage follow(String followerNickname, Long followerId) {
        return new PushMessage(PushType.FOLLOW, followerNickname + "님이 회원님을 팔로우하기 시작했습니다.",
                               "/mypage/" + followerId);
    }

    public static PushMessage directMessage(String senderNickname, Long senderId, Long receiverId) {
        return new PushMessage(PushType.DM, senderNickname + "님께서 메세지를 보냈습니다.",
                               "/directchat/" + senderId + "/" + receiverId);
    }

    public static PushMessage meetCreated(String hostNickname, String meetName, Long meetId) {
        return new PushMessage(PushType.MEETCREATED, hostNickname + "님께서 모임(" + meetName + ")을 생성했습니다.",
                               meetPath(meetId));
    }

    public static PushMessage meetModified(String meetName, Long meetId) {
        return new PushMessage(PushType.MEETMODIFIDE, "모임( " + meetName + ")의 내용이 수정되었습니다. 확인해 주세요.",
                               meetPath(meetId));
    }

    public static PushMessage meetDeleted(String hostNickname, String meetName) {
        return new PushMessage(PushType.MEETDELETED,
                               hostNickname + "님 께서 생성한 모임(" + meetName + ")이 삭제되었습니다.", "/meet");
    }

    // 신청 취소 시 PushType.MEETACCESS로 이 알림을 지운다(MeetPushManager.deletePushLog).
    public static PushMessage meetApplied(String applicantNickname, String meetName, Long meetId) {
        return new PushMessage(PushType.MEETACCESS,
                               applicantNickname + "님께서 " + meetName + "모임에 참여하고 싶어 합니다.", meetPath(meetId));
    }

    public static PushMessage meetAccepted(String meetName, Long meetId) {
        return new PushMessage(PushType.MEETACCESS, "회원님께서 모임(" + meetName + ")참여 되셨습니다.\n 즐거운 시간 되세요.",
                               meetPath(meetId));
    }

    public static PushMessage meetRejected(String meetName) {
        return new PushMessage(PushType.MEETREJECT, "회원님께서 모임(" + meetName + ")참여에 거절당했습니다.", "/meet");
    }

    public static PushMessage meetEvaluation(String meetName, Long meetId) {
        return new PushMessage(PushType.MEETEVALUATION, "모임(" + meetName + ")이 종료 되었습니다. 평가를 진행해 주세요.",
                               "/rating/" + meetId);
    }

    private static String meetPath(Long meetId) {
        return "/meet/" + meetId;
    }

    private static String drinkReviewPath(Long drinkId, Long reviewId) {
        return "/drinkpost/" + drinkId + "/" + reviewId;
    }
}
