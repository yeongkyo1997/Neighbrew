import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { initialDrink } from "../common";
import {
  ageCheck,
  drinkCheck,
  imgcheck,
  liverLimitCheck,
  participantsCheck,
  positionCheck,
  timeCheck,
  titleCheck,
} from "./CheckValid";

describe("titleCheck", () => {
  it("1-30자는 통과한다", () => {
    expect(titleCheck("가")).toBe(true);
    expect(titleCheck("가".repeat(30))).toBe(true);
  });
  it("빈 문자열과 31자 이상은 실패한다", () => {
    expect(titleCheck("")).toBe(false);
    expect(titleCheck("가".repeat(31))).toBe(false);
  });
});

describe("drinkCheck", () => {
  it("술을 고르지 않으면(drinkId 0) 실패한다", () => {
    expect(drinkCheck({ ...initialDrink, drinkId: 0 })).toBe(false);
    expect(drinkCheck({ ...initialDrink, drinkId: 7 })).toBe(true);
  });
});

describe("positionCheck", () => {
  it("시/도와 구/군을 모두 골라야 통과한다", () => {
    expect(positionCheck(11, 110)).toBe(true);
    expect(positionCheck(0, 110)).toBe(false);
    expect(positionCheck(11, 0)).toBe(false);
  });
});

describe("participantsCheck", () => {
  it("1-8명만 통과한다", () => {
    expect(participantsCheck(1)).toBe(true);
    expect(participantsCheck(8)).toBe(true);
    expect(participantsCheck(0)).toBe(false);
    expect(participantsCheck(9)).toBe(false);
  });
});

describe("liverLimitCheck", () => {
  it("100 이하만 통과한다", () => {
    expect(liverLimitCheck(0)).toBe(true);
    expect(liverLimitCheck(100)).toBe(true);
    expect(liverLimitCheck(101)).toBe(false);
  });
});

describe("ageCheck", () => {
  it("최소 나이 20세 이상, 최대 나이 200세 이하만 통과한다", () => {
    expect(ageCheck(20, 30)).toBe(true);
    expect(ageCheck(20, 200)).toBe(true);
    expect(ageCheck(19, 30)).toBe(false);
    expect(ageCheck(20, 201)).toBe(false);
  });

  // 나이 제한이 없는 모임(0, 빈 칸 → NaN, 서버의 null)도 수정·생성할 수 있어야 한다
  it("나이를 입력하지 않으면 제한 없음으로 통과한다", () => {
    expect(ageCheck(0, 0)).toBe(true);
    expect(ageCheck(NaN, NaN)).toBe(true);
    expect(ageCheck(null, null)).toBe(true);
  });
  it("최소 나이 없이 최대 나이만 입력해도 200 이하면 통과한다", () => {
    expect(ageCheck(NaN, 40)).toBe(true);
    expect(ageCheck(NaN, 201)).toBe(false);
  });
});

describe("imgcheck", () => {
  const file = (size: number, type: string) => {
    const f = new File(["x"], "a", { type });
    Object.defineProperty(f, "size", { value: size });
    return f;
  };
  it("파일이 없으면 통과한다", () => {
    expect(imgcheck(null)).toBe(true);
  });
  it("10MB 이하 이미지는 통과한다", () => {
    expect(imgcheck(file(1024 * 1024 * 10, "image/png"))).toBe(true);
  });
  it("10MB를 넘거나 이미지가 아니면 실패한다", () => {
    expect(imgcheck(file(1024 * 1024 * 10 + 1, "image/png"))).toBe(false);
    expect(imgcheck(file(100, "application/pdf"))).toBe(false);
  });
});

describe("timeCheck", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    // 2030-01-01 19:00 (KST)
    vi.setSystemTime(new Date("2030-01-01T19:00:00+09:00"));
  });
  afterEach(() => {
    vi.useRealTimers();
  });

  it("날짜나 시간이 비어 있으면 실패한다", () => {
    expect(timeCheck("", "20:00")).toBe(false);
    expect(timeCheck("2030-01-01", "")).toBe(false);
  });
  it("지금 이후면 통과하고, 지금 이전이면 실패한다", () => {
    expect(timeCheck("2030-01-01", "19:01")).toBe(true);
    expect(timeCheck("2030-01-01", "18:59")).toBe(false);
    expect(timeCheck("2029-12-31", "23:00")).toBe(false);
  });
});
