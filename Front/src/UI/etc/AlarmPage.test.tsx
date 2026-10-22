import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AxiosResponse } from "axios";
import { MemoryRouter, Route, Routes, useLocation } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { AlarmLog, User } from "../../Type/types";
import { callApi } from "../../utils/api";
import AlarmPage from "./AlarmPage";

vi.mock("../../utils/api", () => ({ callApi: vi.fn() }));

const mockedCallApi = vi.mocked(callApi);

const sender: User = { userId: 2, nickname: "사용자2", profile: "no image", liverPoint: 40, follower: 0, following: 0 };
const alarm = (pushId: number, content: string, url: string): AlarmLog => ({
  pushId,
  pushType: "FOLLOW",
  content,
  url,
  receiver: { ...sender, userId: 1, nickname: "사용자1" },
  sender,
  createdAt: "2030-01-01T10:00:00",
  read: false,
});

// 알림을 눌러 이동한 뒤의 경로를 화면에 보여준다
const LocationDisplay = () => {
  const location = useLocation();
  return <div data-testid="location">{location.pathname + location.search}</div>;
};

const renderAlarmPage = (alarms: AlarmLog[]) => {
  mockedCallApi.mockImplementation((method: string, url: string) => {
    if (method === "get" && url === "api/push/1") return Promise.resolve({ data: alarms } as AxiosResponse<AlarmLog[]>);
    return Promise.resolve({ data: {} } as AxiosResponse);
  });
  render(
    <MemoryRouter initialEntries={["/myPage/alarm"]}>
      <Routes>
        <Route path="/myPage/alarm" element={<AlarmPage />} />
        <Route path="*" element={<LocationDisplay />} />
      </Routes>
    </MemoryRouter>
  );
};

beforeEach(() => {
  localStorage.setItem("myId", "1");
});

afterEach(() => {
  mockedCallApi.mockReset();
});

describe("AlarmPage", () => {
  it("내 알림 목록을 불러와 보여준다", async () => {
    renderAlarmPage([alarm(3, "사용자2님이 팔로우했습니다", "/myPage/2"), alarm(4, "모임에 승인되었습니다", "/meet/10")]);
    expect(await screen.findByText("사용자2님이 팔로우했습니다")).toBeInTheDocument();
    expect(screen.getByText("모임에 승인되었습니다")).toBeInTheDocument();
    expect(mockedCallApi).toHaveBeenCalledWith("get", "api/push/1");
  });

  it("알림이 없으면 안내 문구를 보여준다", async () => {
    renderAlarmPage([]);
    expect(await screen.findByText("알림이 없습니다.")).toBeInTheDocument();
  });

  it.each([
    ["도메인을 포함한 URL", "https://neighbrew.example.com/meet/10?tab=chat", "/meet/10?tab=chat"],
    ["앱 내부 경로", "/myPage/2", "/myPage/2"],
    ["예전 상대 경로", "meet/12", "/meet/12"],
  ])("%s 알림을 누르면 알림을 지우고 앱 안의 경로로 이동한다", async (_, url, expected) => {
    renderAlarmPage([alarm(7, "알림 내용", url)]);
    await userEvent.click(await screen.findByText("알림 내용"));
    await waitFor(() => expect(screen.getByTestId("location")).toHaveTextContent(expected));
    expect(mockedCallApi).toHaveBeenCalledWith("delete", "api/push/7");
  });
});
