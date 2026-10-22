// 모임 채팅방: 메시지 목록, 참여자 서랍, 입력창 (STOMP로 송수신)
import useChatRoom from "./useChatRoom";
import ChatHeader from "./ChatHeader";
import ParticipantDrawer from "./ParticipantDrawer";
import MessageList from "./MessageList";
import ChatInputBar from "./ChatInputBar";

const ChatRoom = () => {
  const state = useChatRoom();
  const { rapperDiv } = state;
  return (
    <div ref={rapperDiv}>
      <ChatHeader {...state} />
      <ParticipantDrawer {...state} />
      <MessageList {...state} />
      <ChatInputBar {...state} />
    </div>
  );
};
export default ChatRoom;
