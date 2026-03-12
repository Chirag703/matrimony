package com.matrimony.admin;

import com.matrimony.entity.User;
import com.matrimony.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/user-detail";
    }

    @PostMapping("/ban/{id}")
    public String banUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.banUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User banned successfully");
        return "redirect:/admin/users";
    }

    @PostMapping("/unban/{id}")
    public String unbanUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.unbanUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User unbanned successfully");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        return "redirect:/admin/users";
    }

    @PostMapping("/upgrade-premium/{id}")
    public String upgradeToPremium(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.upgradeToPremium(id);
        redirectAttributes.addFlashAttribute("successMessage", "User upgraded to premium");
        return "redirect:/admin/users";
    }
}
