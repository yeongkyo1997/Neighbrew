// 모임 상태와 내 참여 상태에 따라 바뀌는 하단 버튼
import FooterBigBtn from "../../footer/FooterBigBtn";
import { MeetingDetailState } from "./useMeetingDetail";

const MeetFooterButtons = ({
  meetDetailData,
  userStatus,
  GotoRatingHandler,
  setManageModalOn,
  setExitModalOn,
  cancelApply,
  applyMeet,
}: Pick<
  MeetingDetailState,
  | "meetDetailData"
  | "userStatus"
  | "GotoRatingHandler"
  | "setManageModalOn"
  | "setExitModalOn"
  | "cancelApply"
  | "applyMeet"
>) => {
  return (
    <>
      {meetDetailData.meet.meetStatus === "END" && (
        <FooterBigBtn
          content="모임 후기 남기기"
          reqFunc={GotoRatingHandler}
          color="var(--c-yellow)"
          bgColor="var(--c-lightgray)"
        />
      )}
      {userStatus === "HOST" && meetDetailData.meet.meetStatus === "WAITING" && (
        //user 상태에 따라 버튼 변경
        <FooterBigBtn
          content="모임 관리"
          reqFunc={() => setManageModalOn(true)}
          color="var(--c-yellow)"
          bgColor="var(--c-lightgray)"
        />
      )}
      {userStatus === "GUEST" && meetDetailData.meet.meetStatus === "WAITING" && (
        <FooterBigBtn
          content="모임 나가기"
          reqFunc={() => setExitModalOn(true)} //모임 나가기
          color="#F28F79"
          bgColor="var(--c-lightgray)"
        />
      )}
      {userStatus === "APPLY" && meetDetailData.meet.meetStatus === "WAITING" && (
        <FooterBigBtn
          content="승인 대기 중"
          reqFunc={() => cancelApply()} //참여신청 취소하기
          color="var(--c-gray)"
          bgColor="var(--c-lightgray)"
        />
      )}
      {userStatus === "NONE" && meetDetailData.meet.meetStatus !== "END" && (
        <FooterBigBtn
          content="참여 신청하기"
          reqFunc={() => applyMeet()} //참여신청하기
          color="var(--c-yellow)"
          bgColor="var(--c-lightgray)"
        />
      )}
    </>
  );
};

export default MeetFooterButtons;
