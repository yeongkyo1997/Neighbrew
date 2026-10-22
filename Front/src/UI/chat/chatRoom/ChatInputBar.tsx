// 메시지 입력창과 전송 버튼
import sendImage from "../../../assets/send.png";
import { InputDiv, StyleAutoTextArea, SendBtnDiv, SendImg } from "./ChatRoom.styles";
import { ChatRoomState } from "./useChatRoom";

const ChatInputBar = ({
  message,
  messageHandler,
  sendMessageHandler,
}: Pick<
  ChatRoomState,
  | "message"
  | "messageHandler"
  | "sendMessageHandler"
>) => {
  return (
    <footer>
      <InputDiv>
        <StyleAutoTextArea
          value={message}
          onChange={messageHandler}
          minRows={1}
          maxRows={4}
        ></StyleAutoTextArea>

        <SendBtnDiv>
          {message.length > 0 ? (
            <div onClick={sendMessageHandler}>
              {/*<SendIcon></SendIcon>*/}
              <SendImg src={sendImage} alt="" />
            </div>
          ) : (
            <div
              onClick={sendMessageHandler}
              style={{ visibility: "hidden" }}
            >
              {/*<SendIcon></SendIcon>*/}
              <SendImg src={sendImage} alt="" />
            </div>
          )}
        </SendBtnDiv>
      </InputDiv>
    </footer>
  );
};

export default ChatInputBar;
