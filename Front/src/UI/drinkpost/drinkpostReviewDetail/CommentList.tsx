// 댓글 목록
import CommentItem from "../../components/CommentItem";
import { SubReviewList } from "./DrinkpostReviewDetail.styles";
import { DrinkpostReviewDetailState } from "./useDrinkpostReviewDetail";

const CommentList = ({
  subReviewList,
}: Pick<
  DrinkpostReviewDetailState,
  | "subReviewList"
>) => {
  return (
    <SubReviewList>
      {subReviewList.map((subReview, i) => {
        return <CommentItem key={i} subReview={subReview}></CommentItem>;
      })}
    </SubReviewList>
  );
};

export default CommentList;
