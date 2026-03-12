package com.matrimony.controller;

import com.matrimony.dto.ApiResponse;
import com.matrimony.dto.ProfileDto;
import com.matrimony.entity.User;
import com.matrimony.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * Get potential matches.
     * Free users: max 10 profiles.
     * Premium users: unlimited.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileDto>>> getMatches(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "10") int limit) {
        List<ProfileDto> matches = matchService.findMatches(currentUser.getId(), limit);
        return ResponseEntity.ok(ApiResponse.success(matches));
    }
}
