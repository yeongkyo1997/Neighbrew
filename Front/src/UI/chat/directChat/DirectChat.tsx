// 1:1 채팅(DM): 메시지 목록, 참여자 서랍, 입력창 (STOMP로 송수신)
import useDirectChat from "./useDirectChat";
import DmHeader from "./DmHeader";
import DmParticipantDrawer from "./DmParticipantDrawer";
import DmMessageList from "./DmMessageList";
import DmInputBar from "./DmInputBar";

const DirectChat = () => {
  const state = useDirectChat();
  const { rapperDiv } = state;
  return (
    <div ref={rapperDiv}>
      <DmHeader {...state} />
      <DmParticipantDrawer {...state} />
      <DmMessageList {...state} />
      <DmInputBar {...state} />
    </div>
  );
};
export default DirectChat;
