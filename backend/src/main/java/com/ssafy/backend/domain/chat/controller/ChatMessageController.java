package com.ssafy.backend.domain.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.backend.common.auth.LoginUser;
import com.ssafy.backend.common.auth.LoginUserGuard;
import com.ssafy.backend.domain.chat.entity.ChatMessageMongo;
import com.ssafy.backend.domain.chat.service.ChatMessageService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chatMessage")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    //단체 채팅방 메세지 가져온다.
    @GetMapping("{chatRoomId}/{userId}/messages")
    public ResponseEntity<List<ChatMessageMongo>> getChatMessages(@PathVariable Long chatRoomId,
                                                                  @PathVariable Long userId,
                                                                  @LoginUser Long loginUserId) {
        LoginUserGuard.requireSame(userId, loginUserId);
        List<ChatMessageMongo> messages = chatMessageService.getChatMessages(chatRoomId, loginUserId);
        return ResponseEntity.ok(messages);
    }
}
