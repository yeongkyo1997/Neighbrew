/*
[MeetingInfoManage.tsx]
모임 정보 관리 페이지
모임 이름, 주종 카테고리, 술 검색, 위치, 시간, 조건, 설명, 이미지 첨부 가능
*/
import { useState, useRef, useEffect } from "react";
import { useParams } from "react-router-dom";
import NavbarSimple from "../navbar/NavbarSimple";
import styled, { css } from "styled-components";
import DrinkCategory from "../drinkCategory/DrinkCategory";
import SearchBox from "../components/SearchBox";
import FooterBigBtn from "../footer/FooterBigBtn";
import OneLineListItem from "../components/OneLineListItem";
import ListInfoItem from "../components/ListInfoItem";
import ImageInput from "../components/ImageInput";
import { MeetDetail } from "../../Type/types";
import autoAnimate from "@formkit/auto-animate";
import { callApi } from "../../utils/api";
import { Drink } from "../../Type/types";

const Title = styled.div`
  font-family: "JejuGothic";
  font-size: 20px;
  text-align: left;
  margin-bottom: 0.5rem;
`;

const SubTitle = styled.div`
  font-family: SeoulNamsan;
  font-size: 14px;
  text-align: left;
`;

const QuestionDiv = styled.div`
  margin-top: 1.5rem;
`;

const ReselectBtn = styled.div`
  background: var(--c-lightgray);
  border-radius: 10px;
  width: 3rem;
  font-family: "SeoulNamsan";
  font-size: 15px;
  padding: 0.5rem;
  margin: 0.5rem 0 0 auto;
`;

const Input = styled.input`
  width: 100%;
  background: white;
  text-align: left;
  padding: 2% 0;
  border: none;
  border-bottom: 1px solid var(--c-gray);
  font-family: "SeoulNamsan";
  font-size: 14px;
  outline: none;
  &::placeholder {
    color: var(--c-gray);
  }
`;

const InputShort = styled(Input)`
  width: 4rem;
  padding: 1% 3%;
  text-align: right;
`;

const DropdownInput = styled.select`
  width: 4rem;
  background: white;
  text-align: right;
  padding: 1% 3%;
  border: none;
  border-bottom: 1px solid var(--c-gray);
  font-family: "SeoulNamsan";
  outline: none;
  -webkit-appearance: none; /* 화살표 없애기 for chrome*/
  -moz-appearance: none; /* 화살표 없애기 for firefox*/
  appearance: none; /* 화살표 없애기 공통*/
`;

const SearchResultDiv = styled.div`
  border-radius: 15px;
  border: 1px solid var(--c-gray);
  height: 12rem;
  margin-top: 0.5rem;
  display: flex;
  flex-direction: column;
`;

const AddBtn = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-around;
  background: var(--c-yellow);
  width: 5rem;
  margin: auto auto;
  padding: 2% 5%;
  border-radius: 20px;
  font-family: "SeoulNamsan";
  font-size: 7px;
`;

const DateAndTimeInputStyle = css`
  color: var(--c-black);
  width: 45%;
  font-family: "SeoulNamsan";
  text-align: right;
  border: none;
  border-bottom: 1px solid var(--c-gray);
  background: white;
  font-size: 14px;
  outline: none;
`;

const DateInput = styled.input.attrs({ type: "date" })`
  ${DateAndTimeInputStyle}
`;

const TimeInput = styled.input.attrs({ type: "time" })`
  ${DateAndTimeInputStyle}
`;

const CateDiv = styled.div`
  height: 10rem;
  div {
    margin: 0;
  }
  .first,
  .second {
    display: flex;
    justify-content: space-around;
    margin-top: 0.5rem;
  }
`;

const InfoTextArea = styled.textarea`
  width: 90%;
  height: 10rem;
  margin: 0 auto;
  padding: 1rem;
  border: 1px solid var(--c-gray);
  border-radius: 15px;
  outline: none;
  font-family: "SeoulNamsan";
  font-size: 14px;
  resize: none;
