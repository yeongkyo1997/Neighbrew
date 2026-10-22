// 내 메시지와 다른 사람 메시지 목록
import {
  OtherChat,
  MyChat,
  ChatOtherBox,
  ChatMyBox,
  ChatMyMsg,
  ChatOtherMsg,
  ChatTime,
  ChatUserName,
} from "./ChatRoom.styles";
import { ChatRoomState } from "./useChatRoom";

const MessageList = ({
  messages,
  ismodal,
  userId,
}: Pick<
  ChatRoomState,
  | "messages"
  | "ismodal"
  | "userId"
>) => {
  return (
    <div
      style={{
        backgroundColor: ismodal ? "#757575" : "var(--c-lightgray)",
        width: "100%",
        minHeight: "90vh",
        overflow: "auto",
      }}
    >
      {messages.map((message, i) => {
        return (
          <div
            style={{
              display: "flex",
              alignItems:
                message.userId == userId ? "flex-end" : "flex-start",
              flexDirection: "column",
            }}
            key={i}
          >
            {message.userId === userId ? (
              <ChatMyBox>
                <ChatUserName>나</ChatUserName>
                <ChatMyMsg>
                  <ChatTime>
                    {message.createdAt.length > 5
                      ? message.createdAt.split("T")[1].substring(0, 5)
                      : message.createdAt}
                  </ChatTime>
                  <MyChat>{message.message}</MyChat>
                </ChatMyMsg>
              </ChatMyBox>
            ) : (
              <ChatOtherBox>
                <ChatUserName>
                  {message.userNickname?.includes("@")
                    ? message.userNickname.split("@")[0]
                    : message.userNickname}
                </ChatUserName>
                <ChatOtherMsg>
                  <OtherChat>{message.message}</OtherChat>
                  <ChatTime>
                    {message.createdAt.length > 5
                      ? message.createdAt.split("T")[1].substring(0, 5)
                      : message.createdAt}
                  </ChatTime>
                </ChatOtherMsg>
              </ChatOtherBox>
            )}
          </div>
        );
      })}
      <div style={{ height: "3rem" }}></div>
    </div>
  );
};

export default MessageList;
