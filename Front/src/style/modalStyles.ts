// react-modal의 style prop에 넘기는 모달 스타일 객체
// (style/common.tsx는 컴포넌트만 내보내야 Fast Refresh가 동작하므로 따로 둔다)

//모달창 디자인
export const WhiteModal = {
  content: {
    position: "relative",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "15rem",
    minHeight: "5rem",
    wordBreak: "break-all",
    height: "auto",
    padding: "0.5rem 1rem",
    borderRadius: "15px",
    background: "white",
    overflow: "auto",
    textAlign: "center",
    fontFamily: "NanumSquareNeo",
    WebkitOverflowScrolling:
      "touch" /* overflow가 일어날 경우 모바일 기기에서 부드러운 가속이 적용된 스크롤이 되도록 해주는 속성 */,
  },
  overlay: {
    background: "rgba(0, 0, 0, 0.5)",
    zIndex: "11",
  },
};

export const DeleteModal = {
  content: {
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "80%",
    height: "16%",
    padding: "0.5rem 1rem",
    borderRadius: "15px",
    background: "#ffffff",
    textAlign: "center",
    fontFamily: "SeoulNamsan",
    color: "#000000",
  },
  overlay: {
    background: "rgba(0, 0, 0, 0.5)",
    zIndex: "11",
  },
};

export const ThreeDotModal = {
  content: {
    top: "90%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "90%",
    height: "16%",
    borderRadius: "24px 24px 0px 0px",
    backgroundColor: "#ffffff",
    fontFamily: "SeoulNamsan",
    fontSize: "1.5rem",
    color: "black",
    transition: "top 2s ease-in-out",
  },
  overlay: {
    background: "rgba(0, 0, 0, 0.5)",
    zIndex: "1001",
  },
};

export const ShortThreeDotModal = {
  content: {
    top: "94%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "90%",
    height: "8%",
    borderRadius: "24px 24px 0px 0px",
    backgroundColor: "#ffffff",
    fontFamily: "SeoulNamsan",
    fontSize: "1.5rem",
    color: "black",
    transition: "top 2s ease-in-out",
  },
  overlay: {
    background: "rgba(0, 0, 0, 0.5)",
    zIndex: "1001",
  },
};
