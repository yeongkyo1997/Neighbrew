// DrinkpostReviewDetail 화면의 styled 컴포넌트와 스타일 상수
import TextareaAutosize from "react-textarea-autosize";
import styled from "styled-components";

export const StyleAutoTextArea = styled(TextareaAutosize)`
  display: flex;
  flex-basis: 90%;
  border: 0.5px solid #dfdfdf;
  background-color: #eeeeee;
  border-radius: 5px;
  margin: 0.5rem 0 0.5rem 0.5rem;
  padding: 0.3rem;
  overflow-y: auto;
  outline: none;

  // 글을 아래에 배치
  align-self: flex-end;
  font-size: 1rem;

  &:focus {
    border: none;
  }
`;
export const LikeAndComment = styled.div`
  margin: 0.5rem 0.5rem 0 0.5rem;
  display: flex;
  justify-content: left;
  width: 40%;
  margin-top: 1.5vh;
  font-size: 20px;
`;

export const DescriptionP = styled.p`
  white-space: pre-wrap;
  word-spacing: 0.2rem;
  line-height: 150%;
  font-family: "NanumSquareNeo";
  /* font-family: "Noto Sans KR"; */
  font-size: 1rem;
  margin: 0.5rem;

  text-align: start;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
  max-height: 150px;
  transition: max-height 0.3s ease-in-out;

  // 늘리기
  &.show {
    display: block;
    max-height: 1000px;
    overflow: auto;
    -webkit-line-clamp: unset;
  }

  // 줄이기
  &.hide {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    overflow: hidden;
    max-height: 150px; /* 여기에 max-height 추가 */
  }
`;

export const MoreButton = styled.div`
  border: none;
  display: inline-flex; /* flex를 유지하면서 inline 형태로 만들어 줍니다. */
  justify-content: flex-end;
  padding: 0px 0px;
  color: var(--c-gray);
  border-radius: 0.5rem;
  margin-top: 20px;
  margin-bottom: 0.625rem;

  &:hover {
    background-color: #e0e0e0;
  }

  &.hide {
    display: none;
  }
`;

export const WholeDiv = styled.div``;

export const ImageDiv = styled.div`
  background-color: var(--c-lightgray);
  background-repeat: no-repeat;
  background-size: cover;
  width: 100%;
`;

export const Usercard = styled.div`
  display: flex;
  align-items: center;
  margin: 0.5rem;
  justify-content: space-between;
`;

export const FollowDiv = styled.div`
  width: 5rem;
  height: 2rem;
  border-radius: 20px;
  font-family: JejuGothic;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const CommentBox = styled.div`
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background-color: #fff;
  display: flex;
  flex-direction: row;
  width: 100%;
  border-top: 0.5px solid #dfdfdf;
`;

export const CommentButton = styled.button`
  background-color: var(--c-blue);
  border: none;
  border-radius: 5px;
  flex-basis: 10%;
`;

export const SendImg = styled.img`
  width: 23px;
  height: 23px;
`;

export const SubReviewList = styled.div`
  padding-bottom: 4.5rem;
  margin: 0.5rem;
`;

export const LikeAndCommentDiv = styled.div`
  display: flex;
  flex-direction: row;
  margin-right: 4vw;
`;

export const InfoBox = styled.div`
  display: flex;
  justify-content: space-between;
`;

export const ThreeDotModal = {
  content: {
    top: "90%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "90%",
    height: "16%",
    borderRadius: "24px 24px 0px 0px",
    backgroundColor: "#ffffff",
    fontFamily: "NanumSquareNeo",
    fontSize: "1.5rem",
    color: "black",
    transition: "top 2s ease-in-out",
  },
  overlay: {
    background: "rgba(0, 0, 0, 0.5)",
    zIndex: "1001",
  },
};

export const ModalBtnDiv = styled.div`
  position: fixed;
  bottom: 0;
  left: 10%;
  margin-bottom: 1rem;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
`;

export const ModalBtn = styled.div`
  width: 5rem;
  padding: 0.5rem;
  margin: 0 0.5rem;
  border-radius: 5px;
  background: var(--c-yellow);
`;

export const ModalIcon = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 1.5rem;
  margin-right: 1rem;
`;
