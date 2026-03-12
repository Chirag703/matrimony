package com.matrimony.dto;

import com.matrimony.entity.Subscription.Plan;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SubscriptionDto {

    private Long id;
    private Long userId;
    private Plan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal amount;
    private Boolean active;
    private LocalDateTime createdAt;
}
