package com.ssafy.backend.domain.chat.service.room.dto;

import java.util.List;

public class ChatRoomCreateRequest {
    public final String name;
    public final List<Integer> userIdList;

    public ChatRoomCreateRequest(String name, List<Integer> userIdList) {
        this.name = name;
        this.userIdList = userIdList;
    }
}