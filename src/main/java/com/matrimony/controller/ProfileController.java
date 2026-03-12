package com.matrimony.controller;

import com.matrimony.dto.ApiResponse;
import com.matrimony.dto.BasicInfoRequest;
import com.matrimony.dto.PartnerPreferenceDto;
import com.matrimony.dto.ProfileDto;
import com.matrimony.dto.ProfileUpdateRequest;
import com.matrimony.entity.PartnerPreference;
import com.matrimony.entity.User;
import com.matrimony.repository.PartnerPreferenceRepository;
import com.matrimony.repository.UserRepository;
import com.matrimony.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final PartnerPreferenceRepository partnerPreferenceRepository;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileDto>> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        ProfileDto profile = profileService.getProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileDto>> getProfile(@PathVariable Long userId) {
        ProfileDto profile = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PostMapping("/basic-info")
    public ResponseEntity<ApiResponse<Void>> saveBasicInfo(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody BasicInfoRequest request) {
        profileService.saveBasicInfo(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Basic info saved", null));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<ProfileDto>> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody ProfileUpdateRequest request) {
        ProfileDto updated = profileService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", updated));
    }

    @PostMapping("/complete-onboarding")
    public ResponseEntity<ApiResponse<Void>> completeOnboarding(
            @AuthenticationPrincipal User currentUser) {
        profileService.completeOnboarding(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Onboarding completed", null));
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<PartnerPreferenceDto>> getPreferences(
            @AuthenticationPrincipal User currentUser) {
        PartnerPreference pref = partnerPreferenceRepository
                .findByUserId(currentUser.getId())
                .orElse(null);
        return ResponseEntity.ok(ApiResponse.success(toPreferenceDto(pref)));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<PartnerPreferenceDto>> updatePreferences(
            @AuthenticationPrincipal User currentUser,
            @RequestBody PartnerPreferenceDto request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PartnerPreference pref = partnerPreferenceRepository
                .findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    PartnerPreference p = new PartnerPreference();
                    p.setUser(user);
                    return p;
                });

        pref.setMinAge(request.getMinAge());
        pref.setMaxAge(request.getMaxAge());
        pref.setMinHeight(request.getMinHeight());
        pref.setMaxHeight(request.getMaxHeight());
        pref.setReligion(request.getReligion());
        pref.setCaste(request.getCaste());
        pref.setEducation(request.getEducation());
        pref.setOccupation(request.getOccupation());
        pref.setLocation(request.getLocation());

        pref = partnerPreferenceRepository.save(pref);
        return ResponseEntity.ok(ApiResponse.success("Preferences updated", toPreferenceDto(pref)));
    }

    private PartnerPreferenceDto toPreferenceDto(PartnerPreference pref) {
        if (pref == null) return null;
        PartnerPreferenceDto dto = new PartnerPreferenceDto();
        dto.setId(pref.getId());
        dto.setUserId(pref.getUser().getId());
        dto.setMinAge(pref.getMinAge());
        dto.setMaxAge(pref.getMaxAge());
        dto.setMinHeight(pref.getMinHeight());
        dto.setMaxHeight(pref.getMaxHeight());
        dto.setReligion(pref.getReligion());
        dto.setCaste(pref.getCaste());
        dto.setEducation(pref.getEducation());
        dto.setOccupation(pref.getOccupation());
        dto.setLocation(pref.getLocation());
        return dto;
    }
}
