// MeetingInfoManage의 상태, API 호출, 이벤트 처리
import { useState, useRef, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { MeetDetail, Sido, Gugun } from "../../../Type/types";
import { callApi } from "../../../utils/api";
import { Drink } from "../../../Type/types";
import { initialMeetDetail, initialDrink, initialSido, initialGugun } from "../../common";
import { formateDate, formateTime } from "../DateTimeCommon";
import {
  titleCheck,
  drinkCheck,
  positionCheck,
  timeCheck,
  participantsCheck,
  liverLimitCheck,
  ageCheck,
  imgcheck,
} from "../CheckValid";
import imageCompression from "browser-image-compression";
import { AxiosError } from "axios";
import useEffectEvent from "../../../hooks/useEffectEvent";

const useMeetingInfoManage = () => {
  const titleRef = useRef<HTMLInputElement>(null);
  const sidoRef = useRef<HTMLSelectElement>(null);
  const dateRef = useRef<HTMLInputElement>(null);
  const maxPRef = useRef<HTMLInputElement>(null);
  const liverRef = useRef<HTMLInputElement>(null);
  const minAgeRef = useRef<HTMLInputElement>(null);

  const navigate = useNavigate();
  //모임 수정 후 모임 상세로 이동
  const GoMeetDetailHandler = () => {
    navigate(-1);
  };
  //호스트가 아닌데 편집하려고 할 시 모임 메인으로 이동
  const GoMeetMainHandler = () => {
    navigate(`/meet`);
  };
  //모달 관련
  const [isModalOn, setIsModalOn] = useState(false);
  const [isGotoMainModalOn, setIsGotoMainModalOn] = useState(false); //모임 메인으로 이동시키는 모달은 따로 관리
  const [errorMsg, setErrorMsg] = useState(""); //모달에 띄울 에러메시지
  const [loadingModalOn, setLoadingModalOn] = useState(false); //로딩중 모달

  //미팅 기존 정보
  const [meetData, setMeetData] = useState<MeetDetail>(initialMeetDetail);
  const { meetId } = useParams(); //meetId는 라우터 링크에서 따오기
  const [userId, setUserId] = useState(0); //현재 유저의 userId

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
  const [, setImgSrc] = useState<string>(""); //이미지 경로
  const [file, setFile] = useState<File | null>(null); //파일 타입

  //이미지 수정용
  const [newImgSrc, setNewImgSrc] = useState("");

  //지역 관련 state
  const [sidoList, setSidoList] = useState([initialSido]);
  const [gugunList, setGugunList] = useState([initialGugun]);

  //생성 버튼 클릭했는지 - 버튼 한번이라도 클릭 시에만 빨간 가이드 글씨 오픈
  const [btnClicked, setBtnClicked] = useState(false);
  const [isClick, setIsClick] = useState(false);

  //첫 로딩 시
  useEffect(() => {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
    //시도 정보 미리 받아와 세팅하기
    void callApi<Sido[]>("get", "api/sido").then(res => {
      setSidoList([initialSido, ...res.data]);
    });
  }, []);

  //api 호출, 기존 모임의 정보 저장
  useEffect(() => {
    const promise = callApi<MeetDetail>("get", `api/meet/${meetId}`);
    void promise.then(res => {
      setMeetData(res.data);
    });
    //로컬 스토리지에서 userId 가져오기
    setUserId(parseInt(localStorage.getItem("myId")));
  }, [meetId]);

  //받아온 모임 정보로 state 초기값 설정
  useEffect(() => {
    setMeetTitle(meetData.meet.meetName);
    setSelectedCategory(meetData.meet.tagId); //주종카테고리
    setSelectedDrink(meetData.meet.drink); //주류아이디
    setSido(meetData.meet.sido); //시도
    setGugun(meetData.meet.gugun); //구군
    setDate(formateDate(meetData.meet.meetDate)); //날짜
    setTime(formateTime(meetData.meet.meetDate)); //시간
    setMaxParticipants(meetData.meet.maxParticipants); //최대인원
    setLiverLimit(meetData.meet.minLiverPoint); //간수치 제한
    setMinAge(meetData.meet.minAge); //최소 나이
    setMaxAge(meetData.meet.maxAge); //최대 나이
    setMeetDesc(meetData.meet.description); //모임 소개
    setImgSrc(meetData.meet.imgSrc); //이미지 경로(원래의)
    setNewImgSrc(meetData.meet.imgSrc); //이미지 경로(새로세팅할)
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }, [meetData]);

  //선택한 시도에 따라 구군 fetch
  //gugun은 읽기만 하고, 시도가 바뀔 때만 실행한다
  const onSidoChange = useEffectEvent(() => {
    //초기 모임 데이터 로딩은 제외하고, 구군 정보 초기화
    if (gugun.sidoCode !== sido.sidoCode) {
      setGugun(initialGugun); //초기화
    }
    void callApi<Gugun[]>("get", `api/gugun/${sido.sidoCode}`).then(res => {
      setGugunList([initialGugun, ...res.data]);
    });
  });
  useEffect(() => {
    onSidoChange();
  }, [sido, onSidoChange]);

  //api 호출 전 각종 데이터 검증
  //유저 아이디와 호스트 아이디 확인
  //권한이 없다면 메인 페이지로 이동시킴
  const checkIsHost = () => {
    let isValid = true;
    if (userId !== meetData.meet.host.userId) {
      isValid = false;
      setErrorMsg("모임 편집 권한이 없습니다.");
    }
    return isValid;
  };

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
    //모임 현재 인원수 > 최대인원수 일때
    if (meetData.meet.nowParticipants > maxParticipants) {
      setErrorMsg(
        `최대 인원수는 현재 인원수보다 적어질 수 없습니다. \n 현재 인원수: ${meetData.meet.nowParticipants}`
      );
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
    maxWidthOrHeight: 1000, // 허용하는 최대 width, height 값 지정
  };

  //수정 완료 버튼 클릭 api
  const updateMeeting = () => {
    if (isClick) return; //throttle역할
    setBtnClicked(true);
    //api 요청 전에 확인
    //호스트가 맞는가?
    if (!checkIsHost()) {
      setIsGotoMainModalOn(true);
      return;
    }
    //입력 값들이 적절한가?
    if (!checkRequiredValue()) {
      setIsModalOn(true);
      return;
    }

    setIsClick(true);

    const f = new FormData();
    //필수 입력o
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

    //이미지 수정을 위한 분기
    if (file === null && newImgSrc === "no image") {
      f.append("imgSrc", "no image");
      updateApi(f);
    } else if (file !== null) {
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
          updateApi(f);
        })
        .catch(() => {
          setErrorMsg("이미지 처리 중 오류가 발생했습니다.");
          setLoadingModalOn(false);
          setIsModalOn(true);
          setIsClick(false);
        });
    } else {
      updateApi(f);
    }
  };

  const updateApi = (f: FormData) => {
    const promise = callApi("put", `/api/meet/modify/${userId}/${meetId}`, f);
    promise
      .then(() => {
        GoMeetDetailHandler(); //모임 상세 페이지로 이동
      })
      .catch((error: AxiosError) => {
        const data = error?.response?.data;
        setErrorMsg(typeof data === "string" ? data : "요청 처리 중 오류가 발생했습니다.");
        setLoadingModalOn(false);
        setIsModalOn(true);
        setIsClick(false);
      });
  };

  return {
    titleRef, sidoRef, dateRef, maxPRef, liverRef, minAgeRef, GoMeetMainHandler, isModalOn,
    setIsModalOn, isGotoMainModalOn, setIsGotoMainModalOn, errorMsg, loadingModalOn, meetTitle,
    setMeetTitle, selectedCategory, setSelectedCategory, selectedDrink, setSelectedDrink, sido,
    setSido, gugun, setGugun, date, setDate, time, setTime, maxParticipants, setMaxParticipants,
    liverLimit, setLiverLimit, minAge, setMinAge, maxAge, setMaxAge, meetDesc, setMeetDesc, file,
    setFile, newImgSrc, setNewImgSrc, sidoList, gugunList, btnClicked, updateMeeting,
  };
};

export type MeetingInfoManageState = ReturnType<typeof useMeetingInfoManage>;

export default useMeetingInfoManage;
