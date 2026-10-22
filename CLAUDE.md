# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

NeighBrew는 술 취향이 맞는 사람끼리 모임을 만들고 주종 정보·후기를 공유하는 모바일 우선 SNS(PWA)입니다. SSAFY 9기 공통 프로젝트(2023)로 시작했고, 이후 백엔드를 도메인 단위로 리팩토링하고 Spring Boot 3으로 옮겼습니다.

- `backend/`: Spring Boot 3.5 (Java 17), JPA + MySQL, MongoDB(채팅 메시지), STOMP WebSocket, SSE 푸시, AWS S3, JWT + OAuth(Kakao/Google/Naver)
- `Front/`: React 18 + TypeScript + Vite 6, styled-components, Redux Toolkit, axios. 서비스 워커는 `Front/public/sw.js`를 직접 씁니다(vite-plugin-pwa 미사용).
- `exec/NeighBrew_포팅매뉴얼.md`: 배포 매뉴얼과 설정 키 목록

## 명령어

### 백엔드 (`backend/`)

```bash
./gradlew build -x test                                  # jar 빌드 (Dockerfile은 build/libs/backend-0.0.1-SNAPSHOT.jar를 복사)
./gradlew bootRun
# 단위 테스트 (Spring 컨텍스트 없이 JUnit5 + Mockito)
./gradlew test --tests 'com.ssafy.backend.common.*' --tests 'com.ssafy.backend.domain.*' --tests 'com.ssafy.backend.authentication.*'
./gradlew test --tests 'com.ssafy.backend.SomeTest.method'   # 단일 테스트
```

- Gradle 래퍼는 8.14.3입니다. JDK 17 이상(21 포함)에서 실행되고, 컴파일 대상은 Java 17입니다.
- `src/main/resources/application.properties`는 git에 없습니다(`backend/.gitignore`). 필요한 키(DB, `oauth.*`, `cloud.aws.*`, `spring.data.mongodb.uri` 등)는 포팅 매뉴얼에 있습니다. 이 파일이 없으면 앱 실행과 `@SpringBootTest`(`contextLoads`)가 실패하므로, 새 테스트는 Spring 컨텍스트 없이 작성하세요.
- JWT 서명 키는 환경변수 `JWT_SECRET`(32바이트 이상)에서 읽습니다. 없으면 임시 키를 만들어 쓰므로, 재시작하면 기존 토큰이 모두 무효가 됩니다.
- Spring Boot 3이므로 `javax.*`가 아니라 `jakarta.*`(servlet, persistence, transaction)를 씁니다.

### 프론트엔드 (`Front/`)

```bash
npm i
npm run dev       # vite --host 0.0.0.0, 포트 5173
npm run build     # tsc && vite build
npm run lint      # eslint, --max-warnings 0
```

- 테스트 스크립트는 없습니다.
- Vite dev 서버는 `/api`, `/ws` 요청을 `VITE_API_BASE_URL`(`.env.*`, git에 없음)로 프록시합니다.

### 전체 스택

`docker-compose.yml`은 frontend(5173), MySQL(3306), server(9999)를 띄웁니다. server 이미지(`eclipse-temurin:17-jre`)는 미리 빌드된 jar를 복사하므로 `./gradlew build`를 먼저 실행해야 합니다.

## 백엔드 아키텍처

패키지 루트는 `com.ssafy.backend`입니다.

```
common/          공용 config, TokenFilter, JwtUtil/JwtService, @LoginUser, StompAuthInterceptor, enum
exception/       GlobalExceptionHandler, ForbiddenException(403)
authentication/  OAuth 로그인과 토큰 발급
domain/<name>/   user, meet, drink, chat, follow, evaluation, push, region, storage
```

### 도메인 서비스 구조: Service → Manager / Validator

각 도메인은 `domain/<name>/{controller,dto,entity,repository,service}`로 나뉩니다. 서비스는 `service/<기능>/` 아래에서 다음처럼 역할을 나눕니다.

- `XxxService`: 흐름만 조립하는 얇은 퍼사드
- `manager/`: 실제 작업 단위 (예: `MeetManager`, `MeetQueryManager`, `MeetChatManager`, `MeetPushManager`, `MeetImageManager`)
- `validator/`: 입력 검증, 그리고 `validateAndGetXxx`로 엔티티 조회와 검증을 함께 처리

`MeetService.saveMeet`이 대표 예시입니다. 모임을 만들 때 채팅방 생성, 호스트 `MeetUser` 저장, 팔로워 푸시 알림까지 여러 도메인을 한 번에 엮습니다. 새 기능도 이 패턴을 따르세요.

