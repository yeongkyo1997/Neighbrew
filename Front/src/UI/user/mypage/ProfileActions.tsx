// 닉네임, 팔로우 수, 소개, 팔로우·메세지·프로필 수정 버튼
import { MyPageState } from "./useMyPage";
import { FollowDiv, SirenArea } from "./MyPage.styles";

const ProfileActions = ({
  userData,
  following,
  followHandler,
  goFollowerPage,
  goFollowPage,
  directMessageHandler,
  reportHandler,
  modalHandler,
}: Pick<
  MyPageState,
  | "userData"
  | "following"
  | "followHandler"
  | "goFollowerPage"
  | "goFollowPage"
  | "directMessageHandler"
  | "reportHandler"
  | "modalHandler"
>) => {
  return (
    <>
      <p
        style={{
          textAlign: "left",
          padding: "0 1rem",
          fontFamily: "NanumSquareNeo",
          fontWeight: "800",
          fontSize: "1.3rem",
        }}
      >
        {userData.nickname}
      </p>
      <div
        style={{
          textAlign: "left",
          padding: "0 1rem",
          fontSize: "15px",
          fontFamily: "Jejugothic",
          marginTop: "0.5rem",
        }}
      >
        <span onClick={goFollowerPage}>팔로워 {userData.follower}</span>{" "}
        <span onClick={goFollowPage}>팔로잉 {userData.following} &nbsp;&nbsp; </span>
      </div>
      <div
        style={{
          textAlign: "left",
          padding: "0 1rem",
          fontSize: "12px",
          margin: "0.5rem 0",
          fontFamily: "Jejugothic",
          color: "#777777",
        }}
      >
        <p>{userData.intro}</p>
      </div>
      {userData.userId !== parseInt(localStorage.getItem("myId")) && (
        <FollowDiv>
          <button
            style={{
              backgroundColor: following === 0 ? "var(--c-yellow)" : "var(--c-lightgray)",
              border: "none",
              borderRadius: "8px",
              fontFamily: "JejuGothic",
              cursor: "pointer",
              flex: "0 0 40%", // flex-grow, flex-shrink, flex-basis
            }}
            onClick={() => void followHandler()}
          >
            {following === 0 ? "팔로우" : "언팔로우"}
          </button>
          <button
            style={{
              border: "none",
              borderRadius: "8px",
              fontFamily: "JejuGothic",
              cursor: "pointer",
              flex: "0 0 40%", // flex-grow, flex-shrink, flex-basis
            }}
            onClick={directMessageHandler}
          >
            메세지
          </button>
          <SirenArea onClick={reportHandler}></SirenArea>
        </FollowDiv>
      )}
      {userData.userId === parseInt(localStorage.getItem("myId")) && (
        <FollowDiv>
          <button
            style={{
              backgroundColor: "var(--c-lightgray)",
              border: "none",
              borderRadius: "8px",
              fontFamily: "JejuGothic",
              cursor: "pointer",
              flex: "0 0 40%", // flex-grow, flex-shrink, flex-basis
            }}
            onClick={modalHandler}
          >
            프로필 수정
          </button>
        </FollowDiv>
      )}
    </>
  );
};

export default ProfileActions;
