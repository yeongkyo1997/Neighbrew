import axios from "axios";
import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Loading from "../etc/Loading";
import { LoginResponse } from "../../Type/types";

const KakaoLogin = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const KakaologinHandler = () => {
    const code = location.search.split("=")[1];
    void axios
      .post<LoginResponse>("/api/auth/kakao", {
        authorizationCode: code,
      })
      .then(res => {
        localStorage.setItem("token", res.data.accessToken);
        localStorage.setItem("refreshToken", res.data.refreshToken);
        localStorage.setItem("myId", JSON.stringify(res.data.userId));
      })
      .then(() => {
        navigate("/drinkpost");
      });
  };
  useEffect(() => {
    KakaologinHandler();
  }, []);
  return <Loading />;
};
export default KakaoLogin;
