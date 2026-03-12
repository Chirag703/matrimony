package com.matrimony.service;

import com.matrimony.dto.SubscriptionDto;
import com.matrimony.dto.SubscriptionRequest;
import com.matrimony.entity.Subscription;
import com.matrimony.entity.Subscription.Plan;
import com.matrimony.entity.User;
import com.matrimony.repository.SubscriptionRepository;
import com.matrimony.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setPhone("+919876543210");
        testUser.setName("Test User");
        testUser.setPremium(false);
    }

    @Test
    void subscribe_basicPlan_shouldActivateSubscription() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setPlan(Plan.BASIC);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findByUserIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        Subscription savedSub = new Subscription();
        savedSub.setId(1L);
        savedSub.setUser(testUser);
        savedSub.setPlan(Plan.BASIC);
        savedSub.setStartDate(LocalDate.now());
        savedSub.setEndDate(LocalDate.now().plusDays(30));
        savedSub.setAmount(new BigDecimal("999.00"));
        savedSub.setActive(true);
        savedSub.setCreatedAt(LocalDateTime.now());

        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(savedSub);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        SubscriptionDto result = subscriptionService.subscribe(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getPlan()).isEqualTo(Plan.BASIC);
        assertThat(result.getActive()).isTrue();
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("999.00"));
        verify(userRepository).save(argThat(User::getPremium));
    }

    @Test
    void subscribe_goldPlan_shouldSetCorrectDurationAndPrice() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setPlan(Plan.GOLD);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findByUserIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> {
            Subscription sub = inv.getArgument(0);
            sub.setId(2L);
            return sub;
        });
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        SubscriptionDto result = subscriptionService.subscribe(1L, request);

        assertThat(result.getPlan()).isEqualTo(Plan.GOLD);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("2499.00"));
        assertThat(result.getEndDate()).isEqualTo(result.getStartDate().plusDays(90));
    }

    @Test
    void subscribe_withExistingActiveSubscription_shouldDeactivateOldOne() {
        Subscription existingSub = new Subscription();
        existingSub.setId(10L);
        existingSub.setUser(testUser);
        existingSub.setPlan(Plan.BASIC);
        existingSub.setActive(true);

        SubscriptionRequest request = new SubscriptionRequest();
        request.setPlan(Plan.PLATINUM);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.findByUserIdAndActiveTrue(1L)).thenReturn(Optional.of(existingSub));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        subscriptionService.subscribe(1L, request);

        verify(subscriptionRepository, atLeastOnce()).save(argThat(sub ->
                sub.getId() != null && sub.getId().equals(10L) && !sub.getActive()));
    }

    @Test
    void getActiveSubscription_shouldReturnActiveSubscription() {
        Subscription activeSub = new Subscription();
        activeSub.setId(1L);
        activeSub.setUser(testUser);
        activeSub.setPlan(Plan.GOLD);
        activeSub.setStartDate(LocalDate.now());
        activeSub.setEndDate(LocalDate.now().plusDays(90));
        activeSub.setActive(true);
        activeSub.setCreatedAt(LocalDateTime.now());

        when(subscriptionRepository.findByUserIdAndActiveTrue(1L)).thenReturn(Optional.of(activeSub));

        SubscriptionDto result = subscriptionService.getActiveSubscription(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPlan()).isEqualTo(Plan.GOLD);
        assertThat(result.getActive()).isTrue();
    }

    @Test
    void getActiveSubscription_withNoActiveSub_shouldReturnNull() {
        when(subscriptionRepository.findByUserIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        SubscriptionDto result = subscriptionService.getActiveSubscription(1L);

        assertThat(result).isNull();
    }

    @Test
    void getSubscriptionHistory_shouldReturnAllSubscriptions() {
        Subscription sub1 = new Subscription();
        sub1.setId(1L);
        sub1.setUser(testUser);
        sub1.setPlan(Plan.BASIC);
        sub1.setStartDate(LocalDate.now().minusDays(60));
        sub1.setEndDate(LocalDate.now().minusDays(30));
        sub1.setActive(false);
        sub1.setCreatedAt(LocalDateTime.now().minusDays(60));

        Subscription sub2 = new Subscription();
        sub2.setId(2L);
        sub2.setUser(testUser);
        sub2.setPlan(Plan.GOLD);
        sub2.setStartDate(LocalDate.now());
        sub2.setEndDate(LocalDate.now().plusDays(90));
        sub2.setActive(true);
        sub2.setCreatedAt(LocalDateTime.now());

        when(subscriptionRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(sub2, sub1));

        List<SubscriptionDto> history = subscriptionService.getSubscriptionHistory(1L);

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getPlan()).isEqualTo(Plan.GOLD);
    }
}
