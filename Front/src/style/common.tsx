import styled, { css } from "styled-components";

export const Input = styled.input`
  width: 80%;
  height: 40px;
  border: 1px solid #e5e5e5;
  border-radius: 5px;
  padding: 0 10px;
  margin: 2rem 1rem 2rem 1rem;
`;

export const Alert = styled.div`
  width: 80%;
  /* height: 2rem; */
  border: 2px solid red;
  border-radius: 3px;
  margin: 0 auto;
  text-align: center;
  padding: 3px;
`;

//모달 안에 div
export const ModalInner = styled.div`
  white-space: pre-line;
  overflow: auto;
`;

//인풋 텍스트 타입 긴 버전
export const InputText = styled.input`
  width: 100%;
  background: white;
  text-align: left;
  padding: 2% 0;
  border: none;
  border-bottom: 1px solid var(--c-gray);
  font-family: "NanumSquareNeo";
  font-size: 16px;
  outline: none;
  &::placeholder {
    color: var(--c-gray);
  }
`;

//인풋 date와 time 타입의 스타일(상속용)
const DateAndTimeInputStyle = css`
  color: var(--c-black);
  width: 45%;
  font-family: "NanumSquareNeo";
  text-align: right;
  border: none;
  border-bottom: 1px solid var(--c-gray);
  background: white;
  font-size: 16px;
  outline: none;
`;

//날짜 입력
export const DateInput = styled.input.attrs({ type: "date" })`
  ${DateAndTimeInputStyle}
`;

//시간 입력
export const TimeInput = styled.input.attrs({ type: "time" })`
  ${DateAndTimeInputStyle}
`;

