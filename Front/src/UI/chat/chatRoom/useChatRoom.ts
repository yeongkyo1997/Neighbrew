// ChatRoom의 상태, API 호출, 이벤트 처리
import { useParams } from "react-router-dom";
import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import SockJS from "sockjs-client";
import { CompatClient, Stomp } from "@stomp/stompjs";
import { Chat, ChatRoom as ChatRoomInfo, MeetChatMessage, User } from "../../../Type/types";
import { callApi } from "../../../utils/api";
import useEffectEvent from "../../../hooks/useEffectEvent";

const useChatRoom = () => {
  const { id } = useParams();
  const client = useRef<CompatClient>();
  const [messages, setMessages] = useState<Chat[]>([]);
  const [ismodal, setIsmodal] = useState(false);
  const [chatRoomName, setChatRoomName] = useState<string>();
  const userId = parseInt(localStorage.getItem("myId"));
  const [users, setUsers] = useState<User[]>([]);
  const [message, setMessage] = useState("");
  const [chatRoomId, setChatRoomId] = useState(0);
  const messageHandler = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setMessage(e.target.value);
  };
  // 웹소켓 연결 및 이벤트 핸들러 설정
  const connectToWebSocket = useEffectEvent(() => {
    client.current = Stomp.over(() => {
      const ws = new SockJS("/ws");
      return ws;
    });

    client.current.connect({ Authorization: `Bearer ${localStorage.getItem("token")}` }, () => {
      // 웹소켓 이벤트 핸들러 설정
      client.current.subscribe(`/pub/room/${id}`, (res) => {
        const receivedMessage = JSON.parse(res.body) as MeetChatMessage;
        setMessages(prevMessages => [
          ...prevMessages,
          {
            message: receivedMessage.message,
            userId: receivedMessage.userId,
            userNickname: receivedMessage.userNickname,
            user: {
              userId: receivedMessage.userId,
              nickname: receivedMessage.userNickname,
            },
            createdAt: `${new Date().getHours()}:${String(new Date().getMinutes()).padStart(2, "0")}`,
          },
        ]);
      });
    });
  });

  useEffect(() => {
    connectToWebSocket();
    return () => {
      client.current?.disconnect();
    };
  }, [id, connectToWebSocket]);
  // 엔터 누르면 메세지 전송
  const sendMessageHandler = () => {
    if (message.trim() === "") return;
    client.current.send(
      `/sub/chat/${id}/sendMessage`,
      {},
      JSON.stringify({ message: message, userId, user: { userId: userId } })
    );
    setMessage("");
    scroll();
  };

  const navigate = useNavigate();
  const rapperDiv = useRef<HTMLInputElement>(null);

  // 채팅방 입장시 채팅 메시지 가져오기
  useEffect(() => {
    callApi<Chat[]>(
      "GET",
      `api/chatMessage/${id}/${localStorage.getItem("myId")}/messages`
    )
      .then((res) => {
        setChatRoomId(res.data[0].chatRoomId);
        setMessages(res.data);
        return res;
      })
      .then(() => {
        void callApi<ChatRoomInfo>("GET", `api/chatroom/${id}/detail`).then((res) => {
          setChatRoomName(res.data.chatRoomName);
        });
      })
      .catch(() => undefined);

    callApi<User[]>("GET", `/api/chatroom/${id}/${localStorage.getItem("myId")}/users`)
      .then((res) => {
        setUsers(res.data);
      })
      .catch(() => undefined);
  }, [id]);

  // 방 입장 또는 메세지 보내면 스크롤 내려주는 로직
  useEffect(() => {
    scroll();
  }, [messages]);
  // 스크롤 로직
  const scroll = () => {
    window.scrollTo({
      top: rapperDiv.current.scrollHeight,
      behavior: "smooth",
    });
  };

  useEffect(() => {
    scroll();
  }, []);


  const OutRoomHandler = () => {
    navigate("/chatList");
  };
  const chaterInfoHandler = () => {
    ismodal ? setIsmodal(false) : setIsmodal(true);
  };
  const leaveRoom = () => {
    navigate("/chatList");
    client.current.send(
      `/sub/room/${chatRoomId}/leave`,
      {},
      JSON.stringify({ userId })
    );
  };

  return {
    messages, ismodal, chatRoomName, userId, users, message, messageHandler, sendMessageHandler,
    navigate, rapperDiv, OutRoomHandler, chaterInfoHandler, leaveRoom,
  };
};

export type ChatRoomState = ReturnType<typeof useChatRoom>;

export default useChatRoom;
