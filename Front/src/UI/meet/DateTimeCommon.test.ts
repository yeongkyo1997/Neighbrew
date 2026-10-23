import { afterEach, describe, expect, it, vi } from "vitest";
import { formateDate, formateTime, localDate, localTime } from "./DateTimeCommon";

// vitest.config.ts에서 TZ=Asia/Seoul로 고정한다
describe("formateDate / formateTime", () => {
  it("월·일·시·분이 한 자리면 0을 채운다", () => {
    expect(formateDate("2030-01-05T07:03:00")).toBe("2030-01-05");
    expect(formateTime("2030-01-05T07:03:00")).toBe("07:03");
  });
  it("두 자리는 그대로 쓴다", () => {
    expect(formateDate("2030-12-25T19:45:00")).toBe("2030-12-25");
    expect(formateTime("2030-12-25T19:45:00")).toBe("19:45");
  });
});

describe("localDate / localTime", () => {
  afterEach(() => {
    vi.useRealTimers();
  });

  it("UTC가 아니라 로컬(KST) 기준 날짜와 시간을 돌려준다", () => {
    vi.useFakeTimers();
    // UTC로는 12월 31일 15:30이지만 KST로는 1월 1일 00:30이다
    vi.setSystemTime(new Date("2029-12-31T15:30:00Z"));
    expect(localDate()).toBe("2030-01-01");
    expect(localTime()).toBe("00:30");
  });
});
