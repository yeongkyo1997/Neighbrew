/*
[MeetingInfoManage.tsx]
모임 정보 관리 페이지
모임 이름, 주종 카테고리, 술 검색, 위치, 시간, 조건, 설명, 이미지 첨부 가능
*/
import NavbarSimple from "../../navbar/NavbarSimple";
import FooterBigBtn from "../../footer/FooterBigBtn";
import useMeetingInfoManage from "./useMeetingInfoManage";
import ManageBasicFields from "./ManageBasicFields";
import ManageLocationField from "./ManageLocationField";
import ManageDateField from "./ManageDateField";
import ManageConditionFields from "./ManageConditionFields";
import ManageDescImageFields from "./ManageDescImageFields";
import ManageModals from "./ManageModals";

const MeetingInfoManage = () => {
  const state = useMeetingInfoManage();
  const { updateMeeting } = state;
  return (
    <div>
      <header>
        <NavbarSimple title="모임 관리" />
      </header>
      <div style={{ padding: "0 1.5rem", marginBottom: "7rem" }}>
        <ManageBasicFields {...state} />
        <ManageLocationField {...state} />
        <ManageDateField {...state} />
        <ManageConditionFields {...state} />
        <ManageDescImageFields {...state} />
      </div>
      <FooterBigBtn
        content="정보 수정 완료"
        color="var(--c-yellow)"
        bgColor="white"
        reqFunc={() => {
          updateMeeting();
        }}
      />
      <ManageModals {...state} />
    </div>
  );
};
export default MeetingInfoManage;
