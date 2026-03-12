package com.matrimony.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {

    private Long id;
    private String title;
    private String message;
    private String type;
    private Boolean readStatus;
    private LocalDateTime createdAt;
}
