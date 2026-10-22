/*
[MeetingCreate.tsx]
모임 생성 페이지
모임 이름, 주종 카테고리, 술 검색, 위치, 시간, 조건, 설명, 이미지 첨부 가능
*/
import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import NavbarSimple from "../navbar/NavbarSimple";
import styled from "styled-components";
import FooterBigBtn from "../footer/FooterBigBtn";
import ImageInput from "../components/ImageInput";
import MeetingDrinkSearch from "./MeetingDrinkSearch";
import { callApi } from "../../utils/api";
import { Drink } from "../../Type/types";
import { initialMeetDetail, initialDrink, initialSido, initialGugun } from "../common";
import { WhiteModal, DateInput, TimeInput, InputText } from "../../style/common";
import { localDate, localTime, formateDate, formateTime } from "./DateTimeCommon";
import {
  titleCheck,
  drinkCheck,
  positionCheck,
  timeCheck,
  participantsCheck,
  liverLimitCheck,
  ageCheck,
  imgcheck,
} from "./CheckValid";
import Modal from "react-modal";
import { Tooltip } from "react-tooltip";
import imageCompression from "browser-image-compression";
import LoadingDot from "../etc/LoadingDot";

const Title = styled.div`
  font-family: "JejuGothic";
  font-size: 24px;
  text-align: left;
  margin-top: 1rem;
  margin-bottom: 0.5rem;
`;

const SubTitle = styled.div`
  font-family: "NanumSquareNeo";
  font-size: 16px;
  text-align: left;
`;

const QuestionDiv = styled.div`
  margin-top: 1.5rem;
`;

const InputShort = styled(InputText)`
  width: 4rem;
  padding: 1% 3%;
  text-align: right;
`;

const DropdownInput = styled.select`
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

const LimitDiv = styled.div`
  display: flex;
  align-items: center;
`;

const InfoTextArea = styled.textarea`
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

const ErrorDiv = styled.div`
  color: red;
  text-align: left;
  font-family: "NanumSquareNeo";
  font-size: 14px;
  padding: 0.5rem;
`;

const SubText = styled.div`
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

const TooltipBtn = styled.div`
  padding: 0 0.5rem;
