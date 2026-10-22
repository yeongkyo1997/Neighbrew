import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { Drink, Review, Page } from "../../Type/types";
import { forwardIcon } from "../../assets/AllIcon";
import totaldrink from "../../assets/clickTotal.png";
import { callApi } from "../../utils/api";
import ReviewItem from "../components/ReviewItem";
import Footer from "../footer/Footer";
import Navbar from "../navbar/NavbarForDrinkpost";
import SlideComponent from "../components/Banner";
import useEffectEvent from "../../hooks/useEffectEvent";



const Total = styled.div`
  width: 100%;
  height: 120px;
  margin: 0px 0px 0px 0px;
  background-image: url(${totaldrink});
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  border-radius: 10px;
  color: white;
  display: flex;
  justify-content: end;
  align-items: end;
`;

const TotalDiv = styled.div`
  margin-bottom: 5%;
  margin-right: 5%;
`;

const TotalTitle = styled.div`
  display: flex;
  align-items: center;
  font-size: 30px;
  font-family: "NanumSquareNeoExtraBold";
`;

const TotalSubTitle = styled.div`
  font-size: 16px;
  font-family: "NanumSquareNeo";
  margin-bottom: 0.5rem;
  text-align: left;
  padding-left: 0.3rem;
`;

// 한줄에 두개씩 보여주기
// 화면 비율 맞추기
const ReviewList = styled.div`
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin: 0px 10px 100px 10px;
`;

// 리뷰 카드
// 한줄에 두개씩 보여주기

// 리뷰 이미지

const SortButton = styled.div`
  width: 5rem;
  height: 2rem;
  border-radius: 16px;
  background-color: var(--c-yellow);
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: "NanumSquareNeo";
  color: white;
`;

const DrinkpostMain = (props: { connectHandler: () => void }) => {
  const toForward = forwardIcon();
  const [reviewList, setReviewList] = useState<Review[]>([]);
  const [reviewListLike, setReviewListLike] = useState<Review[]>([]);
  const [reviewButton, setReviewButton] = useState(true);
  const navigate = useNavigate();
  const clickTotalDrink = () => {
    navigate("/drinkpost/total");
  };
  const toDrinkSearch = () => {
    navigate("/drinkpost/search");
  };
  const [, setThreePick] = useState<Drink[]>([]);

  const reviewButtonHandler = () => {
    setReviewButton(!reviewButton);
  };

  const connectHandler = useEffectEvent(() => props.connectHandler());
  useEffect(() => {
    // 놀랍지만 생성순
    void callApi<Page<Review>>("get", "api/drinkreview/likes").then(res => {
      setReviewList(res.data.content);
    });
    void callApi<Page<Review>>("get", "api/drinkreview/makes").then(res => setReviewListLike(res.data.content));
    void callApi<Drink[]>("get", "api/drink/mdPick").then(res => {
      setThreePick([...res.data]);
    });
    connectHandler();
  }, [connectHandler]);

  return (
    <>
      <div>
        <header>
          <Navbar toDrinkSearch={toDrinkSearch}></Navbar>
        </header>
        <div style={{ display: "flex", justifyContent: "center" }}>
          <SlideComponent></SlideComponent>
        </div>

        <div style={{ margin: "4rem 10px" }} onClick={clickTotalDrink}>
          <Total>
            <TotalDiv>
              <TotalSubTitle>다양한 술을 한 눈에!</TotalSubTitle>
              <TotalTitle>
                <div style={{ marginRight: "0.5rem" }}>모든 술 보기</div> {toForward}
              </TotalTitle>
            </TotalDiv>
          </Total>
        </div>
        <div style={{ margin: "0px 3vw" }}>
          <div style={{ textAlign: "start", display: "flex", alignItems: "center" }}>
            {reviewButton ? (
              <h3 style={{ width: "50%" }}>🔥인기 있는 후기🔥</h3>
            ) : (
              <h3 style={{ width: "50%" }}>날짜순 정렬</h3>
            )}
            {reviewButton ? (
              <SortButton onClick={reviewButtonHandler}>
                <b>생성순</b>
              </SortButton>
            ) : (
              <SortButton onClick={reviewButtonHandler}>
                <b>인기순</b>
              </SortButton>
            )}
          </div>
          {reviewButton ? (
            <ReviewList>
              {Array.isArray(reviewList) &&
                reviewList.map(review => {
                  return <ReviewItem key={review.drinkReviewId} review={review}></ReviewItem>;
                })}
            </ReviewList>
          ) : (
            <ReviewList>
              {Array.isArray(reviewListLike) &&
                reviewListLike.map(review => {
                  return <ReviewItem key={review.drinkReviewId} review={review}></ReviewItem>;
                })}
            </ReviewList>
          )}
          {/* <ReviewList>
            {Array.isArray(reviewList) &&
              reviewList.map(review => {
                return <ReviewItem key={review.drinkReviewId} review={review}></ReviewItem>;
              })}
          </ReviewList>
          <ReviewList>
            {Array.isArray(reviewListLike) &&
              reviewListLike.map(review => {
                return <ReviewItem key={review.drinkReviewId} review={review}></ReviewItem>;
              })}
          </ReviewList> */}
        </div>
        <footer>
          <Footer />
        </footer>
      </div>
    </>
  );
};
export default DrinkpostMain;
