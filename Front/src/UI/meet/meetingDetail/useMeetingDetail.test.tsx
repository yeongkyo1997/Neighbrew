import { act, renderHook, waitFor } from "@testing-library/react";
import { AxiosError, AxiosResponse } from "axios";
import { ReactNode } from "react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { MeetDetail, Meeting, User } from "../../../Type/types";
import { callApi } from "../../../utils/api";
import { initialMeetDetail } from "../../common";
import useMeetingDetail from "./useMeetingDetail";

vi.mock("../../../utils/api", () => ({ callApi: vi.fn() }));
// 채팅 참여 버튼을 누르지 않는 한 쓰이지 않지만, 테스트 환경에서 SockJS를 불러오지 않도록 막는다
vi.mock("@stomp/stompjs", () => ({ Stomp: { over: vi.fn() } }));
vi.mock("sockjs-client", () => ({ default: vi.fn() }));

const mockedCallApi = vi.mocked(callApi);
const ok = <T,>(data: T) => Promise.resolve({ data } as AxiosResponse<T>);

const user = (userId: number, extra: Partial<User> = {}): User => ({
  userId,
  nickname: `사용자${userId}`,
  profile: "no image",
  liverPoint: 40,
  follower: 0,
  following: 0,
  ...extra,
});

const meetDetail = (meet: Partial<Meeting> = {}): MeetDetail => ({
  meet: { ...initialMeetDetail.meet, meetId: 10, minAge: null, maxAge: null, minLiverPoint: null, ...meet },
  participants: [
    { user: user(1), status: "HOST" },
    { user: user(2), status: "GUEST" },
    { user: user(3), status: "APPLY" },
  ],
});

type Setup = {
  myId: number;
  meet?: MeetDetail;
  me?: User | "pending";
  apply?: () => Promise<AxiosResponse<unknown>>;
};

const setup = async ({ myId, meet = meetDetail(), me = user(myId), apply = () => ok({}) }: Setup) => {
  localStorage.setItem("myId", String(myId));
  mockedCallApi.mockImplementation((method: string, url: string) => {
    if (method === "get" && url === "api/meet/10") return ok(meet);
    if (method === "get" && url === "api/user/myinfo") return me === "pending" ? new Promise(() => undefined) : ok(me);
    if (method === "post" && url === "api/meet/apply") return apply();
    throw new Error(`예상하지 못한 요청: ${method} ${url}`);
  });
  const wrapper = ({ children }: { children: ReactNode }) => (
    <MemoryRouter initialEntries={["/meet/10"]}>
      <Routes>
        <Route path="/meet/:meetId" element={children} />
      </Routes>
    </MemoryRouter>
  );
  const hook = renderHook(() => useMeetingDetail(), { wrapper });
  // 모임 정보를 받아 참여 상태를 계산하고, 내 정보 요청까지 끝날 때까지 기다린다
  await waitFor(() => expect(hook.result.current.userStatus).not.toBe(""));
  await act(async () => {
    await new Promise(resolve => setTimeout(resolve, 0));
  });
  return hook;
};

const postCalls = () => mockedCallApi.mock.calls.filter(([method]) => method === "post");

beforeEach(() => {
  // 나이 계산이 날짜에 따라 바뀌지 않도록 Date만 고정한다 (waitFor가 쓰는 타이머는 그대로 둔다)
  vi.useFakeTimers({ toFake: ["Date"] });
  vi.setSystemTime(new Date("2030-06-01T12:00:00+09:00"));
  vi.spyOn(window, "scrollTo").mockImplementation(() => undefined);
});

afterEach(() => {
  vi.useRealTimers();
  mockedCallApi.mockReset();
});

describe("참여 상태 계산", () => {
  it.each([
    [1, "HOST"],
    [2, "GUEST"],
    [3, "APPLY"],
    [4, "NONE"],
  ])("사용자 %i의 상태는 %s", async (myId, status) => {
    const { result } = await setup({ myId });
    expect(result.current.userStatus).toBe(status);
  });

  it("참여자 목록에는 신청자를 빼고, 신청자 목록은 따로 둔다", async () => {
    const { result } = await setup({ myId: 1 });
    expect(result.current.memberList.map(u => u.userId)).toEqual([1, 2]);
    expect(result.current.applicantList.map(u => u.userId)).toEqual([3]);
  });
});

describe("참여 신청 (applyMeet)", () => {
  it("내 정보를 아직 못 불러왔으면 신청하지 않고 안내한다", async () => {
    const { result } = await setup({ myId: 4, me: "pending" });
    act(() => result.current.applyMeet());
    expect(result.current.errMsg).toContain("사용자 정보를 불러오는 중");
    expect(result.current.simpleModalOn).toBe(true);
    expect(postCalls()).toHaveLength(0);
  });

  it("나이 제한이 있는 모임은 생년월일이 없으면 거부한다", async () => {
    const { result } = await setup({ myId: 4, meet: meetDetail({ minAge: 25 }), me: user(4, { birth: null }) });
    act(() => result.current.applyMeet());
    expect(result.current.errMsg).toContain("생년월일을 입력해야");
    expect(postCalls()).toHaveLength(0);
  });

  it("간수치가 모임 기준보다 낮으면 거부한다", async () => {
    const { result } = await setup({ myId: 4, meet: meetDetail({ minLiverPoint: 60 }), me: user(4, { liverPoint: 59 }) });
    act(() => result.current.applyMeet());
    expect(result.current.errMsg).toBe("간수치 제한에 부합하지 않습니다.");
    expect(postCalls()).toHaveLength(0);
  });

  it.each([
    ["최소 나이보다 어리면", { minAge: 25 }, "2007-01-01"], // 2030년 기준 24세
    ["최대 나이보다 많으면", { maxAge: 30 }, "1999-01-01"], // 2030년 기준 32세
  ])("%s 거부한다", async (_, limit, birth) => {
    const { result } = await setup({ myId: 4, meet: meetDetail(limit), me: user(4, { birth }) });
    act(() => result.current.applyMeet());
    expect(result.current.errMsg).toBe("나이 제한에 부합하지 않습니다.");
    expect(postCalls()).toHaveLength(0);
  });

  it("조건을 만족하면 신청을 보내고 상태를 APPLY로 바꾼다", async () => {
    const { result } = await setup({
      myId: 4,
      meet: meetDetail({ minAge: 25, maxAge: 40, minLiverPoint: 30 }),
      me: user(4, { birth: "2000-01-01", liverPoint: 50 }),
    });
    await act(async () => {
      result.current.applyMeet();
      await new Promise(resolve => setTimeout(resolve, 0));
    });
    expect(postCalls()).toEqual([["post", "api/meet/apply", { userId: 4, meetId: "10" }]]);
    expect(result.current.userStatus).toBe("APPLY");
    expect(result.current.errMsg).toContain("모임에 참여 신청하였습니다");
  });

  it("서버가 거절하면 서버가 보낸 메시지를 보여준다", async () => {
    const error = new AxiosError("Bad Request", AxiosError.ERR_BAD_REQUEST, undefined, null, {
      status: 400,
      data: "이미 신청한 모임입니다.",
    } as AxiosResponse<string>);
    const { result } = await setup({ myId: 4, apply: () => Promise.reject(error) });
    await act(async () => {
      result.current.applyMeet();
      await new Promise(resolve => setTimeout(resolve, 0));
    });
    expect(result.current.errMsg).toBe("이미 신청한 모임입니다.");
    expect(result.current.userStatus).toBe("NONE");
  });
});
