import { useCallback, useLayoutEffect, useRef } from "react";

// React 19.2의 useEffectEvent와 같은 역할을 하는 폴리필
// effect 안에서 최신 렌더의 props·state를 읽되, 그 값이 바뀐다고 effect를 다시 실행하지는 않을 때 쓴다
// 반환된 함수는 정체성이 바뀌지 않으므로 의존성 배열에 넣어도 effect 실행 시점이 달라지지 않는다
// 렌더 중에는 호출하지 말고 effect나 이벤트 핸들러에서만 호출한다
const useEffectEvent = <A extends unknown[], R>(fn: (...args: A) => R) => {
  const ref = useRef(fn);
  // layout effect는 일반 effect보다 먼저 실행되므로 effect에서 호출할 때는 항상 최신 fn이다
  useLayoutEffect(() => {
    ref.current = fn;
  });
  return useCallback((...args: A) => ref.current(...args), []);
};

export default useEffectEvent;
