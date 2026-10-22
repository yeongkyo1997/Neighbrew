// 배경 막과 오른쪽 참여자 서랍
import defaultImg from "../../../assets/defaultImg.png";
import exitImg from "../../../assets/exit.png";
import { RightModal, ImgDiv, Img, UserDiv, UserNameP, BackDrop } from "./ChatRoom.styles";
import { ChatRoomState } from "./useChatRoom";

const ParticipantDrawer = ({
  ismodal,
  users,
  navigate,
  chaterInfoHandler,
  leaveRoom,
}: Pick<
  ChatRoomState,
  | "ismodal"
  | "users"
  | "navigate"
  | "chaterInfoHandler"
  | "leaveRoom"
>) => {
  return (
    <>
      <BackDrop ismodal={ismodal} onClick={chaterInfoHandler}></BackDrop>
      <RightModal ismodal={ismodal}>
        <div style={{ border: "1px solid var(--c-lightgray)" }}></div>
        <br />
        <p
          style={{
            fontFamily: "JejuGothic",
            fontSize: "1.5rem",
            marginBottom: "1.5rem",
          }}
        >
          참여자 목록
        </p>
        <div style={{ width: "80%" }}>
          {users.map((user, i) => {
            return (
              <UserDiv
                key={i}
                onClick={() => {
                  navigate(`/myPage/${user.userId}`);
                }}
              >
                <ImgDiv>
                  <Img
                    src={user.profile == "no image" ? defaultImg : user.profile}
                  ></Img>
                </ImgDiv>
                <UserNameP>
                  {user.nickname?.includes("@")
                    ? user.nickname.split("@")[0]
                    : user.nickname}
                </UserNameP>
              </UserDiv>
            );
          })}
        </div>
        <div style={{ position: "fixed", top: "80%" }}>
          {/* <button onClick={OutRoomHandler}>채팅방 나가기</button> */}
          <img src={exitImg} alt="" onClick={leaveRoom} />
        </div>
      </RightModal>
    </>
  );
};

export default ParticipantDrawer;
