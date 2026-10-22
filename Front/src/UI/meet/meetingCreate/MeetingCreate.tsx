/*
[MeetingCreate.tsx]
모임 생성 페이지
모임 이름, 주종 카테고리, 술 검색, 위치, 시간, 조건, 설명, 이미지 첨부 가능
*/
import NavbarSimple from "../../navbar/NavbarSimple";
import FooterBigBtn from "../../footer/FooterBigBtn";
import useMeetingCreate from "./useMeetingCreate";
import CreateBasicFields from "./CreateBasicFields";
import CreateLocationField from "./CreateLocationField";
import CreateDateField from "./CreateDateField";
import CreateConditionFields from "./CreateConditionFields";
import CreateDescImageFields from "./CreateDescImageFields";
import CreateModals from "./CreateModals";

const MeetingCreate = () => {
  const state = useMeetingCreate();
  const { createMeeting } = state;
  return (
    <div>
      <header>
        <NavbarSimple title="모임 만들기" />
      </header>
      <div style={{ padding: "0 1.5rem", marginBottom: "7rem" }}>
        <CreateBasicFields {...state} />
        <CreateLocationField {...state} />
        <CreateDateField {...state} />
        <CreateConditionFields {...state} />
        <CreateDescImageFields {...state} />
      </div>
      <FooterBigBtn
        content="모임 만들기"
        color="var(--c-yellow)"
        bgColor="white"
        reqFunc={() => {
          createMeeting();
        }}
      />
      <CreateModals {...state} />
    </div>
  );
};
export default MeetingCreate;
