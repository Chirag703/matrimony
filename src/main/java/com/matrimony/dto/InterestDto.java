package com.matrimony.dto;

import com.matrimony.entity.Interest.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterestDto {

    private Long id;
    private Long fromUserId;
    private String fromUserName;
    private String fromUserPhoto;
    private Long toUserId;
    private String toUserName;
    private Status status;
    private LocalDateTime createdAt;
}
