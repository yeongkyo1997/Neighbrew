package com.ssafy.backend.controller;

import com.ssafy.backend.entity.ChatMessage;
import com.ssafy.backend.entity.ChatRoom;
import com.ssafy.backend.entity.ChatRoomUser;
import com.ssafy.backend.entity.User;
import com.ssafy.backend.repository.ChatMessageRepository;
import com.ssafy.backend.repository.ChatRoomRepository;
import com.ssafy.backend.repository.ChatRoomUserRepository;
import com.ssafy.backend.repository.UserRepository;
import com.ssafy.backend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;


    // 채팅방 생성
    @Transactional
    @PostMapping("/room")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody Map<String, Object> map) {
        ChatRoom room = ChatRoom.builder()
                .chatRoomName((String) map.get("name"))
                .build();
        List<Integer> userIdList = (List<Integer>) map.get("userIdList");

        log.info("userIdList: {}", userIdList);
        for (Integer userId : userIdList) {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
            log.info("user: {}", user);
            ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                    .chatRoom(room)
                    .user(user)
                    .build();
            log.info("chatRoomUser: {}", chatRoomUser);
            chatRoomUserRepository.save(chatRoomUser);
        }

        chatRoomRepository.save(room);
        chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(room)
                .user(null)
                .message("채팅방이 생성되었습니다.")
                .timestamp(LocalDateTime.now())
                .build());
        return ResponseEntity.ok(room);
    }

    // 유저 아이디로 채팅방 조회
    @GetMapping("/{userId}/getChatRoom")
    public ResponseEntity<?> getUserChatRooms(@PathVariable Long userId) {
        return ResponseEntity.ok(chatRoomService.findUserChatRooms(userId));
    }
}
