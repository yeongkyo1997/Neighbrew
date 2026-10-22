// 술 후기 상세: 후기 본문, 좋아요, 팔로우, 댓글, 수정·삭제 메뉴
import NavbarSimple from "../../navbar/NavbarSimple";
import useDrinkpostReviewDetail from "./useDrinkpostReviewDetail";
import { WholeDiv } from "./DrinkpostReviewDetail.styles";
import ReviewAuthorCard from "./ReviewAuthorCard";
import ReviewBody from "./ReviewBody";
import CommentInputBar from "./CommentInputBar";
import CommentList from "./CommentList";
import ReviewMenuModals from "./ReviewMenuModals";

const DrinkpostReviewDetail = () => {
  const state = useDrinkpostReviewDetail();
  const { drink } = state;
  return (
    <>
      <NavbarSimple title={drink?.name}></NavbarSimple>
      <WholeDiv>
        <ReviewAuthorCard {...state} />
        <ReviewBody {...state} />
        <CommentInputBar {...state} />
        <CommentList {...state} />
      </WholeDiv>
      <ReviewMenuModals {...state} />
    </>
  );
};
export default DrinkpostReviewDetail;
