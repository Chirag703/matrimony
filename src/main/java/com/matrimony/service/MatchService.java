package com.matrimony.service;

import com.matrimony.dto.ProfileDto;
import com.matrimony.entity.Profile;
import com.matrimony.entity.User;
import com.matrimony.repository.ProfileRepository;
import com.matrimony.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    /**
     * Find potential matches for a user.
     * Ranking: premium DESC, last_active DESC, profile_completion DESC
     * Free users limited to 10 profiles/day.
     */
    public List<ProfileDto> findMatches(Long userId, int limit) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String oppositeGender = "MALE".equalsIgnoreCase(currentUser.getGender()) ? "FEMALE" : "MALE";

        List<User> candidates = userRepository.findMatchesByGender(userId, oppositeGender);

        int maxResults = currentUser.getPremium() ? candidates.size() : Math.min(limit, 10);

        return candidates.stream()
                .limit(maxResults)
                .map(user -> {
                    Profile profile = profileRepository.findByUserId(user.getId())
                            .orElseGet(() -> {
                                Profile p = new Profile();
                                p.setUser(user);
                                p.setProfileCompletion(0);
                                p.setVerified(false);
                                return p;
                            });
                    return profileService.toDto(user, profile);
                })
                .sorted((a, b) -> {
                    int premiumCmp = Boolean.compare(
                            b.getPremium() != null && b.getPremium(),
                            a.getPremium() != null && a.getPremium());
                    if (premiumCmp != 0) return premiumCmp;
                    int completionA = a.getProfileCompletion() != null ? a.getProfileCompletion() : 0;
                    int completionB = b.getProfileCompletion() != null ? b.getProfileCompletion() : 0;
                    return Integer.compare(completionB, completionA);
                })
                .collect(Collectors.toList());
    }
}
