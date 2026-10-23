import { act, renderHook } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import useDebounce from "./useDebounce";

describe("useDebounce", () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });
  afterEach(() => {
    vi.useRealTimers();
  });

  it("delay가 지나기 전에는 이전 값을, 지나면 새 값을 반환한다", () => {
    const { result, rerender } = renderHook(({ value }) => useDebounce(value, 200), {
      initialProps: { value: "a" },
    });
    rerender({ value: "ab" });
    act(() => {
      vi.advanceTimersByTime(199);
    });
    expect(result.current).toBe("a");
    act(() => {
      vi.advanceTimersByTime(1);
    });
    expect(result.current).toBe("ab");
  });

  it("연속으로 바뀌면 마지막 값만 반영한다", () => {
    const { result, rerender } = renderHook(({ value }) => useDebounce(value, 200), {
      initialProps: { value: "" },
    });
    for (const value of ["막", "막걸", "막걸리"]) {
      rerender({ value });
      act(() => {
        vi.advanceTimersByTime(100);
      });
    }
    expect(result.current).toBe("");
    act(() => {
      vi.advanceTimersByTime(200);
    });
    expect(result.current).toBe("막걸리");
  });
});
