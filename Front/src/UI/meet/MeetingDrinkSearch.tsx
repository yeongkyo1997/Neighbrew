import React, { useState, useRef, useEffect } from "react";
import autoAnimate from "@formkit/auto-animate";
import styled from "styled-components";
import SearchBox from "../components/SearchBox";
import DrinkCategory from "../drinkCategory/DrinkCategory";
import OneLineListItem from "../components/OneLineListItem";
import ListInfoItem from "../components/ListInfoItem";
import { Drink } from "../../Type/types";
import { getTagName, encodeUrl, initialDrink } from "../common";
import { callApi } from "../../utils/api";
import EmptyMsg from "../components/EmptyMsg";
import useIntersectionObserver from "../../hooks/useIntersectionObserver";

const Title = styled.div`
  font-family: "JejuGothic";
  font-size: 20px;
  text-align: left;
  margin-bottom: 0.5rem;
`;

const SubTitle = styled.div`
  font-family: "NanumSquareNeo";
  font-size: 14px;
  text-align: left;
`;

const CateDiv = styled.div`
  height: 10rem;
  div {
    margin: 0;
  }
  .first,
  .second {
    display: flex;
    justify-content: space-around;
    margin-top: 0.5rem;
  }
`;

const SearchResultDiv = styled.div`
  border-radius: 15px;
  border: 1px solid var(--c-gray);
  height: 12rem;
  margin-top: 0.5rem;
  display: flex;
  flex-direction: column;
`;

const CloseDiv = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  position: sticky;
  bottom: 0;
  height: 3rem;
  z-index: 3;
  padding: 0 1rem;
  font-family: "NanumSquareNeo";
`;

const ReselectBtn = styled.div`
  background: var(--c-lightgray);
  border-radius: 10px;
  width: 3rem;
  font-family: "NanumSquareNeo";
  font-size: 15px;
  padding: 0.5rem;
  margin: 0.5rem 0 0 auto;
`;

const ErrorDiv = styled.div`
  color: red;
  text-align: left;
  font-family: "NanumSquareNeo";
  font-size: 15px;
  padding: 0.5rem;
