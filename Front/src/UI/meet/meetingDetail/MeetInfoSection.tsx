// 장소·시간 카드, 간수치·나이 제한, 마실 술, 모임 소개
import ListInfoItem from "../../components/ListInfoItem";
import { encodeUrl, getTagName } from "../../common";
import { formateDate, formateTime } from "../DateTimeCommon";
import { MeetPosDateDiv, SubTitle, Description } from "./MeetingDetail.styles";
import { MeetingDetailState } from "./useMeetingDetail";

const MeetInfoSection = ({
  meetDetailData,
  hasAgeLimit,
  GotoDrinkPostHandler,
}: Pick<
  MeetingDetailState,
  | "meetDetailData"
  | "hasAgeLimit"
  | "GotoDrinkPostHandler"
>) => {
  return (
    <>
      <MeetPosDateDiv>
        <div>
          <img src="/src/assets/mapPinColor.svg" width="20rem" />
          <div>{`${meetDetailData.meet.sido.sidoName}`}</div>
          <div>{`${meetDetailData.meet.gugun.gugunName}`}</div>
        </div>
        <div>
          <img src="/src/assets/calendarColor.svg" width="20rem" />
          <div>{formateDate(meetDetailData.meet.meetDate)}</div>
          <div>{formateTime(meetDetailData.meet.meetDate)}</div>
        </div>
      </MeetPosDateDiv>
      <div
        style={{
          display: "flex",
          justifyContent: "space-evenly",
          fontSize: "14px",
          fontFamily: "NanumSquareNeo",
        }}
      >
        {(meetDetailData.meet.minLiverPoint ?? 0) > 0 && (
          <div style={{ display: "flex", alignItems: "center" }}>
            <img src="/src/assets/liverIcon.svg" width="20rem" style={{ marginRight: "3px" }} />
            <div>{meetDetailData.meet.minLiverPoint}</div>
            IU/L
          </div>
        )}
        {hasAgeLimit() && (
          <div style={{ display: "flex", alignItems: "center" }}>
            <img src="/src/assets/age.svg" width="20rem" style={{ marginRight: "3px" }} />
            {meetDetailData.meet.minAge > 0 && <div>{meetDetailData.meet.minAge}세 이상</div>}
            {meetDetailData.meet.maxAge > 0 && <div>~{meetDetailData.meet.maxAge}세 미만</div>}
          </div>
        )}
      </div>
      <SubTitle>우리가 마실 것은</SubTitle>
      <ListInfoItem
        title={meetDetailData.meet.drink.name}
        imgSrc={
          meetDetailData.meet.drink.image === "no image"
            ? "/src/assets/whiskeyImage.png"
            : encodeUrl(meetDetailData.meet.drink.image)
        }
        tag={getTagName(meetDetailData.meet.drink.tagId)}
        content={meetDetailData.meet.drink.description}
        outLine={false}
        isDrink={true}
        routingFunc={() => GotoDrinkPostHandler(meetDetailData.meet.drink.drinkId)}
      />
      <SubTitle>모임 소개</SubTitle>
      <Description>{meetDetailData.meet.description}</Description>
    </>
  );
};

export default MeetInfoSection;
