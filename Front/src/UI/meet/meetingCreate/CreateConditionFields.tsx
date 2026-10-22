// 최대 인원, 간수치 제한, 나이 제한
import { participantsCheck, liverLimitCheck, ageCheck } from "../CheckValid";
import { Tooltip } from "react-tooltip";
import {
  Title,
  SubTitle,
  QuestionDiv,
  InputShort,
  LimitDiv,
  ErrorDiv,
  TooltipBtn,
} from "./MeetingCreate.styles";
import { MeetingCreateState } from "./useMeetingCreate";

const CreateConditionFields = ({
  maxPRef,
  liverRef,
  minAgeRef,
  maxParticipants,
  setMaxParticipants,
  liverLimit,
  setLiverLimit,
  minAge,
  setMinAge,
  maxAge,
  setMaxAge,
  btnClicked,
}: Pick<
  MeetingCreateState,
  | "maxPRef"
  | "liverRef"
  | "minAgeRef"
  | "maxParticipants"
  | "setMaxParticipants"
  | "liverLimit"
  | "setLiverLimit"
  | "minAge"
  | "setMinAge"
  | "maxAge"
  | "setMaxAge"
  | "btnClicked"
>) => {
  return (
    <QuestionDiv style={{ fontFamily: "NanumSquareNeo", fontSize: "16px" }}>
      <Title>조건</Title>
      <div style={{ marginBottom: "0.7rem" }}>
        <LimitDiv>
          <SubTitle>최대 인원*</SubTitle>
          <InputShort
            ref={maxPRef}
            value={maxParticipants}
            onChange={e =>
              setMaxParticipants(
                !Number.isNaN(parseInt(e.target.value)) ? parseInt(e.target.value) : 0
              )
            }
          />
          명
        </LimitDiv>
        {!participantsCheck(maxParticipants) && btnClicked && (
          <ErrorDiv>📌필수 입력사항입니다.(8명 이내)</ErrorDiv>
        )}
      </div>
      <div style={{ marginBottom: "0.7rem" }}>
        <LimitDiv>
          <SubTitle>간수치</SubTitle>
          <InputShort
            ref={liverRef}
            type="number"
            placeholder="40"
            value={liverLimit > 0 ? liverLimit : ""}
            onChange={e => setLiverLimit(parseInt(e.target.value))}
          />
          IU/L이상
          <TooltipBtn data-tooltip-id="liver-tooltip">❓</TooltipBtn>
        </LimitDiv>
        {!liverLimitCheck(liverLimit) && btnClicked && <ErrorDiv>📌100IU/L이하</ErrorDiv>}
        <Tooltip
          id="liver-tooltip"
          style={{
            backgroundColor: "var(--c-pink)",
            color: "black",
            fontSize: "12px",
            width: "10rem",
            textAlign: "justify",
            wordBreak: "break-word",
          }}
        >
          <div style={{ fontWeight: "700", marginTop: "0.3rem" }}>간수치?</div>
          <div style={{ marginTop: "0.3rem" }}>
            네이브루 사용자로부터 받은 칭찬, 후기, 비매너 평가 등을 종합해서 만든 매너
            지표입니다.
          </div>
          <div style={{ margin: "0.3rem 0" }}>
            간수치는 40 IU/L에서 시작해서 0~100 IU/L 사이의 값을 가집니다.
          </div>
        </Tooltip>
      </div>
      <div style={{ marginBottom: "0.7rem" }}>
        <LimitDiv>
          <SubTitle>나이</SubTitle>
          <InputShort
            ref={minAgeRef}
            placeholder="20"
            value={minAge > 0 ? minAge : ""}
            onChange={e => setMinAge(parseInt(e.target.value))}
          />
          세 이상
          <InputShort
            placeholder="200"
            value={maxAge > 0 ? maxAge : ""}
            onChange={e => setMaxAge(parseInt(e.target.value))}
          />
          세 미만
        </LimitDiv>
        {!ageCheck(minAge, maxAge) && btnClicked && <ErrorDiv>📌20세 ~ 200세 사이</ErrorDiv>}
      </div>
    </QuestionDiv>
  );
};

export default CreateConditionFields;
