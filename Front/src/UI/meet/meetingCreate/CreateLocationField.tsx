// 시/도, 구/군 선택
import { positionCheck } from "../CheckValid";
import { Title, QuestionDiv, DropdownInput, ErrorDiv } from "./MeetingCreate.styles";
import { MeetingCreateState } from "./useMeetingCreate";

const CreateLocationField = ({
  sidoRef,
  sidoList,
  gugunList,
  sido,
  setSido,
  gugun,
  setGugun,
  btnClicked,
}: Pick<
  MeetingCreateState,
  | "sidoRef"
  | "sidoList"
  | "gugunList"
  | "sido"
  | "setSido"
  | "gugun"
  | "setGugun"
  | "btnClicked"
>) => {
  return (
    <QuestionDiv>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
        }}
      >
        <Title>위치*</Title>
        <DropdownInput
          onChange={e => {
            const selectedValue = e.target.value;
            const selectedSido = sidoList.find(item => item.sidoName === selectedValue);
            setSido(selectedSido);
          }}
          value={sido.sidoName}
          ref={sidoRef}
        >
          {sidoList.map(siItem => {
            return (
              <option value={siItem.sidoName} key={siItem.sidoCode}>
                {siItem.sidoName}
              </option>
            );
          })}
        </DropdownInput>
        시/도
        <DropdownInput
          onChange={e => {
            const selectedValue = e.target.value;
            const selectedGugun = gugunList.find(item => item.gugunName === selectedValue);
            setGugun(selectedGugun);
          }}
          value={gugun.gugunName}
        >
          {gugunList.map(guItem => {
            return (
              <option value={guItem.gugunName} key={guItem.gugunCode}>
                {guItem.gugunName}
              </option>
            );
          })}
        </DropdownInput>
        구/군
      </div>
      {!positionCheck(sido.sidoCode, gugun.gugunCode) && btnClicked && (
        <ErrorDiv>📌위치는 필수 입력 사항입니다.</ErrorDiv>
      )}
    </QuestionDiv>
  );
};

export default CreateLocationField;
