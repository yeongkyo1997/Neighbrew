import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from "axios";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

type Call = { method: string; url: string; authorization: string; userId: string; data: unknown };
type Reply = { status: number; data?: unknown } | "network-error";
type Handler = (call: Call) => Reply | Promise<Reply>;

// 모든 axios 요청(api 인스턴스와 토큰 재발급용 axios.post)을 가로채는 가짜 adapter
let calls: Call[] = [];
let handler: Handler;
const fakeAdapter = async (config: InternalAxiosRequestConfig): Promise<AxiosResponse> => {
  const call: Call = {
    method: (config.method ?? "get").toLowerCase(),
    url: config.url ?? "",
    authorization: String(config.headers.get("Authorization") ?? ""),
    userId: String(config.headers.get("UserID") ?? ""),
    data: typeof config.data === "string" ? (JSON.parse(config.data) as unknown) : config.data,
  };
  calls.push(call);
  // 재시도가 끝없이 반복되면 테스트가 멈추지 않고 실패하도록 끊는다
  if (calls.length > 20) throw new Error("요청이 20번을 넘었습니다 (재시도 반복)");
  const reply = await handler(call);
  if (reply === "network-error") {
    throw new AxiosError("Network Error", AxiosError.ERR_NETWORK, config);
  }
  const response: AxiosResponse = {
    status: reply.status,
    statusText: String(reply.status),
    data: reply.data ?? {},
    headers: {},
    config,
  };
  if (reply.status >= 400) {
    throw new AxiosError(`Request failed with status code ${reply.status}`, AxiosError.ERR_BAD_REQUEST, config, null, response);
  }
  return response;
};

const callsTo = (url: string) => calls.filter(c => c.url.endsWith(url));

// window.location.href 대입을 기록하기 위해 location을 바꿔 끼운다
const originalLocation = Object.getOwnPropertyDescriptor(window, "location");
const fakeLocation = { pathname: "/meet", href: "http://localhost/meet" };

let callApi: typeof import("./api").callApi;

beforeEach(async () => {
  calls = [];
  axios.defaults.adapter = fakeAdapter;
  fakeLocation.pathname = "/meet";
  fakeLocation.href = "http://localhost/meet";
  Object.defineProperty(window, "location", { value: fakeLocation, configurable: true, writable: true });
  localStorage.setItem("token", "old-access");
  localStorage.setItem("refreshToken", "refresh-1");
  localStorage.setItem("myId", "1");
  // 재발급 single-flight 상태(refreshPromise)가 테스트끼리 섞이지 않도록 모듈을 새로 불러온다
  vi.resetModules();
  ({ callApi } = await import("./api"));
});

afterEach(() => {
  if (originalLocation) Object.defineProperty(window, "location", originalLocation);
});

describe("callApi", () => {
  it("저장된 토큰과 사용자 ID를 헤더에 붙여 요청한다", async () => {
    handler = () => ({ status: 200, data: { ok: true } });
    const res = await callApi<{ ok: boolean }>("post", "api/meet/apply", { meetId: 10 });
    expect(res.data.ok).toBe(true);
    expect(calls).toEqual([
      { method: "post", url: "api/meet/apply", authorization: "Bearer old-access", userId: "1", data: { meetId: 10 } },
    ]);
  });

  it("401을 받으면 토큰을 재발급받아 새 토큰으로 한 번 다시 요청한다", async () => {
    handler = call => {
      if (call.url.endsWith("refresh-token")) return { status: 200, data: { accessToken: "new-access", refreshToken: "refresh-2" } };
      return call.authorization === "Bearer new-access" ? { status: 200, data: "ok" } : { status: 401 };
    };
    const res = await callApi<string>("get", "api/user/myinfo");
    expect(res.data).toBe("ok");
    expect(callsTo("refresh-token")).toHaveLength(1);
    expect(callsTo("refresh-token")[0].data).toEqual({ refreshToken: "refresh-1" });
    expect(callsTo("api/user/myinfo").map(c => c.authorization)).toEqual(["Bearer old-access", "Bearer new-access"]);
    expect(localStorage.getItem("token")).toBe("new-access");
    expect(localStorage.getItem("refreshToken")).toBe("refresh-2");
  });

  it("여러 요청이 동시에 401을 받아도 재발급은 한 번만 한다", async () => {
    let finishRefresh: (reply: Reply) => void = () => undefined;
    const refreshReply = new Promise<Reply>(resolve => {
      finishRefresh = resolve;
    });
    handler = call => {
      if (call.url.endsWith("refresh-token")) return refreshReply;
      return call.authorization === "Bearer new-access" ? { status: 200, data: call.url } : { status: 401 };
    };
    const pending = Promise.all([callApi<string>("get", "api/a"), callApi<string>("get", "api/b")]);
    // 두 요청이 모두 401을 받고 재발급을 기다릴 때까지 기다린다
    await vi.waitFor(() => expect(callsTo("refresh-token")).toHaveLength(1));
    await new Promise(resolve => setTimeout(resolve, 0));
    finishRefresh({ status: 200, data: { accessToken: "new-access" } });

    const [a, b] = await pending;
    expect([a.data, b.data]).toEqual(["api/a", "api/b"]);
    expect(callsTo("refresh-token")).toHaveLength(1);
    // refreshToken을 주지 않으면 기존 값을 유지한다
    expect(localStorage.getItem("refreshToken")).toBe("refresh-1");
  });

  it("재시도한 요청이 다시 401이면 더 반복하지 않고 실패한다", async () => {
    handler = call =>
      call.url.endsWith("refresh-token") ? { status: 200, data: { accessToken: "new-access" } } : { status: 401 };
    await expect(callApi("get", "api/user/myinfo")).rejects.toMatchObject({ response: { status: 401 } });
    expect(callsTo("api/user/myinfo")).toHaveLength(2);
    expect(callsTo("refresh-token")).toHaveLength(1);
  });

  it("재발급이 실패하면 저장된 토큰을 지우고 첫 화면으로 보낸다", async () => {
    handler = () => ({ status: 401 });
    await expect(callApi("get", "api/user/myinfo")).rejects.toBeInstanceOf(AxiosError);
    expect(localStorage.getItem("token")).toBeNull();
    expect(localStorage.getItem("refreshToken")).toBeNull();
    expect(localStorage.getItem("myId")).toBeNull();
    expect(fakeLocation.href).toBe("/");
  });

  it("refreshToken이 없으면 재발급을 시도하지 않고 첫 화면으로 보낸다", async () => {
    localStorage.removeItem("refreshToken");
    handler = () => ({ status: 401 });
    await expect(callApi("get", "api/user/myinfo")).rejects.toMatchObject({ response: { status: 401 } });
    expect(callsTo("refresh-token")).toHaveLength(0);
    expect(fakeLocation.href).toBe("/");
  });

  it("401이 아닌 오류와 네트워크 오류는 재발급 없이 그대로 실패한다", async () => {
    handler = call => (call.url.endsWith("a") ? { status: 403, data: "권한이 없습니다." } : "network-error");
    await expect(callApi("get", "api/a")).rejects.toMatchObject({ response: { status: 403, data: "권한이 없습니다." } });
    await expect(callApi("get", "api/b")).rejects.toMatchObject({ code: AxiosError.ERR_NETWORK });
    expect(callsTo("refresh-token")).toHaveLength(0);
    expect(fakeLocation.href).toBe("http://localhost/meet");
  });
});
