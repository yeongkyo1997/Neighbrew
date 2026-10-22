// 내 메시지와 상대 메시지 목록
import {
  OtherChat,
  MyChat,
  ChatOtherBox,
  ChatMyBox,
  ChatMyMsg,
  ChatOtherMsg,
  ChatTime,
  ChatUserName,
} from "./DirectChat.styles";
import { DirectChatState } from "./useDirectChat";

const DmMessageList = ({
  messages,
  ismodal,
  userId,
}: Pick<
  DirectChatState,
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
              alignItems: message.user?.userId == userId ? "flex-end" : "flex-start",
              flexDirection: "column",
            }}
            key={i}
          >
            {message.user?.userId === userId ? (
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
                  {message.user?.nickname.includes("@")
                    ? message.user.nickname.split("@")[0]
                    : message.user.nickname}
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

export default DmMessageList;