`;

//TODO: 초기값을 context api로 만들던가 해서 공유하기
const initialData: MeetDetail = {
  meetDto: {
    meetId: 0,
    meetName: "",
    description: "",
    hostId: 0,
    nowParticipants: 0,
    maxParticipants: 0,
    meetDate: "0000-01-01T00:00:00",
    tagId: 0,
    sido: "-",
    gugun: "-",
    dong: "-",
    drink: {
      degree: 0,
      description: "",
      drinkId: 0,
      image: "",
      name: "",
      tagId: 0,
    },
    imgSrc: "",
  },
  users: [],
  statuses: [],
};

const initialDrinkData: Drink = {
  degree: 0,
  description: "",
  drinkId: 0,
  image: "",
  name: "",
  tagId: 0,
};

const MeetingInfoManage = () => {
  //미팅 기존 정보
  const [meetData, setMeetData] = useState<MeetDetail>(initialData);
  const { meetId } = useParams(); //meetId는 라우터 링크에서 따오기

  //폼에 들어갈 state들
  const [meetTitle, setMeetTitle] = useState("");
  const [selectedCategory, setSelectedCategory] = useState(0); //주종카테고리
  const [selectedDrink, setSelectedDrink] = useState<Drink>(initialDrinkData); //주류
  const [sido, setSido] = useState(""); //시도
  const [gugun, setGugun] = useState(""); //구군
  const [dong, setDong] = useState(""); //동
  const [date, setDate] = useState(""); //날짜
  const [time, setTime] = useState(""); //시간
  const [maxParticipants, setMaxParticipants] = useState(8); //최대인원
  const [liverLimit, setLiverLimit] = useState(0); //간수치 제한
  const [minAge, setMinAge] = useState(0); //최소나이
  const [maxAge, setMaxAge] = useState(0); //최대나이
  const [meetDesc, setMeetDesc] = useState(""); //모임 소개
  const [imgSrc, setImgSrc] = useState(""); //이미지

  //검색 관련 state
  const [inputText, setInputText] = useState(""); //검색창에 입력된 텍스트
  const [searchResultList, setSearchResultList] = useState<Drink[]>([]); //주류 검색 결과 리스트

  //api 호출, 기존 모임의 정보 저장
  useEffect(() => {
    const promise = callApi("get", `api/meet/${meetId}`);
    promise.then((res) => {
      setMeetData(res.data);
    });
  }, [meetId]);

  //받아온 모임 정보로 state 초기값 설정
  useEffect(() => {
    setMeetTitle(meetData.meetDto.meetName);
    setSelectedCategory(meetData.meetDto.tagId); //주종카테고리
    setSelectedDrink(meetData.meetDto.drink); //주류아이디
    setSido(meetData.meetDto.sido); //시도
    setGugun(meetData.meetDto.gugun); //구군
    setDong(meetData.meetDto.dong); //동
    setDate(formateDate(meetData.meetDto.meetDate)); //날짜
    setTime(formateTime(meetData.meetDto.meetDate)); //시간
    setMaxParticipants(meetData.meetDto.maxParticipants); //최대인원
    setLiverLimit(meetData.meetDto.minLiverPoint); //간수치 제한
    setMinAge(meetData.meetDto.minAge); //최소 나이
    setMaxAge(meetData.meetDto.maxAge); //최대 나이
    setMeetDesc(meetData.meetDto.description); //모임 소개
    setImgSrc(meetData.meetDto.imgSrc); //이미지경로
  }, [meetData]);

  //inputText로 술장 검색 api
  useEffect(() => {
    const promise = callApi("get", `api/drink/search?name=${inputText}`);
    promise.then((res) => {
      setSearchResultList(res.data.content);
    });
  }, [inputText]);

  //주종 카테고리 선택
  const getDrinkCategory = (tagId: number) => {
    setSelectedCategory(tagId);
  };

  //검색 후 선택한 주류 정보, 즉 모임에 설정할 술 정보받아오기
  const getDrink = (drink: Drink) => {
    setSelectedDrink(drink);
    setIsSearchFocused(false);
  };

  //검색 결과 창 애니메이션 용
  const [isSearchFocused, setIsSearchFocused] = useState(false);
  const parent = useRef(null);
  useEffect(() => {
    parent.current && autoAnimate(parent.current);
  }, [parent]);

  //날짜와 변환 함수
  function formateDate(dateData: string) {
    const date = new Date(dateData);
    const year = date.getFullYear();
    const month =
      date.getMonth() > 8 ? date.getMonth() + 1 : `0${date.getMonth() + 1}`;
    const day = date.getDate();

    return `${year}-${month}-${day}`;
  }

  //시간 변환 함수
  function formateTime(dateData: string) {
    const date = new Date(dateData);
    const hour = date.getHours() < 10 ? `0${date.getHours()}` : date.getHours();
    const minute = date.getMinutes();

    return `${hour}:${minute}`;
  }

  //태그ID를 태그 이름으로 변환
  function getTagName(tagId: number) {
    const tag = [
      { tagId: 0, tagName: "전체" },
      { tagId: 1, tagName: "양주" },
      { tagId: 2, tagName: "전통주" },
      { tagId: 3, tagName: "전체" },
      { tagId: 4, tagName: "사케" },
      { tagId: 5, tagName: "와인" },
      { tagId: 6, tagName: "수제맥주" },
      { tagId: 7, tagName: "소주/맥주" },
    ];
    return tag[tagId].tagName;
  }

  return (
    <div>
      <header>
        <NavbarSimple title="모임 관리" />
      </header>
      <div style={{ padding: "0 1.5rem", marginBottom: "7rem" }}>
        <QuestionDiv>
          <Title>모임의 이름</Title>
          <Input
            placeholder="모임의 이름을 입력해주세요"
            value={meetTitle}
            onChange={(e) => setMeetTitle(e.target.value)}
          />
        </QuestionDiv>
        <QuestionDiv>
          <Title>우리가 마실 것은</Title>
          <SubTitle>카테고리를 선택해주세요</SubTitle>
          <CateDiv>
            <DrinkCategory getFunc={getDrinkCategory} />
          </CateDiv>

          <div ref={parent}>
            {selectedDrink.drinkId === 0 && (
              <div>
                <SubTitle style={{ marginBottom: "0.3rem" }}>
                  정확한 술의 이름을 검색할 수 있어요
                </SubTitle>
                <div onFocus={() => setIsSearchFocused(true)}>
                  <SearchBox
                    placeholder=""
                    value={inputText}
                    changeFunc={(inputTxt: string) => {
                      setInputText(inputTxt);
                    }}
                  />
                </div>
                {isSearchFocused && (
                  <SearchResultDiv>
                    <div
                      style={{
                        overflow: "auto",
                        height: "100%",
                        flexGrow: "1",
                      }}
                    >
                      {searchResultList.map((res) => {
                        return (
                          <div onClick={() => getDrink(res)} key={res.drinkId}>
                            <OneLineListItem
                              content={res.name}
                              tag={getTagName(res.tagId)}
                            ></OneLineListItem>
                          </div>
                        );
                      })}
                    </div>
                    <div
                      style={{
                        position: "sticky",
                        bottom: "0",
                        height: "3rem",
                        zIndex: "3",
                      }}
                    >
                      <AddBtn>
                        <img src="/src/assets/plusButton.svg" width="13rem" />
                        <div>문서 추가하기</div>
                      </AddBtn>
                    </div>
                  </SearchResultDiv>
                )}
              </div>
            )}
            {selectedDrink.drinkId !== 0 && (
              <div>
                <ListInfoItem
                  title={selectedDrink.name}
                  imgSrc="/src/assets/tempgif.gif"
                  tag={getTagName(selectedDrink.tagId)}
                  content={selectedDrink.description}
                  numberInfo={3}
                  isWaiting={false}
                  outLine={true}
                  routingFunc={null}
                />
                <ReselectBtn
                  onClick={() => {
                    setSelectedDrink(initialDrinkData);
                  }}
                >
                  재선택
                </ReselectBtn>
              </div>
            )}
          </div>
        </QuestionDiv>
        <QuestionDiv>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              fontFamily: "SeoulNamsan",
              fontSize: "14px",
            }}
          >
            <Title>위치</Title>
            <DropdownInput
              value={sido}
              onChange={(e) => setSido(e.target.value)}
            >
              <option value="대전" key="대전">
                대전
              </option>
              <option value="서울" key="서울">
                서울
              </option>
              <option value="change Sido" key="change Sido">
                change Sido
              </option>
            </DropdownInput>
            시
            <DropdownInput
              value={gugun}
              onChange={(e) => setGugun(e.target.value)}
            >
              <option value="유성" key="유성">
                유성
              </option>
              <option value="동구" key="동구">
                동구
              </option>
              <option value="change  Gugun" key="change  Gugun">
                change Gugun
              </option>
            </DropdownInput>
            구
            <DropdownInput
              value={dong}
              onChange={(e) => setDong(e.target.value)}
            >
              <option value="덕명" key="덕명">
                덕명
              </option>
              <option value="봉명" key="봉명">
                봉명
              </option>
              <option value="change  Dong" key="change  Dong">
                change Dong
              </option>
            </DropdownInput>
            동
          </div>
        </QuestionDiv>
        <QuestionDiv>
          <Title>시간</Title>
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <DateInput value={date} onChange={(e) => setDate(e.target.value)} />
            <TimeInput value={time} onChange={(e) => setTime(e.target.value)} />
          </div>
        </QuestionDiv>
        <QuestionDiv style={{ fontFamily: "SeoulNamsan", fontSize: "14px" }}>
          <Title>조건</Title>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              marginBottom: "0.5rem",
            }}
          >
            <SubTitle>최대 인원</SubTitle>
            <InputShort
              value={maxParticipants}
              onChange={(e) =>
                setMaxParticipants(
                  !Number.isNaN(parseInt(e.target.value))
                    ? parseInt(e.target.value)
                    : 0
                )
              }
            />
            명
          </div>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              marginBottom: "0.5rem",
            }}
          >
            <img src="/src/assets/liver.svg" />
            <SubTitle>간수치</SubTitle>
            <InputShort
              placeholder="40"
              value={liverLimit > 0 ? liverLimit : ""}
              onChange={(e) => setLiverLimit(parseInt(e.target.value))}
            />
            IU/L이상
          </div>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              marginBottom: "0.5rem",
            }}
          >
            <img src="/src/assets/age.svg" />
            <SubTitle>나이</SubTitle>
            <InputShort
              placeholder="20"
              value={minAge > 0 ? minAge : ""}
              onChange={(e) => setMinAge(parseInt(e.target.value))}
            />
            세 이상
            <InputShort
              placeholder="100"
              value={maxAge > 0 ? maxAge : ""}
              onChange={(e) => setMaxAge(parseInt(e.target.value))}
            />
            세 미만
          </div>
        </QuestionDiv>
        <QuestionDiv>
          <Title>설명</Title>
          <InfoTextArea
            placeholder="모임에 대한 소개글을 작성해주세요"
            value={meetDesc}
            onChange={(e) => setMeetDesc(e.target.value)}
          ></InfoTextArea>
        </QuestionDiv>
        <div>
          <ImageInput />
        </div>
      </div>
      <footer>
        <FooterBigBtn
          content="정보 수정 완료"
          color="var(--c-yellow)"
          bgColor="white"
          reqFunc={() => {
            console.log("모임 정보 수정 완료");
          }}
        />
      </footer>
    </div>
  );
};
export default MeetingInfoManage;
