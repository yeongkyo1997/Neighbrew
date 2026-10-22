// 모임 이름과 마실 술 선택
import MeetingDrinkSearch from "../MeetingDrinkSearch";
import { InputText } from "../../../style/common";
import { titleCheck } from "../CheckValid";
import { Title, QuestionDiv, ErrorDiv, SubText } from "./MeetingCreate.styles";
import { MeetingCreateState } from "./useMeetingCreate";

const CreateBasicFields = ({
  titleRef,
  meetTitle,
  setMeetTitle,
  selectedCategory,
  setSelectedCategory,
  selectedDrink,
  setSelectedDrink,
  btnClicked,
}: Pick<
  MeetingCreateState,
  | "titleRef"
  | "meetTitle"
  | "setMeetTitle"
  | "selectedCategory"
  | "setSelectedCategory"
  | "selectedDrink"
  | "setSelectedDrink"
  | "btnClicked"
>) => {
  return (
    <>
      <QuestionDiv>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <Title>모임의 이름*</Title>
          <SubText>* 표시: 필수입력</SubText>
        </div>
        <InputText
          ref={titleRef}
          placeholder="모임의 이름을 입력해주세요"
          value={meetTitle}
          onChange={e => setMeetTitle(e.target.value)}
        />
        {!titleCheck(meetTitle.trim()) && btnClicked && (
          <ErrorDiv>📌모임 이름은 필수로 입력해야합니다.(30자 이내)</ErrorDiv>
        )}
      </QuestionDiv>
      <QuestionDiv>
        <MeetingDrinkSearch
          tagId={selectedCategory}
          setTagIdFunc={setSelectedCategory}
          drink={selectedDrink}
          setDrinkFunc={setSelectedDrink}
          btnClicked={btnClicked}
          isModify={false}
        />
      </QuestionDiv>
    </>
  );
};

export default CreateBasicFields;
