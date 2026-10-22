import { useState, useEffect, useRef } from "react";
import "./App.css";
import { useLocation, Route, Routes } from "react-router-dom";
import Loading from "./UI/etc/Loading";
import Login from "./UI/user/Login";
import Main from "./UI/home/Main";
import ChatList from "./UI/chat/ChatList";
import ChatRoom from "./UI/chat/ChatRoom";
import DrinkpostTotal from "./UI/drinkpost/DrinkpostTotal";
import MeetingMain from "./UI/meet/MeetingMain";
import MeetingDetail from "./UI/meet/meetingDetail/MeetingDetail";
import MeetingCreate from "./UI/meet/meetingCreate/MeetingCreate";
import MeetingManageMain from "./UI/meet/MeetingManageMain";
import MeetingMemberManage from "./UI/meet/MeetingMemberManage";
import MeetingInfoManage from "./UI/meet/meetingInfoManage/MeetingInfoManage";
import Mypage from "./UI/user/mypage/MyPage";
import Follower from "./UI/user/Follower";
import Follow from "./UI/user/Follow";
import KakaoLogin from "./UI/user/KakaoLogin";
import NaverLogin from "./UI/user/NaverLogin";
import GoogleLogin from "./UI/user/GoogleLogin";
import DrinkpostDetail from "./UI/drinkpost/DrinkpostDetail";
import DrinkpostCreate from "./UI/drinkpost/DrinkpostCreate";
import DrinkpostSearch from "./UI/drinkpost/DrinkpostSearch";
import DrinkpostReviewCreate from "./UI/drinkpost/DrinkpostReviewCreate";
import Test from "./Test";
import DrinkpostMain from "./UI/drinkpost/DrinkpostMain";
import DrinkpostReviewDetail from "./UI/drinkpost/drinkpostReviewDetail/DrinkpostReviewDetail";
import DirectChat from "./UI/chat/DirectChat";
import SearchUser from "./UI/user/SearchUser";
import RatingCreate from "./UI/meetrate/RatingCreate";
import NotFound from "./UI/etc/NotFound";
import logo from "./assets/logoNavbar.svg";
import DrinkpostReviewUpdate from "./UI/drinkpost/DrinkpostReviewUpdate";
import AlarmPage from "./UI/etc/AlarmPage";
import { callApi } from "./utils/api";
import { SseTicket, AlarmLog } from "./Type/types";

