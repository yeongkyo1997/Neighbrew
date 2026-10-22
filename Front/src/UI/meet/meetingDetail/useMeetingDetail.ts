// MeetingDetail의 상태, API 호출, 이벤트 처리
import { useState, useEffect, useRef } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { CompatClient, Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { AxiosError } from "axios";
import { callApi } from "../../../utils/api";
import { initialMeetDetail, initialUser } from "../../common";
import { MeetDetail, User } from "../../../Type/types";

const useMeetingDetail = () => {
  const { meetId } = useParams(); //meetId는 라우터 링크에서 따오기
  const [meetDetailData, setMeetDetailData] = useState<MeetDetail>(initialMeetDetail); //모임 데이터
  const [memberList, setMemberList] = useState<User[]>([]); //참여자 리스트
  const [applicantList, setApplicantList] = useState<User[]>([]); //참여자 리스트
  const [userId, setUserId] = useState(0); //현재 유저의 userId
  const [userData, setUserData] = useState(initialUser);
  const [userStatus, setUserStatus] = useState("");
  const client = useRef<CompatClient>();

  const [exitModalOn, setExitModalOn] = useState(false); //나가기 모달이 열려있는가?
  const [deleteModalOn, setDeleteModalOn] = useState(false); //나가기 모달이 열려있는가?
  const [simpleModalOn, setSimpleModalOn] = useState(false); //오류 모달이 열려있는가?
  const [manageModalOn, setManageModalOn] = useState(false); //관리 모달이 열려있는가?
  const [acceptModalOn, setAcceptModalOn] = useState(false); //승인거절 모달이 열려있는가?
  const [targetUser, setTargetUser] = useState<User>(initialUser); //승인/거절할 유저
  const [targetAction, setTargetAction] = useState(true); //승인:true, 거절:false
  const [errMsg, setErrMsg] = useState(""); //모달에 표시할 오류메시지
  const bgImg =
    meetDetailData.meet.imgSrc == "no image"
      ? "/src/assets/meetDefaultImg.jpg"
      : meetDetailData.meet.imgSrc;

  const navigate = useNavigate();
  //뒤로가기(버튼 클릭 또는 모임 삭제 후)
  const GoBackHandler = () => {
    navigate(-1);
  };
  //특정 술로 이동
  const GotoDrinkPostHandler = (drinkId: number) => {
    navigate(`/drinkpost/${drinkId}`);
  };
  //모임 편집 페이지로 이동
  const GotoMeetInfoManage = (meetId: number) => {
    navigate(`/meet/${meetId}/manage/info`);
  };
  //모임 평가 페이지로 이동
  const GotoRatingHandler = () => {
    navigate(`/rating/${meetId}`);
  };
  //없는 모임일 경우 뒤로가기(모임 메인으로 이동?)
  const GotoMainHandler = () => {
    navigate(-1);
  };
  //특정 채팅방으로 이동
  const GotoChatHandler = () => {
    navigate(`/chatList/${meetDetailData.meet.chatRoomId}`);
  };

  // 웹소켓 연결 및 이벤트 핸들러 설정
  const connectToWebSocket = () => {
    client.current = Stomp.over(() => {
      const ws = new SockJS("/ws");
      return ws;
    });
    client.current.connect({ Authorization: `Bearer ${localStorage.getItem("token")}` }, () => undefined);
  };

  //api호출
  const fetchMeetData = () => {
    const promise = callApi<MeetDetail>("get", `api/meet/${meetId}`);
    promise
      .then(res => {
        setMeetDetailData(res.data); //받아온 데이터로 meetDetailData 세팅
      })
      .catch(() => {
        GotoMainHandler();
      });
  };

  useEffect(() => {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
    //로컬 스토리지에서 userId 가져오기
    setUserId(parseInt(localStorage.getItem("myId")));
  }, []);

  //현재 유저의 정보 가져오기
  useEffect(() => {
    if (userId === 0) return;
    //생년월일은 본인 정보(myinfo)에만 포함된다
    const promise = callApi<User>("get", "api/user/myinfo");
    void promise.then(res => {
      setUserData(res.data);
    });
  }, [userId]);

  //meetId가 생기면 api로 데이터 로드
  useEffect(() => {
    fetchMeetData();
  }, [meetId]);

  useEffect(() => {
    if (meetDetailData === undefined) return;
    if (meetDetailData !== undefined) {
      //users에서 apply인 유저 제외하고 memberList에 넣기
      const members = meetDetailData.participants
        .filter(participant => participant.status !== "APPLY")
        .map(participant => participant.user);
      setMemberList(members);
      //apply 세팅
      const applicants = meetDetailData.participants
        .filter(participant => participant.status === "APPLY")
        .map(participant => participant.user);
      setApplicantList(applicants);
      //현재 유저의 상태를 관리(HOST/GUEST/APPLY/NONE)
      const myParticipation = meetDetailData.participants.find(
        participant => participant.user.userId === userId
      );
      setUserStatus(myParticipation?.status ?? "NONE");
    }
  }, [meetDetailData]);

  //생일을 넣으면 나이를 계산해줌
  const calcAge = (birth: string) => {
    const today = new Date();
    const currentYear = today.getFullYear();
    const birthYear = parseInt(birth.substring(0, 4), 10);
    const age = currentYear - birthYear + 1;
    return age;
  };

  //참여 신청하기
  function applyMeet() {
    //내 정보를 아직 못 불러왔으면 제한을 확인할 수 없다
    if (userData.userId === 0) {
      setErrMsg("사용자 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.");
      setSimpleModalOn(true);
      return;
    }
    //나이 제한이 있는 모임은 생년월일을 입력해야 참여할 수 있다 (서버도 같은 규칙으로 확인한다)
    if (hasAgeLimit() && !userData.birth) {
      setErrMsg("생년월일을 입력해야 나이 제한이 있는 모임에 참여할 수 있습니다.\n마이페이지에서 입력해 주세요.");
      setSimpleModalOn(true);
      return;
    }
    //간수치 제한 확인
    if ((meetDetailData.meet.minLiverPoint ?? 0) > userData.liverPoint) {
      setErrMsg("간수치 제한에 부합하지 않습니다.");
      setSimpleModalOn(true);
      return;
    }
    //나이 제한 확인 (0 이하는 제한 없음)
    const minAge = meetDetailData.meet.minAge ?? 0;
    const maxAge = meetDetailData.meet.maxAge ?? 0;
    if (
      hasAgeLimit() &&
      ((minAge > 0 && minAge > calcAge(userData.birth)) || (maxAge > 0 && maxAge < calcAge(userData.birth)))
    ) {
      setErrMsg("나이 제한에 부합하지 않습니다.");
      setSimpleModalOn(true);
      return;
    }
    const promise = callApi("post", `api/meet/apply`, {
      userId: userId,
      meetId: meetId,
    });
    promise
      .then(() => {
        setUserStatus("APPLY");
        setErrMsg(`모임에 참여 신청하였습니다!\n신청 취소는 '승인 대기중' 버튼을 클릭해주세요.`);
        setSimpleModalOn(true);
      })
      .catch((err: AxiosError<string>) => {
        setErrMsg(err.response.data);
        setSimpleModalOn(true);
      });
  }

  //신청 취소하기
  function cancelApply() {
    const promise = callApi("put", `api/meet/apply-cancel`, {
      userId: userId,
      meetId: meetId,
    });
    void promise.then(() => {
      setUserStatus("NONE");
    });
  }

  //모임 나가기
  function exitMeet() {
    const promise = callApi("delete", `api/meet/exit`, {
      userId: userId,
      meetId: meetId,
    });
    void promise
      .then(() => {
        setUserStatus("NONE");
      })
      .then(() => fetchMeetData());
  }

  //모임 삭제 요청
  const DeleteMeeting = () => {
    const promise = callApi("delete", `api/meet/delete/${meetId}`, {
      userId: parseInt(localStorage.getItem("myId")),
    });
    promise
      .then(() => {
        GoBackHandler(); //모임 삭제 후 뒤로가기
      })
      .catch((e: AxiosError<string>) => {
        setErrMsg(e.response.data);
        setSimpleModalOn(true);
      });
  };

  //수락/거절하기
  const memberHandler = (user: User) => {
    const promise = callApi("post", `api/meet/manage-user`, {
      userId: user.userId,
      meetId: parseInt(meetId),
      applyResult: targetAction,
    });
    promise
      .then(() => {
        fetchMeetData();
      })
      .catch((e: AxiosError<string>) => {
        setErrMsg(e.response.data);
        setSimpleModalOn(true);
      });
  };

  //채팅방 참여하기
  const gotoChat = () => {
    connectToWebSocket();
    setTimeout(() => {
      sendMessageHandler(meetDetailData.meet.chatRoomId);
    }, 500);

    GotoChatHandler();
  };

  const sendMessageHandler = (roomId: number) => {
    client.current.send(`/sub/join/${roomId}`, {}, JSON.stringify({ userId }));
  };

  function hasAgeLimit() {
    if (meetDetailData === undefined) return false;
    const res =
      (meetDetailData.meet.minAge ?? 0) > 0 || (meetDetailData.meet.maxAge ?? 0) > 0 ? true : false;
    return res;
  }

  return {
    meetId, meetDetailData, memberList, applicantList, userStatus, bgImg,
    exitModalOn, setExitModalOn, deleteModalOn, setDeleteModalOn, simpleModalOn, setSimpleModalOn,
    manageModalOn, setManageModalOn, acceptModalOn, setAcceptModalOn,
    targetUser, setTargetUser, targetAction, setTargetAction, errMsg,
    GoBackHandler, GotoDrinkPostHandler, GotoMeetInfoManage, GotoRatingHandler,
    gotoChat, applyMeet, cancelApply, exitMeet, DeleteMeeting, memberHandler, hasAgeLimit,
  };
};

export type MeetingDetailState = ReturnType<typeof useMeetingDetail>;

export default useMeetingDetail;
