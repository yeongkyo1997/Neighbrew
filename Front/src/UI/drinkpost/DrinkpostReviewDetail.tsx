import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import TextareaAutosize from "react-textarea-autosize";
import styled from "styled-components";
import { Drink, Review, SubReview } from "../../Type/types";
import defaultImg from "../../assets/defaultImg.png";
import fancyDrinkImage from "../../assets/fancydrinkImage.jpg";
import { callApi } from "../../utils/api";
import NavbarSimple from "../navbar/NavbarSimple";
import { commentIcon, likeIcon } from "./../../assets/AllIcon";
import sendImage from "./../../assets/send.png";
import CommentItem from "./../components/CommentItem";

const StyleAutoTextArea = styled(TextareaAutosize)`
  display: flex;
  flex-basis: 90%;
  border: 0.5px solid #dfdfdf;
  background-color: #eeeeee;
  border-radius: 5px;
  margin: 0.5rem 0 0.5rem 0.5rem;
  padding: 0.3rem;
  overflow-y: auto;
  outline: none;

  // 글을 아래에 배치
  align-self: flex-end;
  font-size: 1rem;

  &:focus {
    border: none;
  }
`;
const LikeAndComment = styled.div`
  margin: 0.5rem;
  display: flex;
  justify-content: left;
  width: 36%;
  margin-top: 1.5vh;
  font-size: 20px;
`;

const Description = styled.div`
  font-family: "NanumSquareNeo";
  text-align: start;
  margin: 0.5rem;
  white-space: pre-wrap;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
  &.show {
    display: block;
    max-height: none;
    overflow: auto;
    -webkit-line-clamp: unset;
  }
`;

const WholeDiv = styled.div``;

const ImageDiv = styled.div`
  background-color: var(--c-lightgray);
  background-repeat: no-repeat;
  background-size: cover;
  width: 100%;
`;

const Usercard = styled.div`
  display: flex;
  align-items: center;
  margin: 0.5rem;
  justify-content: space-between;
`;

const FollowDiv = styled.div`
  width: 5rem;
  height: 2rem;
  border-radius: 20px;
  font-family: JejuGothic;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const UserImg = styled.img`
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  margin-right: 1rem;
`;

const CommentBox = styled.div`
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  background-color: #fff;
  display: flex;
  flex-direction: row;
  width: 100%;
  border-top: 0.5px solid #dfdfdf;
`;

const CommentButton = styled.button`
  background-color: var(--c-blue);
  border: none;
  border-radius: 5px;
  flex-basis: 10%;
`;

const SendImg = styled.img`
  width: 23px;
  height: 23px;
`;

const SubReviewList = styled.div`
  padding-bottom: 4.5rem;
  margin: 0.5rem;
`;

const LikeAndCommentDiv = styled.div`
  display: flex;
  flex-direction: row;
  margin-right: 4vw;
`;

const InfoBox = styled.div`
  display: flex;
  justify-content: space-between;
