package com.matrimony.dto;

import com.matrimony.entity.Subscription.Plan;
import lombok.Data;

@Data
public class SubscriptionRequest {

    private Plan plan;
}
