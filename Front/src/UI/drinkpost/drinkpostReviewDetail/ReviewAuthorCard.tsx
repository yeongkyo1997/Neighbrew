// 작성자 프로필과 팔로우 버튼
import defaultImg from "../../../assets/defaultImg.png";
import { Usercard, FollowDiv } from "./DrinkpostReviewDetail.styles";
import { DrinkpostReviewDetailState } from "./useDrinkpostReviewDetail";

const ReviewAuthorCard = ({
  review,
  following,
  followHandler,
  toProfileHandler,
}: Pick<
  DrinkpostReviewDetailState,
  | "review"
  | "following"
  | "followHandler"
  | "toProfileHandler"
>) => {
  return (
    <Usercard>
      <div onClick={toProfileHandler} style={{ display: "flex", alignItems: "center" }}>
        <div
          style={{
            backgroundImage: `url(${
              review?.user.profile !== "no image" ? review?.user.profile : defaultImg
            })`,
            backgroundRepeat: "no-repeat",
            backgroundPosition: "center",
            backgroundSize: "cover",
            width: "2.4rem",
            height: "2.4rem",
            borderRadius: "100px",
            marginRight: "0.5rem",
          }}
        ></div>
        <div>
          <b>{review?.user.nickname}</b>
        </div>
      </div>
      {review?.user.userId.toString() !== localStorage.getItem("myId") ? (
        <FollowDiv
          style={{
            backgroundColor: following === 0 ? "var(--c-yellow)" : "var(--c-lightgray)",
          }}
          onClick={followHandler}
        >
          {following === 0 ? "팔로우" : "언팔로우"}
        </FollowDiv>
      ) : null}
    </Usercard>
  );
};

export default ReviewAuthorCard;