`;

const CustomInitialDrink: Drink = {
  ...initialDrink,
  drinkId: -1,
};

type MeetingDrinkSearchProps = {
  tagId: number;
  setTagIdFunc(tagId: number): void;
  drink: Drink;
  setDrinkFunc(drink: Drink): void;
  btnClicked: boolean;
  isModify?: boolean;
};

const MeetingDrinkSearch = (props: MeetingDrinkSearchProps) => {
  const [tag, setTag] = useState(0);
  const [drink, setDrink] = useState(initialDrink);
  const [meetTag, setMeetTag] = useState(0);
  const [meetDrink, setMeetDrink] = useState(initialDrink);

  //주종 카테고리 선택
  const getDrinkCategory = (tagId: number) => {
    setTag(tagId);
  };
  //검색 후 선택한 주류 정보, 즉 모임에 설정할 술 정보받아오기
  const getDrink = (drink: Drink) => {
    setDrink(drink);
    setIsSearchFocused(false);
  };

  //검색 관련 state
  const [inputText, setInputText] = useState(""); //검색창에 입력된 텍스트
  const [searchResultList, setSearchResultList] = useState<Drink[]>([]); //주류 검색 결과 리스트
  const [fetchDone, setFetchDone] = useState(false);

  //무한스크롤 용
  const [page, setPage] = useState(0); //페이징용, 0에서 시작
  const [totalPage, setTotalPage] = useState(0); //페이징용, 예정된 전체 페이지 수
  const [throttle, setThrottle] = useState(false);

  //검색 결과 창 애니메이션 용
  const [isSearchFocused, setIsSearchFocused] = useState(false);
  const parent = useRef(null);
  useEffect(() => {
    parent.current && autoAnimate(parent.current);
  }, [parent]);

  useEffect(() => {
    //초기 데이터 넘어옴(meetTag 설정 전)
    if (meetTag === 0) {
      setMeetTag(props.tagId);
      setMeetDrink(props.drink);
      setTag(props.tagId);
      setDrink(props.drink);
    }
  }, [props]);

  useEffect(() => {
    if (
      !(props.isModify ?? true) ||
      (meetTag !== 0 &&
        meetDrink.drinkId !== 0 &&
        meetTag === tag &&
        meetDrink.drinkId === drink.drinkId)
    ) {
      setFetchDone(true);
    }
  }, [meetTag, meetDrink]);

  //무한 스크롤 로직
  const onIntersect: IntersectionObserverCallback = ([{ isIntersecting }]) => {
    // isIntersecting이 true면 감지했다는 뜻임
    if (isIntersecting && !throttle) {
      setThrottle(true);
      setTimeout(() => {
        setPage(prev => prev + 1);
        setThrottle(false);
      }, 300);
    }
  };

  const { setTarget } = useIntersectionObserver({ onIntersect });

  //inputText로 술장 검색 api
  useEffect(() => {
    if (page === 0) {
      const promise = callApi(
        "get",
        `api/drink/search?tagId=${tag}&name=${inputText}&page=${page}&size=10`
      );
      void promise.then(res => {
        setTotalPage(res.data.totalPages);
        setSearchResultList(res.data.content); //받아온 데이터로 리스트 초기화
      });
    } else {
      setPage(0);
      setTotalPage(1);
    }
  }, [inputText, tag]);

  //페이지가 변하면 기존 데이터에 이어서 로드
  useEffect(() => {
    const promise = callApi(
      "get",
      `api/drink/search?tagId=${tag}&name=${inputText}&page=${page}&size=10`
    );
    if (page === 0) {
      void promise.then(res => {
        setTotalPage(res.data.totalPages);
        setSearchResultList(res.data.content); //받아온 데이터로 리스트 초기화
      });
    } else {
      void promise.then(res => {
        setTotalPage(res.data.totalPages);
        setSearchResultList(prev => [...prev, ...res.data.content]); //받아온 데이터 meetAllData에 추가
      });
    }
  }, [page]);

  useEffect(() => {
    //주류 검색 결과 및 조건 초기화
    if (fetchDone) {
      setDrink(CustomInitialDrink);
      setInputText("");
      props.setTagIdFunc(tag); //부모에게 전달
    }
  }, [tag]);

  //부모에게 전달
  useEffect(() => {
    if (fetchDone) {
      props.setDrinkFunc(drink);
    }
  }, [drink]);

  return (
    <div>
      <Title>우리가 마실 것은*</Title>
      <SubTitle>카테고리를 선택해주세요</SubTitle>
      <CateDiv>
        {tag !== 0 && (
          <DrinkCategory key={tag} getFunc={getDrinkCategory} selectedId={tag} isSearch={false} />
        )}
      </CateDiv>
      <div ref={parent}>
        {drink.drinkId < 1 && (
          <div>
            <SubTitle style={{ marginBottom: "0.3rem" }}>
              정확한 술의 이름을 검색할 수 있어요
            </SubTitle>
            <div onFocus={() => setIsSearchFocused(true)}>
              <SearchBox
                placeholder=""
                changeFunc={(inputTxt: string) => {
                  setInputText(inputTxt);
                }}
                width={90}
              />
            </div>
            {!isSearchFocused && props.btnClicked && (
              <ErrorDiv>📌한 가지의 주류를 필수적으로 입력해야합니다.</ErrorDiv>
            )}
            {isSearchFocused && (
              <SearchResultDiv>
                <div
                  style={{
                    overflow: "auto",
                    height: "100%",
                    flexGrow: "1",
                  }}
                >
                  {searchResultList.length === 0 && (
                    <EmptyMsg
                      title="검색 결과가 없습니다"
                      contents={`아직 네이브루에 등록되지 않았나봐요\n술장에서 직접 등록해보시는 건 어떤가요?`}
                    />
                  )}
                  {searchResultList.length > 0 && (
                    <div key={searchResultList[0].drinkId}>
                      {searchResultList.map(res => (
                        <div onClick={() => getDrink(res)} key={res.drinkId}>
                          <OneLineListItem
                            content={res.name}
                            tag={getTagName(res.tagId)}
                          ></OneLineListItem>
                        </div>
                      ))}
                    </div>
                  )}
                  {!throttle && page < totalPage && (
                    <div
                      ref={setTarget}
                      style={{
                        height: "1px",
                      }}
                    ></div>
                  )}
                </div>
                <CloseDiv onClick={() => setIsSearchFocused(false)}>▲닫기</CloseDiv>
              </SearchResultDiv>
            )}
          </div>
        )}
        {drink.drinkId > 0 && (
          <div>
            <ListInfoItem
              title={drink.name}
              imgSrc={
                drink.image === "no image" ? "/src/assets/whiskeyImage.png" : encodeUrl(drink.image)
              }
              tag={getTagName(drink.tagId)}
              content={drink.description}
              isWaiting={false}
              outLine={true}
              isDrink={true}
              routingFunc={() => {}} //이동하지않도록
            />
            <ReselectBtn
              onClick={() => {
                setDrink(CustomInitialDrink);
              }}
            >
              재선택
            </ReselectBtn>
          </div>
        )}
      </div>
    </div>
  );
};

export default React.memo(MeetingDrinkSearch);