`;

const MeetingCreate = () => {
  const titleRef = useRef(null);
  const sidoRef = useRef(null);
  const dateRef = useRef(null);
  const maxPRef = useRef(null);
  const liverRef = useRef(null);
  const minAgeRef = useRef(null);

  const navigate = useNavigate();
  //모임 생성 후 모임 상세로 이동
  const GoMeetDetailHandler = (meetId: number) => {
    navigate(`/meet/${meetId}`, { replace: true });
  };
  //모달 관련
  const [isModalOn, setIsModalOn] = useState(false);
  const [errorMsg, setErrorMsg] = useState(""); //모달에 띄울 에러메시지
  const [loadingModalOn, setLoadingModalOn] = useState(false); //로딩중일 때 모달(이미지 압축중일때)

  //모임 및 유저 정보
  const [userId, setUserId] = useState(0); //현재 유저의 userId
  const [sidoList, setSidoList] = useState([initialSido]);
  const [gugunList, setGugunList] = useState([initialGugun]);

  //폼에 들어갈 state들
  const [meetTitle, setMeetTitle] = useState<string>("");
  const [selectedCategory, setSelectedCategory] = useState(0); //주종카테고리
  const [selectedDrink, setSelectedDrink] = useState<Drink>(initialDrink); //주류
  const [sido, setSido] = useState(initialSido); //시도
  const [gugun, setGugun] = useState(initialGugun); //구군
  const [date, setDate] = useState(""); //날짜
  const [time, setTime] = useState(""); //시간
  const [maxParticipants, setMaxParticipants] = useState(8); //최대인원
  const [liverLimit, setLiverLimit] = useState(0); //간수치 제한
  const [minAge, setMinAge] = useState(0); //최소나이
  const [maxAge, setMaxAge] = useState(0); //최대나이
  const [meetDesc, setMeetDesc] = useState(""); //모임 소개
  const [imgSrc, setImgSrc] = useState<string>(""); //이미지 경로
  const [file, setFile] = useState(null); //파일 타입

  //생성 버튼 클릭했는지 - 버튼 한번이라도 클릭 시에만 빨간 가이드 글씨 오픈
  const [btnClicked, setBtnClicked] = useState(false);
  const [isClick, setIsClick] = useState(false);

  //모임의 정보 초기 세팅
  useEffect(() => {
    setMeetTitle(initialMeetDetail.meet.meetName);
    setSelectedCategory(initialMeetDetail.meet.tagId); //주종카테고리
    setSelectedDrink(initialMeetDetail.meet.drink); //주류아이디
    setSido(initialSido); //시도
    setGugun(initialGugun); //구군
    setDate(formateDate(`${localDate()}T${localTime()}:00`)); //날짜
    setTime(formateTime(`${localDate()}T${localTime()}:00`)); //시간
    setMaxParticipants(initialMeetDetail.meet.maxParticipants); //최대인원
    setLiverLimit(initialMeetDetail.meet.minLiverPoint); //간수치 제한
    setMinAge(initialMeetDetail.meet.minAge); //최소 나이
    setMaxAge(initialMeetDetail.meet.maxAge); //최대 나이
    setMeetDesc(initialMeetDetail.meet.description); //모임 소개
    setImgSrc(initialMeetDetail.meet.imgSrc); //이미지 경로
    //로컬 스토리지에서 userId 가져오기
    setUserId(parseInt(localStorage.getItem("myId")));
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
    //시도 정보 미리 받아와 세팅하기
    void callApi("get", "api/sido").then(res => {
      setSidoList([initialSido, ...res.data]);
    });
  }, []);

  //선택한 시도에 따라 구군 fetch
  useEffect(() => {
    setGugun(initialGugun); //초기화
    void callApi("get", `api/gugun/${sido.sidoCode}`).then(res => {
      setGugunList([initialGugun, ...res.data]);
    });
  }, [sido]);

  //필수 입력값 검증(위 내용 외에 추가로 모달창 오픈)
  const checkRequiredValue = () => {
    //빨간글씨가 하나라도 있으면 모달 오픈
    const isValid =
      titleCheck(meetTitle.trim()) &&
      drinkCheck(selectedDrink) &&
      positionCheck(sido.sidoCode, gugun.gugunCode) &&
      timeCheck(date, time) &&
      participantsCheck(maxParticipants) &&
      liverLimitCheck(liverLimit) &&
      ageCheck(minAge, maxAge) &&
      imgcheck(file);
    if (!isValid) {
      if (!titleCheck(meetTitle.trim())) {
        setErrorMsg("제목 입력을 확인해주세요");
        titleRef.current.focus();
      } else if (!drinkCheck(selectedDrink)) {
        setErrorMsg("주류를 선택해주세요.");
      } else if (!positionCheck(sido.sidoCode, gugun.gugunCode)) {
        setErrorMsg("지역 입력을 확인해주세요.");
        sidoRef.current.focus();
      } else if (!timeCheck(date, time)) {
        setErrorMsg("시간 입력을 확인해주세요.");
        dateRef.current.focus();
      } else if (!participantsCheck(maxParticipants)) {
        setErrorMsg("최대 인원을 확인해주세요.");
        maxPRef.current.focus();
      } else if (!liverLimitCheck(liverLimit)) {
        setErrorMsg("간수치 입력을 확인해주세요.");
        liverRef.current.focus();
      } else if (!ageCheck(minAge, maxAge)) {
        setErrorMsg("나이 입력을 확인해주세요.");
        minAgeRef.current.focus();
      } else if (!imgcheck(file)) {
        setErrorMsg("첨부한 이미지를 확인해주세요.");
      }
      return false;
    }
    //최대인원수 < 1
    if (1 > maxParticipants) {
      setErrorMsg(`최대 인원수는 1명 이상이어야합니다.`);
      return false;
    }
    //최소 나이 > 최대 나이 일때
    if (maxAge && minAge > maxAge) {
      setErrorMsg("최소 나이는 최대 나이보다 \n 클 수 없습니다.");
      return false;
    }
    return true;
  };

  //필수가 아닌 입력값 검증
  const checkNonRequiredValue = (value: string | number) => {
    //숫자 값이 없는지 -> 있어야만 폼데이터에 넣음
    const res = value !== 0 && value != null && !Number.isNaN(value);
    return res;
  };

  //이미지 압축에 사용하는 옵션
  const options = {
    // maxSizeMB: 5, // 허용하는 최대 사이즈 지정
    maxWidthOrHeight: 1000, // 허용하는 최대 width, height 값 지정
    // fileType: "image/webp",
  };

  //수정 완료 버튼 클릭 api
  const createMeeting = () => {
    if (isClick) return; //throttle역할
    setBtnClicked(true);
    //api 요청 전에 확인
    //입력 값들이 적절한가?
    if (!checkRequiredValue()) {
      setIsModalOn(true);
      return;
    }

    setIsClick(true);

    const f = new FormData();
    //필수 입력o
    f.append("userId", userId.toString());
    f.append("meetName", meetTitle.trim());
    f.append("maxParticipants", maxParticipants.toString());
    f.append("meetDate", `${date}T${time}:00`);
    f.append("tagId", selectedCategory.toString());
    f.append("sidoCode", sido.sidoCode.toString());
    f.append("gugunCode", gugun.gugunCode.toString());
    f.append("drinkId", selectedDrink.drinkId !== 0 ? selectedDrink.drinkId.toString() : "");
    //필수 입력x
    if (checkNonRequiredValue(liverLimit)) {
      f.append("minLiverPoint", liverLimit.toString());
    }
    if (checkNonRequiredValue(minAge)) {
      f.append("minAge", minAge.toString());
    }
    if (checkNonRequiredValue(maxAge)) {
      f.append("maxAge", maxAge.toString());
    }
    f.append("description", meetDesc);

    if (file === null) {
      f.append("image", file);
      createApi(f);
    } else {
      setLoadingModalOn(true);
      //압축하면 blob 타입-> file 타입으로 변환
      const uploadFile = imageCompression(file, options);
      uploadFile
        .then(res => {
          const resizingFile = new File([res], file.name, {
            type: file.type,
          });
          f.append("image", resizingFile);
        })
        .then(() => {
          createApi(f);
        })
        .catch(() => {
          setErrorMsg("이미지 처리 중 오류가 발생했습니다.");
          setLoadingModalOn(false);
          setIsModalOn(true);
          setIsClick(false);
        });
    }
  };

  const createApi = (f: FormData) => {
    const promise = callApi("post", `/api/meet/create`, f);
    promise
      .then(res => {
        GoMeetDetailHandler(res.data.meetId); //모임 상세 페이지로 이동
      })
      .catch(error => {
        const data = error?.response?.data;
        setErrorMsg(typeof data === "string" ? data : "요청 처리 중 오류가 발생했습니다.");
        setLoadingModalOn(false);
        setIsModalOn(true);
        setIsClick(false);
      });
  };

  return (
    <div>
      <header>
        <NavbarSimple title="모임 만들기" />
      </header>
      <div style={{ padding: "0 1.5rem", marginBottom: "7rem" }}>
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
      </div>
      <FooterBigBtn
        content="모임 만들기"
        color="var(--c-yellow)"
        bgColor="white"
        reqFunc={() => {
          createMeeting();
        }}
      />
      <Modal isOpen={isModalOn} onRequestClose={() => setIsModalOn(false)} style={WhiteModal}>
        <div style={{ whiteSpace: "pre-line", overflow: "auto" }}>{errorMsg}</div>
      </Modal>
      <Modal
        isOpen={loadingModalOn}
        onRequestClose={() => undefined} //닫히지 않아야함
        style={WhiteModal}
      >
        <div style={{ whiteSpace: "pre-line", overflow: "auto", padding: "1rem" }}>
          <div style={{ paddingBottom: "0.5rem" }}>
            이미지 압축중입니다. <br /> 잠시만 기다려주세요.
          </div>
          <LoadingDot color="var(--c-yellow)" />
        </div>
      </Modal>
    </div>
  );
};
export default MeetingCreate;
