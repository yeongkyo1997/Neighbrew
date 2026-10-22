// 후기 이미지, 좋아요·댓글 수, 더보기 설명
import fancyDrinkImage from "../../../assets/fancydrinkImage.jpg";
import { commentIcon, moreIcon, likeIcon2 } from "../../../assets/AllIcon";
import {
  LikeAndComment,
  DescriptionP,
  MoreButton,
  ImageDiv,
  LikeAndCommentDiv,
  InfoBox,
} from "./DrinkpostReviewDetail.styles";
import { DrinkpostReviewDetailState } from "./useDrinkpostReviewDetail";

const ReviewBody = ({
  showMore,
  review,
  subReviewList,
  like,
  likeCount,
  toggleShowMore,
  likeHandler,
  modalHandler,
}: Pick<
  DrinkpostReviewDetailState,
  | "showMore"
  | "review"
  | "subReviewList"
  | "like"
  | "likeCount"
  | "toggleShowMore"
  | "likeHandler"
  | "modalHandler"
>) => {
  const MoreIcon = moreIcon();
  const CommentIcon = commentIcon();
  return (
    <>
      <ImageDiv>
        <img
          src={review?.img !== "no image" ? review?.img : fancyDrinkImage}
          style={{ width: "100%" }}
        />
      </ImageDiv>
      <InfoBox>
        <LikeAndComment>
          <LikeAndCommentDiv>
            <div
              onClick={likeHandler}
              style={{
                display: "flex",
                alignItems: "center",
                margin: "0.5rem",
              }}
            >
              {like ? likeIcon2("var(--c-pink)") : likeIcon2("none")}
            </div>
            <div style={{ display: "flex", alignItems: "center" }}>{likeCount}</div>
          </LikeAndCommentDiv>
          <LikeAndCommentDiv>
            <div
              style={{
                display: "flex",
                alignItems: "center",
                margin: "0.5rem",
              }}
            >
              {CommentIcon}
            </div>
            <div style={{ display: "flex", alignItems: "center" }}>{subReviewList.length}</div>
          </LikeAndCommentDiv>
        </LikeAndComment>
        {review?.user.userId.toString() === localStorage.getItem("myId") ? (
          <div
            style={{
              cursor: "pointer",
              margin: "0.5rem",
              borderRadius: "8px",
            }}
            onClick={modalHandler}
          >
            {MoreIcon}
          </div>
        ) : null}
      </InfoBox>

      <div>
        <DescriptionP className={showMore ? "show" : ""}>
          <hr />
          {review?.content}
        </DescriptionP>
        {/* 늘리기 줄이기 토글 */}
        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
          }}
        >
          <MoreButton className={showMore ? "hide" : ""} onClick={toggleShowMore}>
            더보기
          </MoreButton>
          <MoreButton className={showMore ? "" : "hide"} onClick={toggleShowMore}>
            줄이기
          </MoreButton>
        </div>
      </div>
    </>
  );
};

export default ReviewBody;
