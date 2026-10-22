// MeetingCreate 화면의 styled 컴포넌트와 스타일 상수
import styled from "styled-components";
import { InputText } from "../../../style/common";

export const Title = styled.div`
  font-family: "JejuGothic";
  font-size: 24px;
  text-align: left;
  margin-top: 1rem;
  margin-bottom: 0.5rem;
`;

export const SubTitle = styled.div`
  font-family: "NanumSquareNeo";
  font-size: 16px;
  text-align: left;
`;

export const QuestionDiv = styled.div`
  margin-top: 1.5rem;
`;

export const InputShort = styled(InputText)`
  width: 4rem;
  padding: 1% 3%;
  text-align: right;
`;

export const DropdownInput = styled.select`
  width: 5rem;
  background: white;
  text-align: right;
  padding: 1% 3%;
  border: none;
  border-bottom: 1px solid var(--c-gray);
  font-family: "NanumSquareNeo";
  font-size: 16px;
  outline: none;
  -webkit-appearance: none; /* 화살표 없애기 for chrome*/
  -moz-appearance: none; /* 화살표 없애기 for firefox*/
  appearance: none; /* 화살표 없애기 공통*/
`;

export const LimitDiv = styled.div`
  display: flex;
  align-items: center;
`;

export const InfoTextArea = styled.textarea`
  width: 90%;
  height: 10rem;
  margin: 0 auto;
  padding: 1rem;
  border: 1px solid var(--c-gray);
  border-radius: 15px;
  outline: none;
  font-family: "NanumSquareNeo";
  font-size: 14px;
  resize: none;
`;

export const ErrorDiv = styled.div`
  color: red;
  text-align: left;
  font-family: "NanumSquareNeo";
  font-size: 14px;
  padding: 0.5rem;
`;

export const SubText = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: "NanumSquareNeoBold";
  font-size: 13px;
  background-color: var(--c-lightgray);
  width: 7rem;
  border-radius: 5px;
  margin-top: 1rem;
`;

export const TooltipBtn = styled.div`
  padding: 0 0.5rem;
`;
