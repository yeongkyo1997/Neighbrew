// main
import { styled } from "styled-components";
import DrinkCard from "../drinkpost/DrinkCard";
import { useState, useEffect } from "react";

import Footer from "../footer/Footer";
import { useNavigate, useParams } from "react-router-dom";
import { callApi } from "../../utils/api";
import { Drink } from "../../Type/types";
import EmptyMsg from "../components/EmptyMsg.tsx";

const ShowcaseBody = styled.div`
  font-size: 14px;
  margin-left: 1vw;
`;

const RoundBtn = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  bottom: 10%;

  background: var(--c-yellow);
  width: 4rem;
  height: 4rem;
  border-radius: 100px;
  z-index: 10;

  @media (max-width: 430px) {
    right: 5%;
  }
  @media (min-width: 431px) {
    left: 350px;
  }
`;

const DrinkPostUseInUser = () => {
  const { userid } = useParams();
  const [drinkList, setDrinkList] = useState<Drink[]>([]);
  const navigate = useNavigate();
  // const navigate = useNavigate();
  // const userid = localStorage.getItem("myId");
  const toDrinkSearch = () => {
    navigate("/drinkpost/search");
  };
  const myDrinkHandler = () => {
    console.log(userid);
    callApi("get", `api/drink/user/${userid}/review-drink`).then(res => {
      setDrinkList(res.data);
    });
  };
  useEffect(() => {
    console.log(userid);
    myDrinkHandler();
  }, []);
  return (
    <>
      <ShowcaseBody>
        <div style={{ textAlign: "start" }}></div>
        {drinkList.length === 0 ? (
          <EmptyMsg
            title="술장이 비었습니다.🍾"
            contents="다양한 주종을 즐기고 후기를 남겨보세요!"
          />
        ) : (
          <div
            className="whole"
            style={{ display: "flex", flexWrap: "wrap", paddingBottom: "60px", marginLeft: "1px" }}
          >
            {drinkList.map(drink => (
              <DrinkCard key={drink.drinkId} drink={drink}></DrinkCard>
            ))}
          </div>
        )}

        <div
          style={{
            marginTop: "100px",
            height: "5px",
            backgroundColor: "--c-black",
          }}
        ></div>
      </ShowcaseBody>
      <Footer></Footer>
    </>
  );
};

export default DrinkPostUseInUser;
