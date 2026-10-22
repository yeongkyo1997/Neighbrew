// 모임·술장 탭과 탭 내용
import { brewery, meetingicon } from "../../../assets/AllIcon";
import DrinkpostMain from "../DrinkPostUseInUser";
import MeetingMy from "../MeetingMyUseInUser";
import { MyPageState } from "./useMyPage";
import { Button } from "./MyPage.styles";

const MyPageTabs = ({ chooseChat, setChooseChat, userid }: Pick<MyPageState, "chooseChat" | "setChooseChat" | "userid">) => {
  const MeetingIcon = meetingicon(chooseChat === 0 ? "var(--c-black)" : "#AAAAAA");
  const Brewery = brewery(chooseChat === 0 ? "#AAAAAA" : "var(--c-black)");
  return (
    <>
      <div
        style={{
          borderBottom: "1px solid var(--c-borderline)",
        }}
      >
        <Button
          onClick={() => {
            setChooseChat(0);
          }}
          style={{
            borderBottom: chooseChat === 0 ? "2px solid var(--c-black)" : "none",
          }}
        >
          {MeetingIcon}
          {/*<p style={{ color: chooseChat === 0 ? "var(--c-black)" : "var(--c-lightgray)" }}>모임</p>*/}
        </Button>
        <Button
          onClick={() => {
            setChooseChat(1);
          }}
          style={{
            borderBottom: chooseChat === 0 ? "none" : "2px solid var(--c-black)",
          }}
        >
          {Brewery}
          {/*<p style={{ color: chooseChat === 0 ? "var(--c-lightgray)" : "var(--c-black)" }}>술장</p>*/}
        </Button>
      </div>
      {chooseChat === 0 ? (
        <MeetingMy userId={parseInt(userid)}></MeetingMy>
      ) : (
        <DrinkpostMain></DrinkpostMain>
      )}
    </>
  );
};

export default MyPageTabs;
