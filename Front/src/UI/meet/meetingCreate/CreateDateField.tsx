// 모임 날짜와 시간
import { DateInput, TimeInput } from "../../../style/common";
import { localDate } from "../DateTimeCommon";
import { timeCheck } from "../CheckValid";
import { Title, QuestionDiv, ErrorDiv } from "./MeetingCreate.styles";
import { MeetingCreateState } from "./useMeetingCreate";

const CreateDateField = ({
  dateRef,
  date,
  setDate,
  time,
  setTime,
  btnClicked,
}: Pick<
  MeetingCreateState,
  | "dateRef"
  | "date"
  | "setDate"
  | "time"
  | "setTime"
  | "btnClicked"
>) => {
  return (
    <QuestionDiv>
      <Title>시간*</Title>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <DateInput
          ref={dateRef}
          value={date}
          onChange={e => setDate(e.target.value)}
          min={localDate().toString()}
          required
        />
        <TimeInput type="time" value={time} onChange={e => setTime(e.target.value)} required />
      </div>
      {!timeCheck(date, time) && btnClicked && (
        <ErrorDiv>
          <div>📌날짜와 시간은 필수 입력 사항입니다.</div>
          <div>(현재 날짜와 시간 이후로만 입력 가능)</div>
        </ErrorDiv>
      )}
    </QuestionDiv>
  );
};

export default CreateDateField;
