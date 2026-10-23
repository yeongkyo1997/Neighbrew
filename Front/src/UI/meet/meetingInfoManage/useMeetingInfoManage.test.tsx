import { act, renderHook, waitFor } from "@testing-library/react";
import { AxiosResponse } from "axios";
import { ReactNode } from "react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { MeetDetail, Meeting } from "../../../Type/types";
import { callApi } from "../../../utils/api";
import { initialMeetDetail, initialUser } from "../../common";
import useMeetingInfoManage from "./useMeetingInfoManage";

vi.mock("../../../utils/api", () => ({ callApi: vi.fn() }));

const mockedCallApi = vi.mocked(callApi);
const ok = <T,>(data: T) => Promise.resolve({ data } as AxiosResponse<T>);

const meetDetail = (meet: Partial<Meeting>): MeetDetail => ({
  meet: {
    ...initialMeetDetail.meet,
    meetId: 10,
    meetName: "막걸리 모임",
    host: { ...initialUser, userId: 1 },
    tagId: 2,
    drink: { ...initialMeetDetail.meet.drink, drinkId: 7, name: "막걸리", tagId: 2 },
    sido: { sidoCode: 11, sidoName: "서울특별시" },
    gugun: { gugunCode: 110, gugunName: "종로구", sidoCode: 11 },
    meetDate: "2030-07-01T19:00:00",
    nowParticipants: 2,
    maxParticipants: 4,
    minLiverPoint: null,
    description: "같이 마셔요",
    imgSrc: "no image",
    ...meet,
  },
  participants: [],
});

const setup = async (meet: MeetDetail) => {
  localStorage.setItem("myId", "1");
  mockedCallApi.mockImplementation((method: string, url: string) => {
    if (url === "api/sido") return ok([{ sidoCode: 11, sidoName: "서울특별시" }]);
    if (url === "api/meet/10") return ok(meet);
    if (url.startsWith("api/gugun/")) return ok([{ gugunCode: 110, gugunName: "종로구", sidoCode: 11 }]);
    if (method === "put") return ok({});
    throw new Error(`예상하지 못한 요청: ${method} ${url}`);
  });
  const wrapper = ({ children }: { children: ReactNode }) => (
    <MemoryRouter initialEntries={["/meet/10/manage/info"]}>
      <Routes>
        <Route path="/meet/:meetId/manage/info" element={children} />
      </Routes>
    </MemoryRouter>
  );
  const hook = renderHook(() => useMeetingInfoManage(), { wrapper });
  await waitFor(() => expect(hook.result.current.meetTitle).toBe("막걸리 모임"));
  await act(async () => {
    await new Promise(resolve => setTimeout(resolve, 0));
  });
  return hook;
};

const submit = async (result: { current: ReturnType<typeof useMeetingInfoManage> }) => {
  await act(async () => {
    result.current.updateMeeting();
    await new Promise(resolve => setTimeout(resolve, 0));
  });
  const put = mockedCallApi.mock.calls.find(([method]) => method === "put");
  return put && { url: put[1], form: put[2] as FormData };
};

beforeEach(() => {
  // 모임 날짜(2030-07-01)가 미래가 되도록 Date만 고정한다
  vi.useFakeTimers({ toFake: ["Date"] });
  vi.setSystemTime(new Date("2030-06-01T12:00:00+09:00"));
  vi.spyOn(window, "scrollTo").mockImplementation(() => undefined);
});

afterEach(() => {
  vi.useRealTimers();
  mockedCallApi.mockReset();
});

describe("모임 정보 수정 (updateMeeting)", () => {
  it("나이 제한이 없는 모임도 저장할 수 있고, 나이 값은 보내지 않는다", async () => {
    const { result } = await setup(meetDetail({ minAge: null, maxAge: null }));
    const put = await submit(result);
    expect(result.current.isModalOn).toBe(false);
    expect(put?.url).toBe("/api/meet/modify/1/10");
    expect(put?.form.get("meetName")).toBe("막걸리 모임");
    expect(put?.form.get("minAge")).toBeNull();
    expect(put?.form.get("maxAge")).toBeNull();
  });

  it("나이 제한이 있는 모임은 그 값을 그대로 보낸다", async () => {
    const { result } = await setup(meetDetail({ minAge: 25, maxAge: 30 }));
    const put = await submit(result);
    expect(put?.form.get("minAge")).toBe("25");
    expect(put?.form.get("maxAge")).toBe("30");
  });
});
