package com.matrimony.service;

import com.matrimony.dto.BasicInfoRequest;
import com.matrimony.dto.ProfileDto;
import com.matrimony.dto.ProfileUpdateRequest;
import com.matrimony.entity.Profile;
import com.matrimony.entity.User;
import com.matrimony.repository.ProfileRepository;
import com.matrimony.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    private User testUser;
    private Profile testProfile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setPhone("+919876543210");
        testUser.setName("Test User");
        testUser.setGender("MALE");
        testUser.setDob(LocalDate.of(1995, 5, 15));
        testUser.setPremium(false);
        testUser.setBanned(false);
        testUser.setOnboardingComplete(false);

        testProfile = new Profile();
        testProfile.setId(1L);
        testProfile.setUser(testUser);
        testProfile.setCity("Mumbai");
        testProfile.setReligion("Hindu");
        testProfile.setEducation("B.Tech");
        testProfile.setProfileCompletion(50);
        testProfile.setVerified(false);
    }

    @Test
    void getProfile_shouldReturnProfileDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));

        ProfileDto result = profileService.getProfile(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getCity()).isEqualTo("Mumbai");
        assertThat(result.getReligion()).isEqualTo("Hindu");
        assertThat(result.getAge()).isGreaterThan(0);
    }

    @Test
    void getProfile_withNoProfile_shouldReturnEmptyProfileDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ProfileDto result = profileService.getProfile(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getProfileCompletion()).isEqualTo(0);
    }

    @Test
    void getProfile_withNonExistentUser_shouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getProfile(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void saveBasicInfo_shouldUpdateUserAndProfile() {
        BasicInfoRequest request = new BasicInfoRequest();
        request.setName("Updated Name");
        request.setDob(LocalDate.of(1995, 5, 15));
        request.setGender("MALE");
        request.setHeight("5'10\"");
        request.setMaritalStatus("Never Married");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);

        profileService.saveBasicInfo(1L, request);

        verify(userRepository).save(argThat(user -> "Updated Name".equals(user.getName())));
        verify(profileRepository).save(argThat(profile -> "5'10\"".equals(profile.getHeight())));
    }

    @Test
    void updateProfile_shouldUpdateFields() {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setReligion("Hindu");
        request.setCaste("Brahmin");
        request.setCity("Delhi");
        request.setEducation("MBA");
        request.setAbout("I am a software engineer looking for a life partner");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileDto result = profileService.updateProfile(1L, request);

        assertThat(result.getReligion()).isEqualTo("Hindu");
        assertThat(result.getCaste()).isEqualTo("Brahmin");
        assertThat(result.getCity()).isEqualTo("Delhi");
    }

    @Test
    void completeOnboarding_shouldSetOnboardingComplete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        profileService.completeOnboarding(1L);

        verify(userRepository).save(argThat(User::getOnboardingComplete));
    }

    @Test
    void toDto_shouldCalculateAgeCorrectly() {
        testUser.setDob(LocalDate.now().minusYears(30));
        ProfileDto dto = profileService.toDto(testUser, testProfile);
        assertThat(dto.getAge()).isEqualTo(30);
    }

    @Test
    void calculateCompletion_shouldReturnHighScoreForCompleteProfile() {
        testUser.setName("Full Name");
        testUser.setDob(LocalDate.of(1995, 1, 1));
        testUser.setGender("MALE");
        testProfile.setReligion("Hindu");
        testProfile.setEducation("B.Tech");
        testProfile.setOccupation("Engineer");
        testProfile.setCity("Mumbai");
        testProfile.setAbout("About me text");
        testProfile.setPhotoUrl("http://photo.url");
        testProfile.setHeight("5'10\"");

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setReligion("Hindu");
        request.setEducation("B.Tech");
        request.setOccupation("Engineer");
        request.setCity("Mumbai");
        request.setAbout("About me text");
        request.setPhotoUrl("http://photo.url");
        request.setHeight("5'10\"");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileDto result = profileService.updateProfile(1L, request);
        assertThat(result.getProfileCompletion()).isGreaterThan(50);
    }
}