- 퍼사드의 쓰기 메서드에는 Spring `@Transactional`을 붙이고, 엔티티 조회와 검증을 모두 끝낸 뒤에 부작용을 시작하세요.
- 되돌릴 수 없는 부작용(SSE 푸시, S3 삭제)은 트랜잭션 안에서 바로 실행하지 말고 이벤트로 발행하세요. 모임 도메인은 `service/meet/event/MeetEvents`를 발행하고, `MeetEventListener`가 `@TransactionalEventListener(AFTER_COMMIT)`에서 처리합니다. 롤백되면 방금 올린 이미지는 `AFTER_ROLLBACK`에서 지웁니다. 리스너에서 DB에 쓰려면 `REQUIRES_NEW`가 필요합니다.
- 모임 참여 인원은 `Meet.addParticipant()`/`removeParticipant()`로만 바꾸고, `MeetValidator.validateAndGetMeetForUpdate`(비관적 락)로 조회한 엔티티에 호출하세요.

### 인증과 권한

- `common/filter/TokenFilter`는 `FilterConfig`에서 `/api/*` 전체에 등록됩니다. 정규화된 경로(`servletPath` + `pathInfo`)로 판단합니다.
  - 토큰 없이 통과하는 경로: `/api/auth/`로 시작하는 경로와 `/api/user/refresh-token`. 로그인 전에 호출해야 하는 엔드포인트는 `/api/auth` 아래에 두세요.
  - 나머지는 `Authorization: Bearer <access JWT>`를 검증하고 `request.setAttribute("userId", subject)`를 설정합니다. JWT에는 `type` 클레임(access/refresh)이 있어 서로 바꿔 쓸 수 없습니다.
  - SSE 연결 경로(`/api/auth/connect/{userId}`, `/api/push/connect/{userId}`)는 헤더 대신 `?ticket=`을 받습니다. 티켓은 `POST /api/push/sse-ticket`으로 발급하고, 30초 동안 한 번만 쓸 수 있으며, 경로의 `userId`와 일치해야 합니다.
- 컨트롤러는 `@LoginUser Long loginUserId`로 로그인 사용자를 받습니다. 경로에 `userId`가 있으면 `LoginUserGuard.requireSame(userId, loginUserId)`로 본인인지 확인하세요. 권한 위반은 `ForbiddenException`(403)으로 던집니다.
- 다른 사람의 리소스(채팅방, 알림, 모임, 리뷰 등)를 다룰 때는 소유자나 참여자인지 서비스나 매니저에서 확인하세요. 예: `ChatRoomUserManager.requireRoomMember`, `PushDataManager.deletePushById`.
- 다른 사용자에게 내려주는 사용자 정보는 `PublicUserResponseDto`(`userId`, `nickname`, `profile`, `intro`, `liverPoint`)만 씁니다. `email`/`name`/`birth`는 본인 정보(`/api/user/myinfo`)에서만 내려줍니다. JPA 엔티티를 응답에 그대로 넣지 마세요.
- 클라이언트가 보낸 이미지 URL은 저장하지 않습니다. 서버가 방금 업로드한 URL이나 `"no image"`만 저장합니다. 저장된 URL은 나중에 S3 삭제 대상이 되기 때문입니다.
- 프론트(`Front/src/utils/api.ts`)는 `Authorization`과 `UserID` 헤더를 붙입니다. 401을 받으면 `/api/user/refresh-token`으로 재발급을 한 번만(single-flight) 시도합니다. 토큰은 `localStorage`의 `token`, `refreshToken`, `myId`에 저장합니다.

### OAuth

- `authentication/domain/oauth/OAuthApiClient` 구현체를 provider마다 하나씩 둡니다(`infra/{kakao,google,naver}`). `RequestOAuthInfoService`가 `oAuthProvider()`를 키로 `Collectors.toMap`을 만들기 때문에, **같은 provider를 반환하는 `@Component` 빈이 둘 있으면 컨텍스트가 시작되지 않습니다**.
- 로그인 흐름은 `AuthController` → `OAuthLoginService` → `RequestOAuthInfoService` 순서입니다.
- 사용자는 `(oAuthProvider, oAuthId)`로 찾습니다(`UserRepository.findByOAuth`, 유니크 제약 `uk_user_oauth`). 고유 ID는 `OAuthInfoResponse.getOAuthId()`로 꺼냅니다. `User.email`은 provider가 준 값을 그대로 저장하는 데이터일 뿐이고(미동의 시 null, 유니크 아님), 계정 조회에 쓰지 마세요. 이메일은 동의 여부에 따라 바뀌고, provider가 인증하지 않은 값일 수 있습니다. 사용자가 바꿀 수 있는 표시 이름도 계정 키로 쓰지 마세요.
- 신규 가입자의 닉네임은 `user_` + 무작위 hex입니다. 사용자는 `user_숫자` 형식이나 이메일을 닉네임으로 쓸 수 없습니다(`UserService`).

