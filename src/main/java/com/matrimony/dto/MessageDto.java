package com.matrimony.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {

    private Long id;
    private Long chatId;
    private Long senderId;
    private String senderName;
    private String message;
    private Boolean readStatus;
    private LocalDateTime createdAt;
}
