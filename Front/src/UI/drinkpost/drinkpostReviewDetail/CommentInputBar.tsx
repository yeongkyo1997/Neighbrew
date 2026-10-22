// 댓글 입력창과 전송 버튼
import sendImage from "../../../assets/send.png";
import {
  StyleAutoTextArea,
  CommentBox,
  CommentButton,
  SendImg,
} from "./DrinkpostReviewDetail.styles";
import { DrinkpostReviewDetailState } from "./useDrinkpostReviewDetail";

const CommentInputBar = ({
  comment,
  setComment,
  submitHandler,
}: Pick<
  DrinkpostReviewDetailState,
  | "comment"
  | "setComment"
  | "submitHandler"
>) => {
  return (
    <CommentBox>
      <StyleAutoTextArea
        style={{ fontFamily: "NanumSquareNeo", resize: "none" }}
        placeholder="댓글을 작성해주세요..."
        value={comment}
        onChange={e => {
          setComment(e.target.value);
        }}
        minRows={1}
        maxRows={4}
      />
      <CommentButton
        onClick={() => {
          void submitHandler();
        }}
      >
        <SendImg src={sendImage} alt="" />
      </CommentButton>
    </CommentBox>
  );
};

export default CommentInputBar;
