// 모임 설명과 대표 이미지
import ImageInput from "../../components/ImageInput";
import { imgcheck } from "../CheckValid";
import { Title, QuestionDiv, InfoTextArea, ErrorDiv } from "./MeetingCreate.styles";
import { MeetingCreateState } from "./useMeetingCreate";

const CreateDescImageFields = ({
  meetDesc,
  setMeetDesc,
  imgSrc,
  file,
  setFile,
  btnClicked,
}: Pick<
  MeetingCreateState,
  | "meetDesc"
  | "setMeetDesc"
  | "imgSrc"
  | "file"
  | "setFile"
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
        <ImageInput key={imgSrc} getFunc={setFile} imgSrc={imgSrc} />
        {!imgcheck(file) && btnClicked && (
          <ErrorDiv>📌이미지만 업로드 가능합니다.(10MB 이하)</ErrorDiv>
        )}
      </div>
    </>
  );
};

export default CreateDescImageFields;
