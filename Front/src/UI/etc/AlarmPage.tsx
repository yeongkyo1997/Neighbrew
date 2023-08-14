import { callApi } from "../../utils/api";
import { backIcon } from "../../assets/AllIcon";
import styled from "styled-components";
import { useState, useEffect } from "react";
import AlarmItem from "../components/AlarmItem";
import Footer from "../footer/Footer";
import { useNavigate } from "react-router-dom";

const NavbarDiv = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const alarmPage = () => {
  const BackIcon = backIcon();
  const navigate = useNavigate();

  // 현재 더미 텍스트로 이루어져 있습니다. api가 있으면 비어두면 될 것 같습니다.
  const [alarmList, setAlarmList] = useState([
    "이현욱님이 당신을 팔로우했습니다. 프로필을 확인해보세요 ",
    "최준서의 LightWeight 소맥파티 채팅방에 새로운 메세지가 있습니다.",
    "여현빈의 꽐라 모임의 참여가 승인되었습니다. 채팅방을 확인해보세요.",
    "인영교의 취중코딩 모임에 새로운 참가 신청이 도착했습니다.",
    "이다영 님이 새로운 모임을 주최하고있습니다. 프로필을 확인해보세요.",
  ]);

  // 비동기 통신으로 알림을 불러옵니다. api가 있을 때 까지 주석처리.
  // useEffect(() => {
  //   callApi("get", "alarmListUrl")
  //     .then(res => setAlarmList(res.data.content))
  //     .catch(err => console.error(err));
  // }, []);

  const toBackHandler = () => {
    navigate(-1);
  };
  return (
    <>
      {/* 알림창의 내브바 */}
      <NavbarDiv>
        <div style={{ marginLeft: "10px" }} onClick={toBackHandler}>
          {BackIcon}
        </div>
        <div>
          <h2 style={{ fontFamily: "JejuGothic" }}>알림 페이지</h2>
        </div>
        <div style={{ width: "36px", height: "10px", visibility: "hidden" }}></div>
      </NavbarDiv>

      {/* 모든 알림을 리스트로 가정을 하고 map으로 풀어냅니다. 정의될때까지 주석처리. */}
      <div style={{ margin: "0px 10px 0px 10px" }}>
        {alarmList.map((alarm, i) => {
          return <AlarmItem key={i} alarm={alarm}></AlarmItem>;
        })}
      </div>
      <Footer></Footer>
    </>
  );
};
export default alarmPage;