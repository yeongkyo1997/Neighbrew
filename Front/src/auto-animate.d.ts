// @formkit/auto-animate의 루트 export에는 types 조건이 없어서 moduleResolution: bundler에서 any로 해석된다.
// 이 프로젝트가 쓰는 기본 함수만 선언한다.
declare module "@formkit/auto-animate" {
  export default function autoAnimate(el: HTMLElement, config?: Partial<{ duration: number; easing: string }>): unknown;
}
