import { describe, expect, it } from "vitest";
import { encodeUrl, getTagName } from "./common";

describe("getTagName", () => {
  it.each([
    [0, "전체"],
    [1, "양주"],
    [2, "전통주"],
    [3, "칵테일"],
    [4, "사케"],
    [5, "와인"],
    [6, "수제맥주"],
    [7, "소주/맥주"],
  ])("tagId %i는 %s", (tagId, name) => {
    expect(getTagName(tagId)).toBe(name);
  });

  // 현재 동작 기록: 목록에 없는 tagId는 이름을 돌려주지 않고 예외가 난다
  it("목록에 없는 tagId는 예외가 난다", () => {
    expect(() => getTagName(8)).toThrow(TypeError);
  });
});

describe("encodeUrl", () => {
  it("공백을 %20으로 바꾼다", () => {
    expect(encodeUrl("https://s3.example.com/my image 1.png")).toBe(
      "https://s3.example.com/my%20image%201.png"
    );
  });

  it("공백이 없으면 그대로 둔다", () => {
    expect(encodeUrl("https://s3.example.com/a.png")).toBe("https://s3.example.com/a.png");
  });
});
