package com.matrimony.service;

import com.matrimony.dto.SubscriptionDto;
import com.matrimony.dto.SubscriptionRequest;
import com.matrimony.entity.Subscription;
import com.matrimony.entity.Subscription.Plan;
import com.matrimony.entity.User;
import com.matrimony.repository.SubscriptionRepository;
import com.matrimony.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final Map<Plan, Integer> PLAN_DURATIONS = Map.of(
            Plan.BASIC, 30,
            Plan.GOLD, 90,
            Plan.PLATINUM, 180
    );

    private static final Map<Plan, BigDecimal> PLAN_PRICES = Map.of(
            Plan.BASIC, new BigDecimal("999.00"),
            Plan.GOLD, new BigDecimal("2499.00"),
            Plan.PLATINUM, new BigDecimal("3999.00")
    );

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubscriptionDto subscribe(Long userId, SubscriptionRequest request) {
        User user = getUserOrThrow(userId);

        subscriptionRepository.findByUserIdAndActiveTrue(userId).ifPresent(sub -> {
            sub.setActive(false);
            subscriptionRepository.save(sub);
        });

        Plan plan = request.getPlan();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(PLAN_DURATIONS.getOrDefault(plan, 30));

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setAmount(PLAN_PRICES.getOrDefault(plan, BigDecimal.ZERO));
        subscription.setActive(true);
        subscription = subscriptionRepository.save(subscription);

        user.setPremium(true);
        userRepository.save(user);

        return toDto(subscription);
    }

    public SubscriptionDto getActiveSubscription(Long userId) {
        return subscriptionRepository.findByUserIdAndActiveTrue(userId)
                .map(this::toDto)
                .orElse(null);
    }

    public List<SubscriptionDto> getSubscriptionHistory(Long userId) {
        return subscriptionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void checkAndExpireSubscriptions() {
        subscriptionRepository.findAll().stream()
                .filter(sub -> sub.getActive() && sub.getEndDate().isBefore(LocalDate.now()))
                .forEach(sub -> {
                    sub.setActive(false);
                    subscriptionRepository.save(sub);
                    User user = sub.getUser();
                    boolean hasOtherActive = subscriptionRepository
                            .findByUserIdAndActiveTrue(user.getId()).isPresent();
                    if (!hasOtherActive) {
                        user.setPremium(false);
                        userRepository.save(user);
                    }
                });
    }

    private SubscriptionDto toDto(Subscription subscription) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUser().getId());
        dto.setPlan(subscription.getPlan());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setAmount(subscription.getAmount());
        dto.setActive(subscription.getActive());
        dto.setCreatedAt(subscription.getCreatedAt());
        return dto;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
