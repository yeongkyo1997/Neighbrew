import { renderHook } from "@testing-library/react";
import { useEffect } from "react";
import { describe, expect, it, vi } from "vitest";
import useEffectEvent from "./useEffectEvent";

describe("useEffectEvent", () => {
  it("리렌더해도 같은 함수를 반환한다", () => {
    const { result, rerender } = renderHook(({ value }) => useEffectEvent(() => value), {
      initialProps: { value: 1 },
    });
    const first = result.current;
    rerender({ value: 2 });
    expect(result.current).toBe(first);
  });

  it("호출하면 최신 렌더의 값을 읽는다", () => {
    const { result, rerender } = renderHook(({ value }) => useEffectEvent(() => value), {
      initialProps: { value: "처음" },
    });
    rerender({ value: "나중" });
    expect(result.current()).toBe("나중");
  });

  it("의존성에 넣어도 effect를 다시 실행시키지 않고, effect 안에서는 최신 값을 읽는다", () => {
    const seen = vi.fn<(trigger: number, other: string) => void>();
    const { rerender } = renderHook(
      ({ trigger, other }) => {
        const onTrigger = useEffectEvent(() => {
          seen(trigger, other);
        });
        useEffect(() => {
          onTrigger();
        }, [trigger, onTrigger]);
      },
      { initialProps: { trigger: 1, other: "a" } }
    );
    // trigger가 그대로면 other가 바뀌어도 effect는 실행되지 않는다
    rerender({ trigger: 1, other: "b" });
    expect(seen).toHaveBeenCalledTimes(1);
    // trigger가 바뀌면 그 렌더의 other를 읽는다
    rerender({ trigger: 2, other: "c" });
    expect(seen).toHaveBeenCalledTimes(2);
    expect(seen).toHaveBeenLastCalledWith(2, "c");
  });

  it("인자와 반환값을 그대로 전달한다", () => {
    const { result } = renderHook(() => useEffectEvent((a: number, b: number) => a + b));
    expect(result.current(2, 3)).toBe(5);
  });
});
