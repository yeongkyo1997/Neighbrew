// 마이 페이지
import Footer from "../../footer/Footer";
import Navbar from "../Navbar";
import MyPageTabs from "./MyPageTabs";
import ProfileActions from "./ProfileActions";
import ProfileEditModal from "./ProfileEditModal";
import ProfileSummary from "./ProfileSummary";
import useMyPage from "./useMyPage";

const MyPage = () => {
  const state = useMyPage();
  return (
    <nav>
      <header>
        <Navbar />
      </header>
      <div
        style={{
          minHeight: "200px",
          display: "flex",
          flexDirection: "column",
        }}
      >
        <ProfileSummary userData={state.userData} />
        <ProfileActions {...state} />
      </div>
      <MyPageTabs {...state} />
      {/* Footer에 의해 가려지는게 없게 하기위해 존재하는 div */}
      <div style={{ height: "3rem" }}></div>
      <ProfileEditModal {...state} />
      <Footer />
    </nav>
  );
};

export default MyPage;
