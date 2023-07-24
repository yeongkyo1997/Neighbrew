/*
[MeetingMy.tsx]
내 모임 페이지
내가 주최 중인 모임, 내가 참여 중인 모임, 내가 신청한 모임 출력
*/
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import ListInfoItem from "../components/ListInfoItem";
import MeetingDetail from "./../meet/MeetingDetailSimple";
import PeopleNumInfo from "./../meet/PeopleNumInfo";

const MeetingDiv = styled.div`
  margin-bottom: 2rem;
`;

const MeetTitle = styled.div`
  font-family: "JejuGothic";
  font-size: 20px;
  text-align: left;
`;

const meetingMy = () => {
  const navigate = useNavigate();

  const GotoMeetDetailHandler = (meetId: number) => {
    console.log(meetId, "my");
    navigate(`/meet/${meetId}`);
  };

  return (
    <div style={{ background: "var(--c-lightgray)", padding: "1.5rem", minHeight: "760px" }}>
      <MeetingDiv>
        <MeetTitle>내가 주최 중인 모임</MeetTitle>
        <ListInfoItem
          title="내가 주최 중인 모임의 이름"
          tag="소주/맥주"
          content={<MeetingDetail />}
          numberInfo={<PeopleNumInfo now={1} max={1} color={"var(--c-black)"} />}
          isWaiting={false}
          routingFunc={() => GotoMeetDetailHandler(1)}
        ></ListInfoItem>
      </MeetingDiv>
      <MeetingDiv>
        <MeetTitle>내가 참여 중인 모임</MeetTitle>
        <ListInfoItem
          title="내가 참여 중인 모임의 이름"
          tag="소주/맥주"
          content={<MeetingDetail />}
          numberInfo={<PeopleNumInfo now={1} max={1} color={"var(--c-black)"} />}
          isWaiting={false}
          routingFunc={() => GotoMeetDetailHandler(1)}
        ></ListInfoItem>
      </MeetingDiv>
    </div>
  );
};
export default meetingMy;