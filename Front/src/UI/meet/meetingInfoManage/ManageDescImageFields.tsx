// 모임 설명과 대표 이미지
import ImageInput from "../../components/ImageInput";
import { imgcheck } from "../CheckValid";
import { Title, QuestionDiv, InfoTextArea, ErrorDiv } from "./MeetingInfoManage.styles";
import { MeetingInfoManageState } from "./useMeetingInfoManage";

const ManageDescImageFields = ({
  meetDesc,
  setMeetDesc,
  file,
  setFile,
  newImgSrc,
  setNewImgSrc,
  btnClicked,
}: Pick<
  MeetingInfoManageState,
  | "meetDesc"
  | "setMeetDesc"
  | "file"
  | "setFile"
  | "newImgSrc"
  | "setNewImgSrc"
  | "btnClicked"
>) => {
  return (
    <>
      <QuestionDiv>
        <Title>설명</Title>
        <InfoTextArea
          placeholder="모임에 대한 소개글을 작성해주세요"
          value={meetDesc}
          onChange={e => setMeetDesc(e.target.value)}
        ></InfoTextArea>
      </QuestionDiv>
      <div>
        <ImageInput
          key={newImgSrc}
          getFunc={setFile}
          imgSrc={newImgSrc}
          getImgSrc={setNewImgSrc}
        />
        {!imgcheck(file) && btnClicked && (
          <ErrorDiv>📌이미지만 업로드 가능합니다.(10MB 이하)</ErrorDiv>
        )}
      </div>
    </>
  );
};

export default ManageDescImageFields;
