package com.matrimony.admin;

import com.matrimony.entity.Profile;
import com.matrimony.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/profiles")
@RequiredArgsConstructor
public class AdminProfileController {

    private final ProfileRepository profileRepository;

    @GetMapping
    public String listProfiles(Model model) {
        List<Profile> profiles = profileRepository.findAll();
        model.addAttribute("profiles", profiles);
        return "admin/profiles";
    }

    @PostMapping("/verify/{id}")
    public String verifyProfile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        profileRepository.findById(id).ifPresent(profile -> {
            profile.setVerified(true);
            profileRepository.save(profile);
        });
        redirectAttributes.addFlashAttribute("successMessage", "Profile verified");
        return "redirect:/admin/profiles";
    }

    @PostMapping("/reject/{id}")
    public String rejectProfile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        profileRepository.findById(id).ifPresent(profile -> {
            profile.setVerified(false);
            profileRepository.save(profile);
        });
        redirectAttributes.addFlashAttribute("successMessage", "Profile rejected");
        return "redirect:/admin/profiles";
    }
}
