// MyPage 화면의 styled 컴포넌트와 모달 스타일
import styled from "styled-components";

export const QuestionDiv = styled.div`
  margin-top: 1.5rem;
`;

export const Title = styled.div`
  font-family: "JejuGothic";
  font-size: 18px;
  text-align: left;
  margin-bottom: 0.5rem;
`;

export const ImgInput = styled.div`
  // label로 대신하고 input은 숨기기 위한 css
  input[type="file"] {
    position: absolute;
    width: 0;
    height: 0;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    border: 0;
  }
`;

export const ImageArea = styled.div<{ src: string }>`
  background: url(${props => props.src}) no-repeat center;
  background-size: cover;
  border-radius: 50%;
  position: relative;
  width: 50%;
  padding-bottom: 50%;
  overflow: hidden;
`;
export const SirenArea = styled.div`
  background: url("/src/assets/siren.png") no-repeat center;
  //background-size : cover;
  background-size: contain;
  overflow: hidden;
  flex-basis: 10%; // flex-grow, flex-shrink, flex-basis
`;
// user 이미지, 간수치, 술장이 들어갈 div
export const FlexDivRow = styled.div`
  /* width: 100%; */
  display: flex;
  justify-content: space-between;
  padding: 0 1rem;
  align-items: center;
`;

export const ImgDiv = styled.div`
  flex-basis: 30%;
  //height: 50%;
  overflow: hidden;
  min-width: 8rem;
  aspect-ratio: 1/1;
  border-radius: 50%;
  float: left;
`;

export const InfoDiv = styled.div`
  display: flex;
  flex-direction: row;
  flex-basis: 70%;
  justify-content: center;
`;

export const FollowDiv = styled.div`
  display: flex;
  justify-content: space-between;
  height: 1.5rem;
  padding: 0 1rem;
  /* min-height: 1rem; */
`;
// 간수치, 주종이 들어갈 imgdiv
export const UserImgDiv = styled.div`
  width: 25%;
  height: 100%;
  min-height: 5rem;
  max-height: 6rem;
  min-width: 3rem;
  object-fit: cover;
  max-width: 4rem;
  overflow: hidden;
  padding: 0.5rem;
  margin: 0 0.5rem;
  background-color: var(--c-lightgray);
  border-radius: 1rem;
  font-size: 12px;
  font-family: "JejuGothic";
  font-weight: 500;
`;

export const Button = styled.button`
  width: 45%;
  display: inline-block;
  height: 3rem;
  background-color: white;
  border: none;
  font-size: 12px;
  font-family: "JejuGothic";
  /* margin: 1rem auto; */
`;

export const Img = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

export const UserDiv = styled.div`
  width: 100%;
  /* height: 180px; */
  align-items: center;
  justify-content: space-evenly;
  margin-bottom: 0.5rem;
`;

// 간수치, 술병 등의 div
export const LiverDiv = styled.div<{ liverpoint: number }>`
  /* position: relative; */
  /* height: 100%; */
  overflow: hidden;
  /* background-image: linear-gradient(to top, #e591a1 50%, #ececec 50%); */
  background-image: linear-gradient(to top, var(--c-pink) 50%, #ececec 50%);
  background-size: ${props => `50% ${props.liverpoint + 80}%`};
  /* background-size: 50% 150%; */
  animation: fillAnimation 5s forwards;
  @keyframes fillAnimation {
    0% {
      background-position: 0 0;
    }
    100% {
      background-position: 0 100%;
    }
  }

  aspect-ratio: 1.2/1; /* Added to set the aspect ratio to 1:1 */
  display: flex;
  align-items: center;
  justify-content: center;
`;
export const BottleDiv = styled.div<{ drinkcount: number }>`
  /* position: relative; */
  /* height: 100%; */
  overflow: hidden;
  background-image: linear-gradient(to top, #d5a002 50%, #ececec 50%);
  background-size: ${props => `50% ${props.drinkcount * 8 + 80}%`};
  animation: fillAnimation 0.2s forwards;
  @keyframes fillAnimation {
    0% {
      background-position: 0 0;
    }
    100% {
      background-position: 0 100%;
    }
  }

  aspect-ratio: 1.2/1; /* Added to set the aspect ratio to 1:1 */
  display: flex;
  align-items: center;
  justify-content: center;
`;

export const WhiteModal = {
  content: {
    position: "relative",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "80%",
    padding: "0.5rem 1rem",
    borderRadius: "15px",
    background: "white",
    textAlign: "center",
    fontFamily: "NanumSquareNeo",
  },
  overlay: {
    background: "rgba(0, 0, 0, 0.5)",
    zIndex: "11",
  },
};

export const FlexDiv = styled.div`
  display: flex;
  width: 100%;
  flex-direction: column;
  margin: 1rem auto;
`;

export const ImageInputBtn = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 1.5rem;
  height: 1.5rem;
  border-radius: 10px;
  background-color: var(--c-yellow);
  padding: 0.5rem;
  margin-left: 0.5rem;
`;

export const TitleLabel = styled.label`
  font-family: "JejuGothic";
  font-size: 18px;
  text-align: left;
  margin-bottom: 0.5rem;
`;

export const BtnDiv = styled.div`
  display: flex;
  justify-content: space-around;
  margin: 1.5rem 0;
`;
