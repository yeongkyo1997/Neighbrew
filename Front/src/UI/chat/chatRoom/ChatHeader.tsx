// 뒤로가기, 채팅방 이름과 인원, 참여자 서랍 버튼
import { arrowLeftIcon, outRoom } from "../../../assets/AllIcon";
import { ChatNav } from "./ChatRoom.styles";
import { ChatRoomState } from "./useChatRoom";

const ChatHeader = ({
  chatRoomName,
  users,
  OutRoomHandler,
  chaterInfoHandler,
}: Pick<
  ChatRoomState,
  | "chatRoomName"
  | "users"
  | "OutRoomHandler"
  | "chaterInfoHandler"
>) => {
  const ArrowLeftIcon = arrowLeftIcon("black");
  const OutRoom = outRoom();
  return (
    <header>
      <ChatNav>
        <div
          style={{
            marginRight: "0rem",
            marginLeft: "0rem",
            textAlign: "center",
            cursor: "pointer",
          }}
          onClick={OutRoomHandler}
        >
          {ArrowLeftIcon}
        </div>
        <span
          style={{
            marginRight: "0rem",
            fontFamily: "JejuGothic",
            fontSize: "20px",
            whiteSpace: "nowrap",
            overflow: "hidden",
            textOverflow: "ellipsis",
            maxWidth: "80%",
          }}
        >
          <>
            {chatRoomName ?? null}
            <span style={{ fontSize: "14px", color: "var(--c-gray)" }}>
              &nbsp;&nbsp;&nbsp;&nbsp;{users.length}
            </span>
          </>
        </span>
        <div
          style={{ marginLeft: "0rem", marginTop: "3px", cursor: "pointer" }}
          onClick={chaterInfoHandler}
        >
          {OutRoom}
        </div>
      </ChatNav>
    </header>
  );
};

export default ChatHeader;
