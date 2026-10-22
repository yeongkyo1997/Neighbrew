// 썸네일 배경 위의 뒤로가기·태그·채팅 버튼, 모임 이름, 주최자, 인원
import { arrowLeftIcon } from "../../../assets/AllIcon";
import PeopleNumInfo from "../PeopleNumInfo";
import defaultImg from "../../../assets/defaultImg.png";
import { getTagName } from "../../common";
import { MeetThumbnail, DetailHeader, Tag, ChatBtn, Title, HostInfo, UserProfileImg } from "./MeetingDetail.styles";
import { MeetingDetailState } from "./useMeetingDetail";

const MeetHeader = ({
  meetDetailData,
  memberList,
  userStatus,
  bgImg,
  GoBackHandler,
  gotoChat,
}: Pick<
  MeetingDetailState,
  | "meetDetailData"
  | "memberList"
  | "userStatus"
  | "bgImg"
  | "GoBackHandler"
  | "gotoChat"
>) => {
  const ArrowLeftIcon = arrowLeftIcon("white");
  return (
    <MeetThumbnail $bgImgSrc={bgImg}>
      <DetailHeader>
        <div style={{ display: "flex", alignItems: "center" }}>
          <div style={{ cursor: "pointer", marginRight: "1rem" }} onClick={GoBackHandler}>
            {ArrowLeftIcon}
          </div>
          <Tag>{getTagName(meetDetailData.meet.tagId)}</Tag>
        </div>
        {userStatus !== "APPLY" && userStatus !== "NONE" && (
          <ChatBtn onClick={gotoChat}>채팅 참여하기</ChatBtn>
        )}
      </DetailHeader>
      <div style={{ textAlign: "center", padding: "2rem 0 7rem 0" }}>
        <Title>{meetDetailData.meet.meetName}</Title>
        <HostInfo>
          <div>주최자: </div>
          {memberList[0] && (
            <div style={{ display: "flex", alignItems: "center" }}>
              <UserProfileImg
                src={memberList[0].profile === "no image" ? defaultImg : memberList[0].profile}
              />
              <div>{memberList[0].nickname}</div>
            </div>
          )}
        </HostInfo>
        <div style={{ display: "flex", justifyContent: "center" }}>
          <PeopleNumInfo
            now={meetDetailData.meet.nowParticipants}
            max={meetDetailData.meet.maxParticipants}
            color="white"
            size={15}
          />
        </div>
      </div>
    </MeetThumbnail>
  );
};

export default MeetHeader;