`;

const DrinkpostReviewDetail = () => {
  const LikeIcon = likeIcon();
  const CommentIcon = commentIcon();
  const { drinkId, reviewId } = useParams();
  const [review, setReview] = useState<Review>();
  const [drink, setDrink] = useState<Drink>();
  const [following, setFollowing] = useState(0);
  const [subReviewList, setSubReviewList] = useState<SubReview[]>([]);
  const [comment, setComment] = useState("");
  const navigate = useNavigate();
  const [like, setLike] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  useEffect(() => {
    callApi("get", `api/drink/${drinkId}`).then((res) => {
      setDrink(res.data);
    });

    callApi("get", `api/subreview/list/${reviewId}`).then((res) => {
      setSubReviewList(res.data);
    });

    async function summonReview() {
      // 술 상세 후기 조회 요청
      const response1 = await callApi(
        "get",
        `api/drinkreview/review/${reviewId}`
      );
      setReview(response1.data);
      setLikeCount(response1.data.likeCount);
      const userId = response1.data.user.userId;
      // 후기 쓴 사람에 대한 follow 요청
      // 술 상세 후기 조회 이후에 이루어져야 함.
      const response2 = await callApi("get", `api/follow/follower/${userId}`);
      if (response2.data.length == 0) {
        setFollowing(0);
        return;
      }
      response2.data.map((item, i) => {
        if (item.follower.userId == parseInt(localStorage.getItem("myId"))) {
          setFollowing(1);
          return;
        } else if (i == response2.data.length - 1) {
          setFollowing(0);
        }
      });
    }
    summonReview();
  }, []);

  useEffect(() => {
    callApi("GET", `api/like/${reviewId}`, {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    }).then((res) => {
      setLike(res.data);
    });
  }, [localStorage.getItem("token")]);

  const likeHandler = () => {
    callApi("POST", `api/like/${reviewId}`, {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    }).then(() => {
      if (!like) {
        setLikeCount((prev) => prev + 1);
      } else {
        setLikeCount((prev) => prev - 1);
      }
    });
    setLike(!like);
  };

  const followHandler = () => {
    callApi("post", `api/follow/${review?.user.userId}`)
      .then(() => {
        followers();
      })
      .catch(() => {});
  };

  const followers = async () => {
    callApi("get", `api/follow/follower/${review?.user.userId}`).then((res) => {
      if (res.data.length == 0) {
        setFollowing(0);
        return;
      }

      res.data.map((item, i) => {
        if (item.follower.userId == parseInt(localStorage.getItem("myId"))) {
          setFollowing(1);
          return;
        } else if (i == res.data.length - 1) {
          setFollowing(0);
        }
      });
    });
  };

  const toProfileHandler = () => {
    navigate(`/myPage/${review?.user.userId}`);
  };

  useEffect(() => {}, [comment]);

  // 술 후기에 대한 댓글 제출하는 함수.
  const submitHandler = async () => {
    const fun = await callApi("post", "api/subreview/write", {
      content: comment.trim(),
      drinkReviewId: reviewId,
    });

    setComment("");
    setSubReviewList((prev) => [fun.data, ...prev]);
  };

  const deleteHandler = () => {
    callApi("delete", `api/drinkreview/${review?.drinkReviewId}`).then(() => {
      navigate(`/drinkpost/${drinkId}`);
    });
  };
  return (
    <>
      <NavbarSimple title={drink?.name}></NavbarSimple>
      <WholeDiv>
        <Usercard>
          <div
            onClick={toProfileHandler}
            style={{ display: "flex", alignItems: "center" }}
          >
            <div>
              <UserImg
                src={
                  review?.user.profile !== "no image"
                    ? review?.user.profile
                    : defaultImg
                }
              ></UserImg>
            </div>
            <div>
              <b>{review?.user.nickname}</b>
            </div>
          </div>
          <FollowDiv
            style={{
              backgroundColor:
                following === 0 ? "var(--c-yellow)" : "var(--c-lightgray)",
            }}
            onClick={followHandler}
          >
            {following === 0 ? "팔로우" : "언팔로우"}
          </FollowDiv>
        </Usercard>
        <ImageDiv
        // style={{
        //   backgroundImage: `url(${review?.img !== "no image" ? review?.img : fancyDrinkImage})`,
        // }}
        >
          <img
            src={review?.img !== "no image" ? review?.img : fancyDrinkImage}
            style={{ width: "100%" }}
          />
        </ImageDiv>
        <InfoBox>
          <LikeAndComment>
            <LikeAndCommentDiv>
              <div onClick={likeHandler}>{LikeIcon}</div>
              <div>{likeCount}</div>
            </LikeAndCommentDiv>
            <LikeAndCommentDiv>
              <div>{CommentIcon}</div>
              <div>{subReviewList.length}</div>
            </LikeAndCommentDiv>
          </LikeAndComment>
          {review?.user.userId.toString() === localStorage.getItem("myId") ? (
            <div
              style={{
                cursor: "pointer",
                margin: "0.5rem",
                backgroundColor: "#FF5F5F",
                color: "white",
                borderRadius: "8px",
              }}
              onClick={deleteHandler}
            >
              delete
            </div>
          ) : null}
        </InfoBox>
        <Description>{review?.content}</Description>

        <CommentBox>
          <StyleAutoTextArea
            style={{ fontFamily: "NanumSquareNeo", resize: "none" }}
            placeholder="댓글을 작성해주세요..."
            value={comment}
            onChange={(e) => {
              setComment(e.target.value);
            }}
            minRows={1}
            maxRows={4}
          />
          <CommentButton
            onClick={() => {
              submitHandler();
            }}
          >
            <SendImg src={sendImage} alt="" />
          </CommentButton>
        </CommentBox>
        <SubReviewList>
          {subReviewList.map((subReview, i) => {
            return <CommentItem key={i} subReview={subReview}></CommentItem>;
          })}
        </SubReviewList>
      </WholeDiv>
    </>
  );
};
export default DrinkpostReviewDetail;
