// MyPage의 상태, API 호출, 이벤트 처리
import axios, { AxiosError } from "axios";
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { User, FollowRelation, TokenResponse, Drink } from "../../../Type/types";
import { callApi } from "../../../utils/api";
import useEffectEvent from "../../../hooks/useEffectEvent";

const useMyPage = () => {
  const [userData, setUserData] = useState<User>({
    email: "이메일",
    follower: 0,
    following: 0,
    liverPoint: 0,
    name: "이름",
    profile: "한줄설명",
    userId: 0,
    nickname: "닉네임",
    drinkcount: 0,
  }); // 유저 정보

  const [chooseChat, setChooseChat] = useState(0); // 선택한 채팅방의 index
  const [following, setFollowing] = useState(0); // 팔로잉,팔로워 목록
  const { userid } = useParams();
  const [deleteModalOn, setDeleteModalOn] = useState(false);
  const [nickname, setNickname] = useState("");
  const [profile, setProfile] = useState("profile");
  const [intro, setIntro] = useState("");
  const [birth, setBirth] = useState("");
  const navigate = useNavigate();

  // 팔로우 하기
  const followHandler = async () => {
    await callApi("post", `api/follow/${userid}`).then(() => {
      followers();
    });
  };
  // 팔로워, 팔로잉 인원 수 세기 (팔로우 버튼 색깔 변경)
  const followers = () => {
    void callApi<FollowRelation[]>("get", `api/follow/follower/${userid}`).then(res => {
      if (res.data.length == 0) {
        setUserData(userData => ({ ...userData, follower: res.data.length }));
        setFollowing(0);
        return;
      }
      setUserData(userData => ({ ...userData, follower: res.data.length }));
      res.data.map((item, i) => {
        if (item.follower.userId == parseInt(localStorage.getItem("myId"))) {
          setFollowing(1);
          return;
        } else if (i == res.data.length - 1) {
          setFollowing(0);
        }
      });
    });
    void callApi<FollowRelation[]>("get", `api/follow/following/${userid}`).then(res => {
      setUserData(userData => ({ ...userData, following: res.data.length }));
    });
  };
  const myDrinks = () => {
    void callApi<Drink[]>("get", `api/drink/user/${userid}/review-drink`).then(res => {
      setUserData(userData => ({ ...userData, drinkcount: res.data.length }));
    });
  };
  const goFollowerPage = () => {
    navigate("/myPage/follower/" + userid);
  };
  const goFollowPage = () => {
    navigate("/myPage/follow/" + userid);
  };
  const userInfo = useEffectEvent(() => {
    //생년월일 등 개인정보는 본인 정보(myinfo)에만 포함된다
    const url = userid === localStorage.getItem("myId") ? "api/user/myinfo" : `api/user/${userid}`;
    void callApi<User>("get", url)
      .then(res => {
        setUserData(res.data);
      })
      .then(() => {
        //생년월일을 입력하지 않은 사용자는 빈 값으로 둔다 (기본값을 채우면 다른 항목만 저장해도 그 값이 저장된다)
        setBirth(userData.birth ?? "");
      })
      .then(() => {
        myDrinks();
        followers();
      });
  });
  const refresh = () => {
    if (localStorage.getItem("token") != null) {
      void callApi<TokenResponse>("post", "api/user/refresh-token", {
        refreshToken: localStorage.getItem("refreshToken"),
      }).then(res => {
        localStorage.setItem("token", res.data.accessToken);
        localStorage.setItem("refreshToken", res.data.refreshToken);
      });
    }
  };

  useEffect(() => {
    setNickname(userData.nickname);
    setIntro(userData.intro);
    setBirth(userData.birth ?? "");
  }, [userData]);
  const nicknameHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    setNickname(e.target.value);
  };
  const introHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    setIntro(e.target.value);
  };
  //이 연도 이상 출생자는 만 18세 미만으로 간주
  const adultCutoffYear = new Date().getFullYear() - 18;
  const birthHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    if (parseInt(e.target.value.split("-")[0]) >= adultCutoffYear) {
      alert("만 18세 이상만 이용 가능합니다");
      setBirth(userData.birth ?? "");
      return;
    }
    setBirth(e.target.value);
  };
  const modalHandler = () => {
    setDeleteModalOn(true);
  };
  useEffect(() => {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });

    refresh();
    userInfo();
    localStorage.setItem("chooseMenu", "3");
  }, [userid, userInfo]);

  const [imgFile, setImgFile] = useState<string | null>(null);
  const imgRef = useRef<HTMLInputElement>(null);

  //이미지 파일 업로드 시 미리보기
  const saveImgFile = () => {
    setProfile("profile");
    const file = imgRef.current.files[0];
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onloadend = () => {
      if (typeof reader.result === "string") {
        setImgFile(reader.result);
      }
    };
  };

  const changeUserInfo = () => {
    const file = imgRef.current.files[0];
    const formData = new FormData();
    formData.append("profile", file);
    if (userData.nickname != nickname && nickname.length > 20) {
      alert("닉네임은 20자 까지만 가능합니다.");
      setNickname(userData.nickname);
      return;
    }
    if (userData.nickname != nickname && nickname.includes("@")) {
      alert("닉네임에 '@'는 사용할 수 없습니다.");
      setNickname(userData.nickname);
      return;
    }
    if (userData.nickname != nickname && /^user_\d+$/.test(nickname)) {
      alert("user_숫자 형식의 닉네임은 사용할 수 없습니다.");
      setNickname(userData.nickname);
      return;
    }
    if (userData.intro != intro && intro.length > 50) {
      alert("한줄소개는 50자 까지만 가능합니다");
      setIntro(userData.intro);
      return;
    }
    //생년월일을 바꾼 경우에만 검사 (생년월일을 비워 둔 사용자도 다른 항목 수정 가능)
    if (birth !== (userData.birth ?? "") && parseInt(birth.split("-")[0]) >= adultCutoffYear) {
      alert("만 18세 이상만 가입 가능합니다.");
      setBirth(userData.birth ?? "");
      return;
    }
    if (file !== undefined && profile !== null) {
      void axios
        .put(`/api/user/img/${userid}`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: "Bearer " + localStorage.getItem("token"),
            UserID: localStorage.getItem("myId"),
          },
        })
        .then(() => {
          userInfo();
        })
        .then(() => {
          followers();
        })
        .then(() => {
          myDrinks();
        });
    }
    if (profile == null) {
      const formData = new FormData();
      formData.append("profile", null);
      void axios
        .put(`/api/user/img/${localStorage.getItem("myId")}`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: "Bearer " + localStorage.getItem("token"),
            UserID: localStorage.getItem("myId"),
          },
        })
        .then(() => {
          userInfo();
        })
        .then(() => {
          followers();
        })
        .then(() => {
          myDrinks();
        });
    }
    if (intro == "" || nickname == "") {
      alert("빈 값이 존재합니다.");
      return;
    }
    if (userData.nickname == nickname && userData.intro == intro && (userData.birth ?? "") == birth) return;

    callApi("put", `api/user/${localStorage.getItem("myId")}`, {
      nickname: nickname,
      intro: intro,
      //비워 두면 보내지 않는다 (서버는 null이면 기존 값을 유지)
      birth: birth || undefined,
    })
      .then(() => {
        userInfo();
      })
      .then(() => {
        followers();
      })
      .then(() => {
        myDrinks();
      })
      .catch((err: AxiosError<string>) => {
        if (err?.response?.data === "닉네임 중복") {
          alert("중복된 닉네임입니다. 다시 입력해주세요.");
        }
      });
  };
  const reportHandler = () => {
    if (window.confirm("정말 신고 하시겠습니까?")) {
      alert("신고되었습니다..");
    }
  };

  // DM 창으로 이동
  const directMessageHandler = () => {
    if (userid != localStorage.getItem("myId")) {
      navigate("/directchat/" + localStorage.getItem("myId") + "/" + userid);
    }
  };
  const changeUserProfiletoDefault = () => {
    setProfile(null);
    setImgFile(null);
    setUserData(prev => {
      return { ...prev, profile: "no image" };
    });
  };
  return {
    userData, userid, chooseChat, setChooseChat, following, deleteModalOn, setDeleteModalOn,
    nickname, intro, birth, imgFile, imgRef, adultCutoffYear, navigate,
    followHandler, goFollowerPage, goFollowPage, nicknameHandler, introHandler, birthHandler, modalHandler,
    saveImgFile, changeUserInfo, reportHandler, directMessageHandler, changeUserProfiletoDefault,
  };
};

export type MyPageState = ReturnType<typeof useMyPage>;

export default useMyPage;
