// DirectChat의 상태, API 호출, 이벤트 처리
import { useParams } from "react-router-dom";
import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import SockJS from "sockjs-client";
import { CompatClient, Stomp } from "@stomp/stompjs";
import { Chat, User, DmMessages, DmChatMessage } from "../../../Type/types";
import { callApi } from "../../../utils/api";

const useDirectChat = () => {
  const { senderId, receiverId } = useParams();
  const client = useRef<CompatClient>();
  const [messages, setMessages] = useState<Chat[]>([]);
  const [ismodal, setIsmodal] = useState(false);
  const [chatRoomName, setChatRoomName] = useState<string>();
  const userId = parseInt(localStorage.getItem("myId"));
  const [users, setUsers] = useState<User[]>([]);
  const [message, setMessage] = useState("");
  const messageHandler = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setMessage(e.target.value);
  };
  // 웹소켓 연결 및 이벤트 핸들러 설정
  let user1 = senderId;
  let user2 = receiverId;
  if (parseInt(user1) > parseInt(user2)) {
    user1 = receiverId;
    user2 = senderId;
  }

  const connectToWebSocket = () => {
    client.current = Stomp.over(() => {
      const ws = new SockJS("/ws");
      return ws;
    });

    client.current.connect({ Authorization: `Bearer ${localStorage.getItem("token")}` }, () => {
      // 웹소켓 이벤트 핸들러 설정
      client.current.subscribe(`/pub/dm/${user1}/${user2}`, res => {
        const receivedMessage = JSON.parse(res.body) as DmChatMessage;
        setMessages(prevMessages => [
          ...prevMessages,
          {
            message: receivedMessage.message,
            userId: receivedMessage.userId,
            user: {
              userId: receivedMessage.user.userId,
              nickname: receivedMessage.user.nickname,
            },
            createdAt: `${new Date().getHours()}:${String(new Date().getMinutes()).padStart(2, "0")}`,
          },
        ]);
      });
    });
  };

  useEffect(() => {
    connectToWebSocket();
    return () => {
      client.current?.disconnect();
    };
  }, [user1, user2]);
  // 엔터 누르면 메세지 전송
  const sendMessageHandler = () => {
    client.current.send(
      `/sub/dm/${user1}/${user2}`,
      {},
      JSON.stringify({ message: message, senderId: userId, nickname: "닉네임" })
    );

    setMessage("");
    scroll();
  };

  const navigate = useNavigate();
  const rapperDiv = useRef<HTMLInputElement>(null);

  // 채팅방 입장시 채팅 메시지 가져오기
  useEffect(() => {
    if (parseInt(user1) > parseInt(user2)) {
      user1 = receiverId;
      user2 = senderId;
    }
    callApi<DmMessages>("get", `/api/dm/message/${user1}/${user2}`)
      .then(res => {
        setUsers([res.data.user1, res.data.user2]);
        setChatRoomName(
          String(res.data.user1.userId) === localStorage.getItem("myId")
            ? res.data.user2.nickname
            : res.data.user1.nickname
        );
        setMessages(res.data.messages ?? []);
      })
      .catch(() => undefined);
  }, []);

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

  const outChatHandler = () => {
    client.current.send(
      `/sub/dm/${user1}/${user2}/leave`,
      {},
      JSON.stringify({
        leaveUserId: localStorage.getItem("myId"),
      })
    );
    navigate("/chatList");
  };

  return {
    messages, ismodal, chatRoomName, userId, users, message, messageHandler, sendMessageHandler,
    navigate, rapperDiv, OutRoomHandler, chaterInfoHandler, outChatHandler,
  };
};

export type DirectChatState = ReturnType<typeof useDirectChat>;

export default useDirectChat;
