// 뒤로가기, 상대 이름, 참여자 서랍 버튼
import { arrowLeftIcon, outRoom } from "../../../assets/AllIcon";
import { ChatNav } from "./DirectChat.styles";
import { DirectChatState } from "./useDirectChat";

const DmHeader = ({
  chatRoomName,
  OutRoomHandler,
  chaterInfoHandler,
}: Pick<
  DirectChatState,
  | "chatRoomName"
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
            {chatRoomName}
            <span style={{ fontSize: "14px", color: "var(--c-gray)" }}>
              &nbsp;&nbsp;&nbsp;&nbsp;2
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

export default DmHeader;
