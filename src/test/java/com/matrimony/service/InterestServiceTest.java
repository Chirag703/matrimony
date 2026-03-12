package com.matrimony.service;

import com.matrimony.dto.InterestDto;
import com.matrimony.entity.Interest;
import com.matrimony.entity.Interest.Status;
import com.matrimony.entity.User;
import com.matrimony.repository.InterestRepository;
import com.matrimony.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InterestService interestService;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userA = new User();
        userA.setId(1L);
        userA.setPhone("+911111111111");
        userA.setName("User A");

        userB = new User();
        userB.setId(2L);
        userB.setPhone("+912222222222");
        userB.setName("User B");
    }

    @Test
    void sendInterest_shouldCreateInterest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userB));
        when(interestRepository.existsByFromUserIdAndToUserId(1L, 2L)).thenReturn(false);

        Interest savedInterest = new Interest();
        savedInterest.setId(1L);
        savedInterest.setFromUser(userA);
        savedInterest.setToUser(userB);
        savedInterest.setStatus(Status.PENDING);
        savedInterest.setCreatedAt(LocalDateTime.now());

        when(interestRepository.save(any(Interest.class))).thenReturn(savedInterest);
        doNothing().when(notificationService).sendNotification(any(User.class), anyString(), anyString(), anyString());

        InterestDto result = interestService.sendInterest(1L, 2L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.PENDING);
        assertThat(result.getFromUserId()).isEqualTo(1L);
        assertThat(result.getToUserId()).isEqualTo(2L);
        verify(notificationService).sendNotification(eq(userB), anyString(), anyString(), anyString());
    }

    @Test
    void sendInterest_toSelf_shouldThrowException() {
        assertThatThrownBy(() -> interestService.sendInterest(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot send interest to yourself");
    }

    @Test
    void sendInterest_duplicate_shouldThrowException() {
        when(interestRepository.existsByFromUserIdAndToUserId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> interestService.sendInterest(1L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Interest already sent");
    }

    @Test
    void respondToInterest_accept_shouldUpdateStatus() {
        Interest interest = new Interest();
        interest.setId(1L);
        interest.setFromUser(userA);
        interest.setToUser(userB);
        interest.setStatus(Status.PENDING);
        interest.setCreatedAt(LocalDateTime.now());

        when(interestRepository.findById(1L)).thenReturn(Optional.of(interest));
        when(interestRepository.save(any(Interest.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationService).sendNotification(any(User.class), anyString(), anyString(), anyString());

        InterestDto result = interestService.respondToInterest(1L, 2L, Status.ACCEPTED);

        assertThat(result.getStatus()).isEqualTo(Status.ACCEPTED);
        verify(notificationService).sendNotification(eq(userA), anyString(), anyString(), anyString());
    }

    @Test
    void respondToInterest_byWrongUser_shouldThrowException() {
        Interest interest = new Interest();
        interest.setId(1L);
        interest.setFromUser(userA);
        interest.setToUser(userB);
        interest.setStatus(Status.PENDING);

        when(interestRepository.findById(1L)).thenReturn(Optional.of(interest));

        assertThatThrownBy(() -> interestService.respondToInterest(1L, 99L, Status.ACCEPTED))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void getReceivedInterests_shouldReturnList() {
        Interest interest = new Interest();
        interest.setId(1L);
        interest.setFromUser(userA);
        interest.setToUser(userB);
        interest.setStatus(Status.PENDING);
        interest.setCreatedAt(LocalDateTime.now());

        when(interestRepository.findByToUserId(2L)).thenReturn(List.of(interest));

        List<InterestDto> results = interestService.getReceivedInterests(2L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFromUserId()).isEqualTo(1L);
    }
}
