/*
[MeetingDetail.tsx]
특정한 모임의 상세 정보를 보여주는 페이지
모임 리스트에서 하나를 클릭하면 이 페이지로 이동함
모임 위치, 시간, 주최자, 간수치제한, 인원 제한 정보를 담고 있음
*/
import useMeetingDetail from "./useMeetingDetail";
import { MeetDetailDiv } from "./MeetingDetail.styles";
import MeetHeader from "./MeetHeader";
import MeetInfoSection from "./MeetInfoSection";
import MeetMembers from "./MeetMembers";
import MeetModals from "./MeetModals";
import MeetFooterButtons from "./MeetFooterButtons";

const MeetingDetail = () => {
  const state = useMeetingDetail();
  return (
    <div style={{ color: "var(--c-black)" }}>
      <MeetHeader {...state} />
      <MeetDetailDiv>
        <MeetInfoSection {...state} />
        <MeetMembers {...state} />
        <MeetModals {...state} />
      </MeetDetailDiv>
      <MeetFooterButtons {...state} />
    </div>
  );
};
export default MeetingDetail;
