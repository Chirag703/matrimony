package com.matrimony.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDto {

    private Long id;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserPhoto;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private long unreadCount;
}
