package com.matrimony.admin;

import com.matrimony.entity.Subscription;
import com.matrimony.repository.SubscriptionRepository;
import com.matrimony.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/subscriptions")
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;

    @GetMapping
    public String listSubscriptions(Model model) {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        BigDecimal totalRevenue = subscriptionRepository.getTotalRevenue();
        long activeCount = subscriptionRepository.countByActiveTrue();

        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("activeCount", activeCount);
        return "admin/subscriptions";
    }
}
