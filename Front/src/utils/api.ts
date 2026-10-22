import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from "axios";
import { TokenResponse } from "../Type/types";

const api = axios.create({
  baseURL: "/",
});
// 요청 인터셉터: 토큰을 헤더에 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("myId");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    if (userId) {
      config.headers["UserID"] = `${userId}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 동시에 여러 요청이 401을 받아도 토큰 재발급은 한 번만 수행
let refreshPromise: Promise<string> | null = null;

const refreshAccessToken = (refreshToken: string) => {
  if (refreshPromise === null) {
    refreshPromise = axios
      .post<TokenResponse>("/api/user/refresh-token", { refreshToken })
      .then(({ data }) => {
        localStorage.setItem("token", data.accessToken);
        if (data.refreshToken) localStorage.setItem("refreshToken", data.refreshToken);
        return data.accessToken;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }
  return refreshPromise;
};

// 응답 인터셉터: 토큰 만료 시 재요청 or 로그인 페이지로 리디렉션
api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (err: AxiosError) => {
    // 재시도 여부를 표시하기 위해 요청 설정에 _retry를 붙인다
    const originalRequest = err.config as (InternalAxiosRequestConfig & { _retry?: boolean }) | undefined;
    // 네트워크/CORS/타임아웃 오류는 response가 없으므로 그대로 전달
    if (err.response?.status === 401 && originalRequest && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) {
        // 리프레시 토큰이 없으면 로그인 페이지로 리디렉션
        if (window.location.pathname !== "/") window.location.href = "/";
        return Promise.reject(err);
      }

      try {
        // 토큰 재발급 API 호출
        const accessToken = await refreshAccessToken(refreshToken);
        originalRequest.headers["Authorization"] = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (err) {
        // 리프레시 토큰이 만료되었거나 재발급 요청에 문제가 있으면 로그인 페이지로 리디렉션
        localStorage.removeItem("token");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("myId");
        window.location.href = "/";
        return Promise.reject(err);
      }
    }
    return Promise.reject(err);
  }
);

// T: 응답 본문 타입. 지정하지 않으면 unknown이라 res.data를 쓰려면 타입을 넘겨야 한다.
export const callApi = async <T = unknown>(method: string, url: string, body: unknown = {}): Promise<AxiosResponse<T>> => {
  return api<T>({
    method: method,
    url: url,
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
      UserID: `${localStorage.getItem("myId")}`,
    },
    data: body,
  });
};

// 알림 관련 함수
export const noti = () => {
  void navigator.serviceWorker.ready.then((registration) => {
    void registration.showNotification("알림", {
      body: "pinyin + '\n' + means",
      actions: [
        {
          title: "화면보기",
          action: "goTab",
        },
        {
          title: "닫기",
          action: "close",
        },
      ],
    });
  });
};
