// 프로필 수정 모달 (이미지, 닉네임, 소개, 생년월일, 로그아웃)
import Modal from "react-modal";
import defaultImg from "../../../assets/defaultImg.png";
import { InputText, DateInput } from "../../../style/common";
import { MyPageState } from "./useMyPage";
import { BtnDiv, Button, FlexDiv, ImageArea, ImageInputBtn, ImgInput, QuestionDiv, Title, TitleLabel, WhiteModal } from "./MyPage.styles";

const ProfileEditModal = ({
  userData,
  deleteModalOn,
  setDeleteModalOn,
  imgFile,
  imgRef,
  saveImgFile,
  changeUserProfiletoDefault,
  nickname,
  nicknameHandler,
  intro,
  introHandler,
  birth,
  birthHandler,
  adultCutoffYear,
  changeUserInfo,
  navigate,
}: Pick<
  MyPageState,
  | "userData"
  | "deleteModalOn"
  | "setDeleteModalOn"
  | "imgFile"
  | "imgRef"
  | "saveImgFile"
  | "changeUserProfiletoDefault"
  | "nickname"
  | "nicknameHandler"
  | "intro"
  | "introHandler"
  | "birth"
  | "birthHandler"
  | "adultCutoffYear"
  | "changeUserInfo"
  | "navigate"
>) => {
  return (
    <Modal
      closeTimeoutMS={1000}
      isOpen={deleteModalOn}
      onRequestClose={() => setDeleteModalOn(false)}
      style={WhiteModal}
      ariaHideApp={false}
    >
      <div>
        <QuestionDiv style={{ textAlign: "left", marginBottom: "2rem" }}>
          <Title>프로필 이미지</Title>
          <div style={{ display: "flex", justifyContent: "center" }}>
            {imgFile == null ? (
              userData.profile == "no image" ? (
                <ImageArea src={defaultImg}></ImageArea>
              ) : (
                <ImageArea src={userData.profile}></ImageArea>
              )
            ) : (
              <ImageArea src={imgFile}></ImageArea>
            )}
          </div>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              marginTop: "1rem",
            }}
          >
            <ImgInput>
              <label htmlFor="img_file">
                <ImageInputBtn>
                  <img src="/src/assets/imagePlusIcon.svg" width="90%" />
                </ImageInputBtn>
              </label>
              <input
                type="file"
                id="img_file"
                accept="image/jpg, image/png, image/jpeg"
                onChange={saveImgFile}
                ref={imgRef}
              />
            </ImgInput>
            <Button
              onClick={() => {
                changeUserProfiletoDefault();
              }}
              style={{
                backgroundColor: "var(--c-lightgray)",
                borderRadius: "0.5rem",
                width: "40%",
                marginLeft: "1rem",
              }}
            >
              기본 이미지로 변경
            </Button>
          </div>
        </QuestionDiv>
        <FlexDiv>
          <TitleLabel htmlFor="nickname">닉네임</TitleLabel>
          <InputText type="text" id="nickname" value={nickname} onInput={nicknameHandler} />
        </FlexDiv>
        <FlexDiv>
          <TitleLabel htmlFor="intro">한줄 설명</TitleLabel>
          <InputText
            type="text"
            id="intro"
            value={intro}
            onInput={introHandler}
            placeholder="한줄 설명을 입력해주세요"
          />
        </FlexDiv>
        <FlexDiv style={{ flexDirection: "row", justifyContent: "space-between" }}>
          <TitleLabel htmlFor="date">생년월일</TitleLabel>
          <DateInput
            type="date"
            id="date"
            value={birth}
            onInput={birthHandler}
            max={`${adultCutoffYear - 1}-12-31`}
            style={{ width: "70%", marginRight: "1rem" }}
          />
        </FlexDiv>
        <BtnDiv>
          <Button
            onClick={() => {
              changeUserInfo();
              setDeleteModalOn(false);
            }}
            style={{
              backgroundColor: "var(--c-yellow)",
              color: "var(--c-black)",
              borderRadius: "8px",
              marginBottom: "1rem",
            }}
          >
            유저 정보 변경
          </Button>
          <Button
            onClick={() => {
              setDeleteModalOn(false);
              localStorage.removeItem("token");
              localStorage.removeItem("myId");
              localStorage.removeItem("refreshToken");
              localStorage.removeItem("chooseMenu");
              navigate("/");
            }}
            style={{
              backgroundColor: "var(--c-lightgray)",
              color: "var(--c-black)",
              borderRadius: "8px",
            }}
          >
            로그아웃
          </Button>
        </BtnDiv>
      </div>
    </Modal>
  );
};

export default ProfileEditModal;
