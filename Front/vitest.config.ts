import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

// 날짜·시간 테스트가 실행 환경의 시간대에 따라 달라지지 않도록 한국 시간으로 고정한다
process.env.TZ = "Asia/Seoul";

// vite.config.ts는 loadEnv를 쓰는 함수형이라 테스트 설정은 따로 둔다
export default defineConfig({
  plugins: [react()],
  test: {
    environment: "jsdom",
    setupFiles: ["src/test/setup.ts"],
    include: ["src/**/*.test.{ts,tsx}"],
    restoreMocks: true,
  },
});
