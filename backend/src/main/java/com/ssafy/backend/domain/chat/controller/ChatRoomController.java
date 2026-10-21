package com.ssafy.backend.domain.chat.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.common.auth.LoginUser;
import com.ssafy.backend.common.auth.LoginUserGuard;
import com.ssafy.backend.domain.chat.entity.ChatRoom;
import com.ssafy.backend.domain.chat.service.room.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @PostMapping("/room/{userId}")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody Map<String, Object> map,
                                                   @PathVariable Long userId,
                                                   @LoginUser Long loginUserId) {
        LoginUserGuard.requireSame(userId, loginUserId);
        return ResponseEntity.ok(chatRoomService.createChatRoom(map));
    }

    // 유저 아이디로 채팅방 조회
    @GetMapping("/{userId}/getChatRoom")
    public ResponseEntity<?> getUserChatRooms(@PathVariable Long userId,
                                              @LoginUser Long loginUserId) {
        LoginUserGuard.requireSame(userId, loginUserId);
        return ResponseEntity.ok(chatRoomService.findUserChatRooms(userId));
    }

    // 채팅방 유저 조회
    @GetMapping("/{chatRoomId}/{userId}/users")
    public ResponseEntity<?> getChatRoomUsers(@PathVariable Long chatRoomId,
                                              @PathVariable Long userId,
                                              @LoginUser Long loginUserId) {
        LoginUserGuard.requireSame(userId, loginUserId);
        return ResponseEntity.ok(chatRoomService.getUsersInChatRoom(chatRoomId, loginUserId));
    }

    @GetMapping("{chatRoomId}/detail")
    public ResponseEntity<?> getChatRoomDetail(@PathVariable Long chatRoomId) {
        return ResponseEntity.ok(chatRoomService.getChatRoomDetail(chatRoomId));
    }
}
