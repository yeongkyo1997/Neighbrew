// 프로필 사진, 간수치, 술병
import { Tooltip } from "react-tooltip";
import { User } from "../../../Type/types";
import bottle from "../../../assets/bottle.png";
import defaultImg from "../../../assets/defaultImg.png";
import liver from "../../../assets/liver.png";
import { BottleDiv, FlexDivRow, Img, ImgDiv, InfoDiv, LiverDiv, UserDiv, UserImgDiv } from "./MyPage.styles";

const ProfileSummary = ({ userData }: { userData: User }) => {
  return (
    <UserDiv>
      <FlexDivRow>
        <ImgDiv>
          <Img src={userData.profile == "no image" ? defaultImg : userData.profile}></Img>
        </ImgDiv>
        <InfoDiv>
          <UserImgDiv data-tooltip-id="liver-tooltip">
            <p style={{ marginBottom: "0.5rem" }}>
              {parseFloat(userData.liverPoint.toFixed(1))} IU/L
            </p>
            <LiverDiv liverpoint={userData.liverPoint ?? 40}>
              <Img src={liver} alt="" />
            </LiverDiv>
            <p style={{ marginTop: "0.2rem" }}>간수치</p>
          </UserImgDiv>
          <Tooltip
            id="liver-tooltip"
            style={{
              backgroundColor: "var(--c-pink)",
              color: "black",
              fontSize: "12px",
              width: "10rem",
              textAlign: "justify",
              wordBreak: "break-all",
            }}
          >
            <div
              style={{
                fontWeight: "700",
                marginTop: "0.3rem",
                textAlign: "center",
              }}
            >
              간수치?
            </div>
            <div style={{ marginTop: "0.3rem" }}>
              네이브루 사용자로부터 받은 칭찬, 후기, 비매너 평가 등을 종합해서 만든 매너
              지표입니다.
            </div>
            <div style={{ margin: "0.3rem 0" }}>
              간수치는 40 IU/L에서 시작해서 0~100 IU/L 사이의 값을 가집니다.
            </div>
          </Tooltip>
          <UserImgDiv data-tooltip-id="drink-tooltip">
            <p style={{ marginBottom: "0.5rem" }}>{userData.drinkcount}병</p>
            <BottleDiv drinkcount={userData.drinkcount ?? 2}>
              <Img src={bottle} alt="" />
            </BottleDiv>
            <p style={{ marginTop: "0.2rem" }}>술병</p>
          </UserImgDiv>
          <Tooltip
            id="drink-tooltip"
            style={{
              backgroundColor: "var(--c-yellow)",
              color: "black",
              fontSize: "12px",
              width: "8rem",
              textAlign: "justify",
              wordBreak: "break-all",
            }}
          >
            <div
              style={{
                fontWeight: "700",
                marginTop: "0.3rem",
                textAlign: "center",
              }}
            >
              술병?
            </div>
            <div style={{ margin: "0.3rem 0" }}>
              사용자가 술을 마시고 남긴 후기 수를 의미합니다.
            </div>
          </Tooltip>
        </InfoDiv>
      </FlexDivRow>
    </UserDiv>
  );
};

export default ProfileSummary;
