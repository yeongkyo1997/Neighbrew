// ChatRoom 화면의 styled 컴포넌트와 스타일 상수
import styled from "styled-components";
import TextareaAutosize from "react-textarea-autosize";

export const OtherChat = styled.div`
  white-space: pre-wrap;
  position: relative;
  display: inline-block;
  background-color: white;
  border-radius: 12px;
  padding: 10px;
  max-width: 70%;
  word-break: break-all;
  margin-bottom: 5px;
  margin-left: 1px;
  margin-right: 0.5rem;
  font-size: 13px;
  text-align: left;
  font-family: "NanumSquareNeo";
`;

export const MyChat = styled.div`
  white-space: pre-wrap;
  position: relative;
  display: inline-block;
  flex-direction: row;
  background-color: var(--c-yellow);
  border-radius: 12px;
  padding: 14px;
  max-width: 70%;
  word-break: break-all;
  margin-bottom: 5px;
  margin-right: 1px;
  margin-left: 0.5rem;
  font-size: 13px;
  text-align: left;
  font-family: "NanumSquareNeo";
`;

export const ChatOtherBox = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 0.7rem;
`;

export const ChatMyBox = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-right: 0.7rem;
`;

export const ChatMyMsg = styled.div`
  font-size: 0.4rem;
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
`;
export const ChatOtherMsg = styled.div`
  font-size: 0.4rem;
  display: flex;
  align-items: flex-end;
  justify-content: flex-start;
`;

export const ChatTime = styled.div`
  margin-left: 2px;
  margin-bottom: 0.4rem;
  font-size: 0.8rem;
`;

export const ChatUserName = styled.div`
  font-family: "JejuGothic";
  font-size: 0.8rem;
  margin-bottom: 0.4rem;
  margin-top: 1.2rem;
`;

export const ChatNav = styled.div`
  box-sizing: border-box;
  padding: 0.5rem;
  width: 100%;
  height: 3rem;
  background-color: var(--c-lightgray);
  display: flex;
  word-break: break-all;
  font-size: 0.9rem;
  align-items: center;
  justify-content: space-between;
`;

export const RightModal = styled.div<{ ismodal: boolean }>`
  transform: ${(props) =>
    props.ismodal ? "translateX(16%)" : "translateX(100%)"};
  position: fixed;
  width: 95%;
  overflow-x: scroll;
  height: 100vh;
  background-color: white;
  transition: all 0.3s;
  z-index: 16;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 0rem 2rem;
`;
// const ImgDiv = styled.div`
//   width: 15%;
//   height: 100%;
//   overflow: hidden;
//   aspect-ratio: 1/1;
//   border-radius: 50%;
//   float: left;
//   margin-right: 2rem;
// `;
export const ImgDiv = styled.div`
  /* width: 15%;
  height: 100%; */
  overflow: hidden;
  /* inline-size: 25ch; */
  aspect-ratio: 1/1;
  border-radius: 50%;
  float: left;
  margin-right: 2rem;
  min-width: 40px;
  min-height: 40px;
  max-width: 40px;
  max-height: 40px;
`;

export const Img = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

export const UserDiv = styled.div`
  display: flex;
  margin-bottom: 1rem;
  font-size: 16px;
  @font-face {
    font-family: "SUITE-Regular";
    src: url("https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2304-2@1.0/SUITE-Regular.woff2")
      format("woff2");
    font-style: normal;
  }
  font-family: "SUITE-Regular";
  align-items: center;
  width: 75%;
  /* white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; */
`;
export const UserNameP = styled.p`
  width: 80%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
`;

export const InputDiv = styled.div`
  display: flex;
  background-color: #fff;
  width: 100%;
  border-top: 0.5px solid #dfdfdf;
  max-height: 100px;
  align-items: center;
  justify-content: space-around;
`;

export const BackDrop = styled.div<{ ismodal: boolean }>`
  display: ${(props) => (props.ismodal ? "block" : "none")};
  transition: all 1s;
  width: 100%;
  max-width: 430px;
  height: 10000px;
  position: fixed;
  z-index: 15;
  background-color: #322d29;
`;
export const StyleAutoTextArea = styled(TextareaAutosize)`
  display: flex;
  flex-basis: 90%;
  border: 0.5px solid #dfdfdf;
  background-color: #eeeeee;
  border-radius: 5px;
  padding: 0.3rem;
  overflow-y: auto;
  resize: none;
  margin: 0.5rem 0 0.5rem 0.5rem;
  padding: 0.3rem;
  outline: none;
  // 글을 아래에 배치
  align-self: flex-end;
  font-size: 1rem;

  &:focus {
    border: none;
  }
`;
export const SendBtnDiv = styled.div`
  background-color: #ffffff;
  border-radius: 0 5px 5px 0;
  border: none;
  flex-basis: 10%;
`;
export const SendImg = styled.img`
  width: 23px;
  height: 23px;
`;
