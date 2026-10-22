// MeetingDetail 화면의 styled 컴포넌트와 모달 스타일
import styled from "styled-components";

export const MeetThumbnail = styled.div<{ $bgImgSrc: string }>`
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)),
    url(${props => props.$bgImgSrc}) no-repeat center;
  background-size: cover;
  width: 100%;
  min-height: 30vh;
  color: white;
`;

export const DetailHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
`;

export const Tag = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--c-yellow);
  padding: 0.5rem 1rem;
  font-family: "NanumSquareNeoBold";
  font-size: 12px;
  border-radius: 20px;
  color: var(--c-black);
  &::before {
    content: "";
    display: inline-block;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: white;
    margin-right: 2px;
  }
  span {
    display: flex;
    justify-content: center;
    min-width: 30px;
  }
`;

export const ChatBtn = styled.div`
  padding: 0.5rem 1rem;
  background-color: var(--c-yellow);
  color: black;
  font-family: "NanumSquareNeo";
  font-size: 12px;
  border-radius: 5px;
`;

export const Title = styled.div`
  font-family: "JejuGothic";
  font-size: 28px;
  margin: 0 auto 0.5rem auto;
  width: 20rem;
`;

export const HostInfo = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: "NanumSquareNeo";
  font-size: 16px;
  margin: 0.5rem 0;
`;

export const UserProfileImg = styled.div<{ src: string }>`
  background: url(${props => props.src}) no-repeat center;
  background-size: cover;
  width: 2rem;
  padding-bottom: 2rem;
  border-radius: 100px;
  margin-right: 0.2rem;
`;

export const MeetDetailDiv = styled.div`
  background: var(--c-lightgray);
  min-height: 70vh;
  border-radius: 30px 30px 0px 0px;
  position: relative;
  z-index: 1;
  top: -2rem;
  padding: 4.5rem 1rem;
`;

export const MeetPosDateDiv = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-around;
  position: absolute;
  top: -2rem;
  left: 50%;
  transform: translate(-50%, 0%);
  z-index: 2;
  width: 17rem;
  height: 4rem;
  background: white;
  border-radius: 20px;
  box-shadow: 0px 4px 10px 0px rgba(0, 0, 0, 0.07);
  padding: 1rem;
  font-family: "NanumSquareNeo";
  font-size: 16px;
`;

export const SubTitle = styled.div`
  font-family: "JejuGothic";
  font-size: 20px;
  text-align: left;
  margin-top: 1.5rem;
`;

export const Description = styled.div`
  color: var(--c-black);
  font-family: "NanumSquareNeo";
  font-size: 16px;
  text-align: justify;
  line-height: 1.6rem;
  margin-top: 0.5rem;
  margin-bottom: 2rem;
  white-space: pre-line;
  word-break: break-all;
`;

export const ManageModal = {
  content: {
    top: "90%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "90%",
    height: "16%",
    borderRadius: "24px 24px 0px 0px",
    backgroundColor: "#ffffff",
    fontFamily: "NanumSquareNeo",
    fontSize: "16px",
    color: "black",
  },
  overlay: {
    background: "rgba(0, 0, 0, 0.5)",
    zIndex: "1001",
  },
};

export const ModalIcon = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 1.5rem;
  margin-right: 1rem;
`;

export const BtnSmall = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  width: 2rem;
  min-width: 2rem;
  height: 2rem;
  border-radius: 5px;
  margin: 0 0.2rem;
`;

export const OKBtn = styled(BtnSmall)`
  background: var(--c-yellow);
`;

export const NoBtn = styled(BtnSmall)`
  background: #f28f79;
`;