function App() {
  const [isLoading, setIsLoading] = useState(true); // 개발시 isLoading true로 두고 하기
  const [userid, setUserid] = useState<string | null>(null);
  const es = useRef<EventSource | undefined>();
  // 마지막으로 받은 알림의 이벤트 ID(pushId). EventSource를 직접 다시 만들면 브라우저가 Last-Event-ID를 보내지 않으므로
  // 재연결할 때 쿼리 파라미터로 넘겨 끊긴 동안 놓친 알림을 다시 받는다.
  const lastEventId = useRef("");
  const location = useLocation();
  // 로그인/로그아웃 시 localStorage의 myId와 SSE 연결 대상을 동기화
  const connectHandler = () => {
    setUserid(localStorage.getItem("myId"));
  };
  useEffect(() => {
    connectHandler();
  }, [location.pathname]);
  useEffect(() => {
    if ("serviceWorker" in navigator) {
      // Register a service worker hosted at the root of the
      // site using the default scope.
      void navigator.serviceWorker.register("/sw.js");
    }
  }, []);
  useEffect(() => {
    setTimeout(() => {
      setIsLoading(true);
    }, 1000);
  }, []);
  // EventSource는 헤더를 보낼 수 없으므로, 인증된 요청으로 30초짜리 일회용 티켓을 받아 URL에 사용
  // (access 토큰을 URL에 싣지 않음). 티켓은 1회용이라 끊기면 새 티켓으로 직접 재연결한다.
  const esconnect = async (id: string, isActive: () => boolean) => {
    let ticket: string;
    try {
      const res = await callApi<SseTicket>("post", "api/push/sse-ticket");
      ticket = res.data.ticket;
    } catch {
      if (isActive()) setTimeout(() => esconnect(id, isActive), 5000);
      return;
    }
    if (!isActive()) return;

    const resume = lastEventId.current ? `&lastEventId=${encodeURIComponent(lastEventId.current)}` : "";
    es.current = new EventSource(`/api/auth/connect/${id}?ticket=${encodeURIComponent(ticket)}${resume}`, {
      withCredentials: true,
    });

    es.current.onmessage = event => {
      if (event.lastEventId) lastEventId.current = event.lastEventId;
      try {
        const data = JSON.parse(event.data as string) as Pick<AlarmLog, "content" | "url">;
        noti(data.content, data.url);
      } catch {
        // JSON이 아닌 메시지(연결 확인 등)는 알림으로 띄우지 않는다
      }
      if (event.data === "finished") {
        es.current?.close();
        return;
      }
    };
    es.current.onerror = () => {
      es.current?.close();
      if (isActive()) setTimeout(() => esconnect(id, isActive), 5000);
    };
  };
  useEffect(() => {
    if (userid == null) return;
    let active = true;
    void esconnect(userid, () => active);

    return () => {
      active = false;
      es.current?.close();
      es.current = undefined;
      lastEventId.current = "";
    };
  }, [userid]);
  useEffect(() => {
    subscribe();
  }, []);

  const subscribe = () => {
    if (!("Notification" in window)) {
      // 브라우저가 Notification API를 지원하는지 확인한다.
      alert("알림을 지원하지 않는 데스크탑 브라우저입니다");
      return;
    }

    if (Notification.permission === "granted") {
      // 이미 알림 권한이 허가됐는지 확인한다.
      // 그렇다면, 알림을 표시한다.
      // const notification = new Notification("안녕하세요!");
      return;
    }

    // 알림 권한이 거부된 상태는 아니라면
    if (Notification.permission !== "denied") {
      // 사용자에게 알림 권한 승인을 요청한다
      void Notification.requestPermission().then(permission => {
        // 사용자가 승인하면, 알림을 표시한다
        if (permission === "granted") {
          new Notification("알림을 허용하셨습니다.");
        }
      });
    }
  };

  const noti = (message: string, url: string) => {
    void navigator.serviceWorker.ready.then(registration => {
      void registration.showNotification("NeighBrew", {
        body: message,
        icon: logo,
        data: url,
        actions: [
          {
            title: "이동",
            action: "goTab",
          },
          {
            title: "닫기",
            action: "close",
          },
        ],
      });
    });
  };

  return (
    <>
      <Routes>
        <Route
          path="/"
          element={
            <>
              {!isLoading && <Loading />}
              {isLoading && <Login></Login>}
            </>
          }
        />
        <Route path="/home" element={<Main />} />
        <Route
          path="/drinkpost"
          element={<DrinkpostMain connectHandler={connectHandler}></DrinkpostMain>}
        />
        <Route path="/meet" element={<MeetingMain />}></Route>
        <Route path="/meet/:meetId" element={<MeetingDetail />}></Route>
        <Route path="/meet/create" element={<MeetingCreate />}></Route>
        <Route path="/meet/:meetId/manage" element={<MeetingManageMain />}></Route>
        <Route path="/meet/:meetId/manage/member" element={<MeetingMemberManage />}></Route>
        <Route path="/meet/:meetId/manage/info" element={<MeetingInfoManage />}></Route>

        <Route path="/myPage/:userid" element={<Mypage></Mypage>}></Route>
        <Route path="/myPage/follower/:userid" element={<Follower></Follower>}></Route>
        <Route path="/myPage/follow/:userid" element={<Follow></Follow>}></Route>
        <Route path="/usersearch" element={<SearchUser></SearchUser>}></Route>
        <Route path="/chatList" element={<ChatList></ChatList>}></Route>
        <Route path="/chatList/:id" element={<ChatRoom></ChatRoom>} />
        <Route path="/directchat/:senderId/:receiverId" element={<DirectChat></DirectChat>}></Route>

        <Route path="/kakao/:str" element={<KakaoLogin></KakaoLogin>}></Route>
        <Route path="/naver/:str" element={<NaverLogin></NaverLogin>}></Route>
        <Route path="/google/:str" element={<GoogleLogin></GoogleLogin>}></Route>

        <Route path="/drinkpost/:drinkId" element={<DrinkpostDetail></DrinkpostDetail>}></Route>
        <Route path="/drinkpost/create" element={<DrinkpostCreate></DrinkpostCreate>}></Route>
        <Route path="/drinkpost/search" element={<DrinkpostSearch></DrinkpostSearch>}></Route>
        <Route
          path="/drinkpost/:drinkId/review/create"
          element={<DrinkpostReviewCreate></DrinkpostReviewCreate>}
        ></Route>
        <Route path="/drinkpost/total" element={<DrinkpostTotal></DrinkpostTotal>}></Route>
        <Route path="/test" element={<Test></Test>}></Route>

        <Route path="/rating/:meetId" element={<RatingCreate></RatingCreate>}></Route>

        <Route
          path="/drinkpost/:drinkId/:reviewId"
          element={<DrinkpostReviewDetail></DrinkpostReviewDetail>}
        ></Route>
        <Route
          path="/drinkpost/:drinkId/:reviewId/update"
          element={<DrinkpostReviewUpdate></DrinkpostReviewUpdate>}
        ></Route>
        <Route path="/*" element={<NotFound></NotFound>}></Route>
        <Route path="/myPage/alarm" element={<AlarmPage></AlarmPage>}></Route>
      </Routes>
    </>
  );
}

export default App;
