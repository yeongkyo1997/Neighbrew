export interface User {
  userId: number;
  //email·name·birth는 본인 정보(api/user/myinfo)에만 포함된다
  email?: string;
  nickname: string;
  name?: string;
  birth?: string;
  intro?: string;
  liverPoint: number;
  profile: string;
  follower: number;
  following: number;
  createdAt?: string;
  updatedAt?: string;
  oauthProvider?: string;
  drinkcount?: number;
}
export interface Message {
  chatMessageId: number;
  chatRoom: ChatRoom;
  user: User;
  message: string;
  createdAt: string;
}
export interface Chat {
  chatMessageId?: number;
  message?: string;
  // 실시간 메시지는 보낸 사람의 userId·nickname만 담는다
  user?: Pick<User, "userId" | "nickname"> & Partial<User>;
  createdAt?: string;
  chatRoom?: ChatRoom;
  userId?: number;
  userNickname?: string;
  chatRoomName?: string;
  chatRoomId?: number;
}

export interface ChatRoom {
  chatRoomId?: number;
  chatRoomName?: string;
}
//모임 인터페이스
export interface Meeting {
  meetId: number;
  meetName: string;
  description: string;
  host?: User;
  hostId?: number;
  nowParticipants: number;
  maxParticipants: number;
  meetDate: string;
  tagId: number;
  sido: {
    sidoCode: number;
    sidoName: string;
  };
  gugun: {
    gugunCode: number;
    gugunName: string;
    sidoCode: number;
  };
  minAge?: number;
  maxAge?: number;
  minLiverPoint?: number;
  drink?: Drink;
  imgSrc: string;
  meetStatus?: string;
  chatRoomId?: number;
}

//미팅 참여자와 참여 상태(HOST/GUEST/APPLY)
export type Participant = {
  user: User;
  status: string;
};

//미팅 상세 정보 type
export type MeetDetail = {
  meet: Meeting;
  participants: Participant[];
};

export interface Drink {
  degree: number;
  description: string;
  drinkId: number;
  image: string;
  name: string;
  tagId: number;
}

export interface Review {
  content: string;
  drink: Drink;
  drinkReviewId: number;
  img: string;
  user: User;
  likeCount: number;
}
// 특정 유저의 팔로잉과 팔로워 목록을 보여줌
export interface Followers {
  followerId: number; // 그냥 pk임
  follower: User[];
  following: User[];
}

export interface SubReview {
  subReviewId: number;
  content: string;
  createdAt: string;
  drinkReview: Review;
  user: User;
}

export interface DrinkFestival {
  drinkFestivalId: number;
  image: string;
  name: string;
  redirectUri: string;
}

export interface AlarmLog {
  pushId: number;
  pushType: string;
  content: string;
  url: string;
  receiver: User;
  sender: User;
  createdAt: string;
  read: boolean;
}

// ===== API 응답 타입 (백엔드 DTO와 맞춘다) =====

// Spring Data Page 응답에서 화면이 쓰는 필드
export interface Page<T> {
  content: T[];
  last: boolean;
  first: boolean;
  empty: boolean;
  number: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

export interface Sido {
  sidoCode: number;
  sidoName: string;
}

export interface Gugun {
  gugunCode: number;
  gugunName: string;
  sidoCode: number;
}

// GET /api/follow/follower|following/{userId} (FollowResponseDto)
export interface FollowRelation {
  follower: User;
  following: User;
}

// GET /api/meet/mymeet/{userId}: 참여 상태별 모임 목록
export type MyMeets = Record<"HOST" | "GUEST" | "APPLY", Meeting[]>;

// GET /api/dm/list/{userId} (ChatDmRoom)
export interface ChatDmRoom {
  chatDmRoomId: number;
  chatDmRoomName?: string;
  user1: User;
  user2: User;
  user1AttendTime?: string;
  user2AttendTime?: string;
  lastMessageTime?: string;
}

// GET /api/dm/message/{user1Id}/{user2Id}
export interface DmMessages {
  messages?: Chat[];
  user1: User;
  user2: User;
}

// 채팅 목록 화면은 모임 채팅방과 DM 방을 같은 상태에 번갈아 담는다
export type ChatListItem = ChatRoom & Partial<ChatDmRoom>;

// STOMP로 받는 모임 채팅 메시지
export interface MeetChatMessage {
  message: string;
  userId: number;
  userNickname: string;
}

// STOMP로 받는 DM 메시지
export interface DmChatMessage {
  message: string;
  userId: number;
  user: { userId: number; nickname: string };
}

// POST /api/user/refresh-token
export interface TokenResponse {
  accessToken: string;
  refreshToken?: string;
}

// POST /api/auth/{provider} (LoginResponse)
export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
}

// GET /api/auth/login/{provider}: 소셜 로그인 페이지 주소
export interface LoginRedirect {
  URL: string;
}

// POST /api/push/sse-ticket
export interface SseTicket {
  ticket: string;
}
