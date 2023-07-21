import { useState, useEffect } from "react";
import "./App.css";
import { useNavigate, Route, Routes } from "react-router-dom";
import FirstLoading from "./UI/etc/FirstLoading";
import Login from "./UI/user/Login";
import Main from "./UI/home/Main";
import ChatList from "./UI/chat/ChatList";
import ChatRoom from "./UI/chat/ChatRoom";
import Drinkpost from "./UI/drinkpost/DrinkpostMain";
import MeetingMain from "./UI/meet/MeetingMain";
import MeetingDetail from "./UI/meet/MeetingDetail";
import Mypage from "./UI/user/MyPage";

import DrinkpostCreate from "./UI/drinkpost/DrinkpostCreate";
import DrinkpostDetail from "./UI/drinkpost/DrinkpostDetail";

function App() {
  const navigate = useNavigate();
  const [isLodaing, setIsLoading] = useState(true); // 개발시 isLoading true로 두고 하기

  useEffect(() => {
    setTimeout(() => {
      setIsLoading(true);
      console.log(isLodaing);
    }, 3000);
  }, []);

  return (
    <>
      <Routes>
        <Route
          path="/"
          element={
            <>
              {!isLodaing && <FirstLoading />}
              {isLodaing && <Login></Login>}
            </>
          }
        />
        <Route
          path="/home"
          element={
            <>
              <Main />
            </>
          }
        />
        <Route
          path="/drinkpost"
          element={
            <>
              <Drinkpost />
            </>
          }
        />
        <Route path="/meet" element={<MeetingMain />}></Route>
        <Route path="/meet/:meetId" element={<MeetingDetail />}></Route>
        <Route path="/myPage/:userid" element={<Mypage></Mypage>}></Route>
        <Route path="/chatList" element={<ChatList></ChatList>}></Route>
        <Route path="/chatList/:roomId" element={<ChatRoom></ChatRoom>} />
        <Route path="/drinkpost/create" element={<DrinkpostCreate></DrinkpostCreate>} />
        <Route path="/drinkpost/:drinkId" element={<DrinkpostDetail></DrinkpostDetail>} />
      </Routes>
    </>
  );
}

export default App;
