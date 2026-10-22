// DrinkpostReviewDetail의 상태, API 호출, 이벤트 처리
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Drink, Review, SubReview, FollowRelation } from "../../../Type/types";
import { callApi } from "../../../utils/api";

const useDrinkpostReviewDetail = () => {
  const { drinkId, reviewId } = useParams();
  const [showMore, setShowMore] = useState(false);
  const [, setFollowRejection] = useState(false);
  const [deleteModalOn, setDeleteModalOn] = useState(false);
  const [threeDotOn, setThreeDotOn] = useState(false);
  const [review, setReview] = useState<Review>();
  const [drink, setDrink] = useState<Drink>();
  const [following, setFollowing] = useState(0);
  const [subReviewList, setSubReviewList] = useState<SubReview[]>([]);
  const [comment, setComment] = useState("");
  const navigate = useNavigate();
  const [like, setLike] = useState(false);
  const [likeCount, setLikeCount] = useState(0);

  const toggleShowMore = () => {
    setShowMore(!showMore);
  };
  useEffect(() => {
    void callApi<Drink>("get", `api/drink/${drinkId}`).then(res => {
      setDrink(res.data);
    });

    void callApi<SubReview[]>("get", `api/subreview/list/${reviewId}`).then(res => {
      setSubReviewList(res.data);
    });
  }, []);

  useEffect(() => {
    async function summonReview() {
      // 술 상세 후기 조회 요청
      const response1 = await callApi<Review>("get", `api/drinkreview/review/${reviewId}`);
      setReview(response1.data);
      setLikeCount(response1.data.likeCount);
      const userId = response1.data.user.userId;
      // 후기 쓴 사람에 대한 follow 요청
      // 술 상세 후기 조회 이후에 이루어져야 함.
      const response2 = await callApi<FollowRelation[]>("get", `api/follow/follower/${userId}`);
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

    void summonReview();
  }, []);

  useEffect(() => {
    void callApi<boolean>("GET", `api/like/${reviewId}`, {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    }).then(res => {
      setLike(res.data);
    });
  }, [localStorage.getItem("token")]);

  const likeHandler = () => {
    void callApi("POST", `api/like/${reviewId}`, {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    }).then(() => {
      if (!like) {
        setLikeCount(prev => prev + 1);
      } else {
        setLikeCount(prev => prev - 1);
      }
    });
    setLike(!like);
  };

  const followHandler = () => {
    if (review?.user.userId.toString() === localStorage.getItem("myId")) {
      setFollowRejection(true);
      setTimeout(() => setFollowRejection(false), 1000);
      return;
    }
    callApi("post", `api/follow/${review?.user.userId}`)
      .then(() => {
        followers();
      })
      .catch(() => undefined);
  };

  const followers = () => {
    void callApi<FollowRelation[]>("get", `api/follow/follower/${review?.user.userId}`).then(res => {
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


  // 술 후기에 대한 댓글 제출하는 함수.
  const submitHandler = async () => {
    const fun = await callApi<SubReview>("post", "api/subreview/write", {
      content: comment.trim(),
      drinkReviewId: reviewId,
    });

    setComment("");
    setSubReviewList(prev => [fun.data, ...prev]);
  };

  const modalHandler = () => {
    setThreeDotOn(true);
  };

  const toDeleteQuestionHandler = () => {
    setDeleteModalOn(true);
    setThreeDotOn(false);
  };

  const toUpdateHandler = () => {
    navigate("update/");
  };

  const deleteHandler = () => {
    void callApi("delete", `api/drinkreview/${review?.drinkReviewId}`).then(() => {
      navigate(-1);
    });
  };

  return {
    showMore, deleteModalOn, setDeleteModalOn, threeDotOn, setThreeDotOn, review, drink, following,
    subReviewList, comment, setComment, like, likeCount, toggleShowMore, likeHandler,
    followHandler, toProfileHandler, submitHandler, modalHandler, toDeleteQuestionHandler,
    toUpdateHandler, deleteHandler,
  };
};

export type DrinkpostReviewDetailState = ReturnType<typeof useDrinkpostReviewDetail>;

export default useDrinkpostReviewDetail;
