package com.matrimony.service;

import com.matrimony.dto.InterestDto;
import com.matrimony.entity.Interest;
import com.matrimony.entity.Interest.Status;
import com.matrimony.entity.User;
import com.matrimony.repository.InterestRepository;
import com.matrimony.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public InterestDto sendInterest(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot send interest to yourself");
        }

        if (interestRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId)) {
            throw new IllegalStateException("Interest already sent");
        }

        User fromUser = getUserOrThrow(fromUserId);
        User toUser = getUserOrThrow(toUserId);

        Interest interest = new Interest();
        interest.setFromUser(fromUser);
        interest.setToUser(toUser);
        interest.setStatus(Status.PENDING);
        interest = interestRepository.save(interest);

        notificationService.sendNotification(toUser, "New Interest",
                fromUser.getName() + " has sent you an interest!", "INTEREST");

        return toDto(interest);
    }

    @Transactional
    public InterestDto respondToInterest(Long interestId, Long userId, Status newStatus) {
        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("Interest not found"));

        if (!interest.getToUser().getId().equals(userId)) {
            throw new SecurityException("Not authorized to respond to this interest");
        }

        interest.setStatus(newStatus);
        interest = interestRepository.save(interest);

        if (newStatus == Status.ACCEPTED) {
            notificationService.sendNotification(interest.getFromUser(), "Interest Accepted",
                    interest.getToUser().getName() + " accepted your interest!", "INTEREST_ACCEPTED");
        }

        return toDto(interest);
    }

    public List<InterestDto> getSentInterests(Long userId) {
        return interestRepository.findByFromUserId(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<InterestDto> getReceivedInterests(Long userId) {
        return interestRepository.findByToUserId(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<InterestDto> getPendingInterests(Long userId) {
        return interestRepository.findByToUserIdAndStatus(userId, Status.PENDING)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private InterestDto toDto(Interest interest) {
        InterestDto dto = new InterestDto();
        dto.setId(interest.getId());
        dto.setFromUserId(interest.getFromUser().getId());
        dto.setFromUserName(interest.getFromUser().getName());
        dto.setToUserId(interest.getToUser().getId());
        dto.setToUserName(interest.getToUser().getName());
        dto.setStatus(interest.getStatus());
        dto.setCreatedAt(interest.getCreatedAt());

        if (interest.getFromUser().getProfile() != null) {
            dto.setFromUserPhoto(interest.getFromUser().getProfile().getPhotoUrl());
        }

        return dto;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