### 실시간 기능

- 채팅은 STOMP를 씁니다(`WebSocketConfiguration`, 엔드포인트 `/ws`, SockJS 포함). 접두어가 일반적인 관례와 **반대** 입니다.
  - 클라이언트가 보내는 주소: `/sub/...` (`setApplicationDestinationPrefixes("/sub")`)
  - 클라이언트가 구독하는 주소: `/pub/room/{roomId}`, `/pub/dm/{u1}/{u2}` (`enableSimpleBroker("/pub")`)
- `StompAuthInterceptor`가 inbound 채널에서 권한을 확인합니다.
  - CONNECT/STOMP 프레임에는 `Authorization: Bearer` 헤더가 있어야 합니다.
  - 클라이언트가 보낼 수 있는 명령만 허용합니다. 서버 전용 `MESSAGE` 등은 거부합니다.
  - SEND는 `/sub/`로만 보낼 수 있습니다.
  - SUBSCRIBE는 정확히 일치하는 `/pub/room/{id}`(채팅방 참여자나 모임 HOST/GUEST)와 `/pub/dm/{a}/{b}`(본인 포함)만 허용합니다. SimpleBroker는 구독 목적지를 `*`, `**` 패턴으로 해석하므로 허용 목록을 넓히지 마세요.
- `ChatController`는 메시지의 발신자 ID를 STOMP `Principal`로 덮어쓴 뒤 `SimpMessagingTemplate`으로 브로드캐스트합니다.
- 모임 채팅 메시지는 MongoDB(`ChatMessageMongo`)에 저장되고, 나머지 엔티티는 모두 MySQL/JPA에 있습니다.
- 푸시 알림의 종류, 문구, 이동 경로는 `domain/push/service/push/PushMessage`의 팩토리 메서드로 만들고 `PushService.send(sender, receiver, PushMessage)`로 보냅니다. 경로는 `/`로 시작하는 앱 내부 경로만 넣고, 도메인(`neighbrew.full.url`)은 `PushService`만 붙입니다. 새 `PushType` 값은 DB에 문자열로 저장되므로 함부로 추가하지 마세요.
- 푸시 알림은 SSE를 씁니다. emitter는 `domain/push`의 `EmitterRepository` / `SseConnectionManager`에 메모리로 보관하므로 서버 인스턴스가 하나라고 가정합니다. 설계 메모는 `backend/src/main/resources/SSE_SPEC.md`에 있습니다.
- 이미지 업로드는 `domain/storage`의 S3 매니저를 거칩니다. 최대 20MB입니다(`UploadSizeGuard`).

## 프론트엔드 구조

- `Front/src/App.tsx`: 모든 라우트와 SSE 연결(티켓 발급 후 `EventSource`, 끊기면 재연결)이 있습니다.
- `Front/src/UI/<기능>/`: 화면 컴포넌트 (chat, drinkpost, meet, meetrate, user 등)
- `Front/src/UI/components/`: 공용 컴포넌트
- `Front/src/store.ts`: Redux 상태는 푸터 메뉴 하나뿐입니다. 서버 호출은 화면 컴포넌트에서 `utils/api.ts`의 `callApi`로 바로 합니다. react-query는 `main.tsx`에 Provider만 있고 실제로는 거의 쓰지 않습니다.
- `Front/reference.md`의 팀 규칙:
  - 한글 입력이 두 번 들어가는 문제 때문에 Enter 처리는 `onKeyDown` 대신 `onKeyUp`을 씁니다.
  - 모바일 Enter는 `e.keyCode === 13`으로 판별합니다.
  - 무한 스크롤은 `hooks/useIntersectionObserver`를 씁니다.

## 컨벤션

- 코드 주석, 예외 메시지, 커밋 메시지는 한국어로 씁니다.
- 커밋은 `fix(backend):`, `refactor(front):` 같은 Conventional Commits 형식을 씁니다. 오래된 커밋은 형식이 섞여 있습니다.
- 브랜치 규칙 그림은 `docsResource/branchConvention.png`에 있습니다.
