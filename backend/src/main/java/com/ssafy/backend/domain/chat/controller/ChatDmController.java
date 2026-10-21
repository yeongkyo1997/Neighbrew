package com.ssafy.backend.domain.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.common.auth.LoginUser;
import com.ssafy.backend.common.auth.LoginUserGuard;
import com.ssafy.backend.domain.chat.service.ChatDmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dm")
public class ChatDmController {
    private final ChatDmService chatDmService;

    // 유저 아이디로 DM목록 조회
    @GetMapping("/list/{userId}")
    public ResponseEntity<?> getUserDmRooms(@PathVariable Long userId,
                                            @LoginUser Long loginUserId) {
        LoginUserGuard.requireSame(userId, loginUserId);
        return ResponseEntity.ok(chatDmService.findMyDmList(userId));
    }

    //dm 방 별로 메세지들을 가져온다.
    @GetMapping("/message/{user1Id}/{user2Id}")
    public ResponseEntity<?> getDmMessages(
            @PathVariable Long user1Id,
            @PathVariable Long user2Id,
            @LoginUser Long userId) {

        return userId.equals(user1Id) || userId.equals(user2Id) ? ResponseEntity.ok(
                chatDmService.findDmMessagesByRoomId(user1Id, user2Id, userId)) : ResponseEntity.status(
                HttpStatus.UNAUTHORIZED).body("유저 정보가 올바르지 않습니다.");
    }
}
