package com.matrimony.service;

import com.matrimony.dto.BasicInfoRequest;
import com.matrimony.dto.ProfileDto;
import com.matrimony.dto.ProfileUpdateRequest;
import com.matrimony.entity.Profile;
import com.matrimony.entity.User;
import com.matrimony.repository.ProfileRepository;
import com.matrimony.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveBasicInfo(Long userId, BasicInfoRequest request) {
        User user = getUserOrThrow(userId);
        user.setName(request.getName());
        user.setDob(request.getDob());
        user.setGender(request.getGender());
        userRepository.save(user);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUser(user);
                    return p;
                });
        profile.setHeight(request.getHeight());
        profile.setMaritalStatus(request.getMaritalStatus());
        profileRepository.save(profile);
    }

    @Transactional
    public ProfileDto updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = getUserOrThrow(userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUser(user);
                    return p;
                });

        if (request.getReligion() != null) profile.setReligion(request.getReligion());
        if (request.getCaste() != null) profile.setCaste(request.getCaste());
        if (request.getCommunity() != null) profile.setCommunity(request.getCommunity());
        if (request.getEducation() != null) profile.setEducation(request.getEducation());
        if (request.getOccupation() != null) profile.setOccupation(request.getOccupation());
        if (request.getSalary() != null) profile.setSalary(request.getSalary());
        if (request.getSmoking() != null) profile.setSmoking(request.getSmoking());
        if (request.getDrinking() != null) profile.setDrinking(request.getDrinking());
        if (request.getDiet() != null) profile.setDiet(request.getDiet());
        if (request.getCountry() != null) profile.setCountry(request.getCountry());
        if (request.getState() != null) profile.setState(request.getState());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getHeight() != null) profile.setHeight(request.getHeight());
        if (request.getMaritalStatus() != null) profile.setMaritalStatus(request.getMaritalStatus());
        if (request.getAbout() != null) profile.setAbout(request.getAbout());
        if (request.getPhotoUrl() != null) profile.setPhotoUrl(request.getPhotoUrl());

        profile.setProfileCompletion(calculateCompletion(user, profile));
        profile = profileRepository.save(profile);

        return toDto(user, profile);
    }

    public ProfileDto getProfile(Long userId) {
        User user = getUserOrThrow(userId);
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUser(user);
                    p.setProfileCompletion(0);
                    p.setVerified(false);
                    return p;
                });
        return toDto(user, profile);
    }

    @Transactional
    public void completeOnboarding(Long userId) {
        User user = getUserOrThrow(userId);
        user.setOnboardingComplete(true);
        userRepository.save(user);
    }

    private int calculateCompletion(User user, Profile profile) {
        int score = 0;
        int total = 10;

        if (user.getName() != null && !user.getName().isBlank()) score++;
        if (user.getDob() != null) score++;
        if (user.getGender() != null) score++;
        if (profile.getReligion() != null) score++;
        if (profile.getEducation() != null) score++;
        if (profile.getOccupation() != null) score++;
        if (profile.getCity() != null) score++;
        if (profile.getAbout() != null && !profile.getAbout().isBlank()) score++;
        if (profile.getPhotoUrl() != null && !profile.getPhotoUrl().isBlank()) score++;
        if (profile.getHeight() != null) score++;

        return (score * 100) / total;
    }

    public ProfileDto toDto(User user, Profile profile) {
        ProfileDto dto = new ProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setGender(user.getGender());
        dto.setPremium(user.getPremium());

        if (user.getDob() != null) {
            dto.setAge(Period.between(user.getDob(), LocalDate.now()).getYears());
        }

        dto.setHeight(profile.getHeight());
        dto.setMaritalStatus(profile.getMaritalStatus());
        dto.setReligion(profile.getReligion());
        dto.setCaste(profile.getCaste());
        dto.setCommunity(profile.getCommunity());
        dto.setEducation(profile.getEducation());
        dto.setOccupation(profile.getOccupation());
        dto.setSalary(profile.getSalary());
        dto.setSmoking(profile.getSmoking());
        dto.setDrinking(profile.getDrinking());
        dto.setDiet(profile.getDiet());
        dto.setCountry(profile.getCountry());
        dto.setState(profile.getState());
        dto.setCity(profile.getCity());
        dto.setAbout(profile.getAbout());
        dto.setPhotoUrl(profile.getPhotoUrl());
        dto.setProfileCompletion(profile.getProfileCompletion());
        dto.setVerified(profile.getVerified());

        return dto;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
