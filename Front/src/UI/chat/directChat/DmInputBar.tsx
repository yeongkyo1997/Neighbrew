// 메시지 입력창과 전송 버튼
import sendImage from "../../../assets/send.png";
import { StyleAutoTextArea, SendBtnDiv, SendImg, InputDiv } from "./DirectChat.styles";
import { DirectChatState } from "./useDirectChat";

const DmInputBar = ({
  message,
  messageHandler,
  sendMessageHandler,
}: Pick<
  DirectChatState,
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
              <SendImg src={sendImage} alt="" />
            </div>
          ) : (
            <div onClick={sendMessageHandler} style={{ visibility: "hidden" }}>
              <SendImg src={sendImage} alt="" />
            </div>
          )}
        </SendBtnDiv>
      </InputDiv>
    </footer>
  );
};

export default DmInputBar;
