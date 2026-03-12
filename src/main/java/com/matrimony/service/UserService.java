package com.matrimony.service;

import com.matrimony.entity.User;
import com.matrimony.repository.ReportRepository;
import com.matrimony.repository.SubscriptionRepository;
import com.matrimony.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ReportRepository reportRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Transactional
    public void banUser(Long id) {
        User user = getUserById(id);
        user.setBanned(true);
        userRepository.save(user);
    }

    @Transactional
    public void unbanUser(Long id) {
        User user = getUserById(id);
        user.setBanned(false);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void upgradeToPremium(Long id) {
        User user = getUserById(id);
        user.setPremium(true);
        userRepository.save(user);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByBannedFalse());
        stats.put("premiumUsers", userRepository.countByPremiumTrue());
        stats.put("revenue", subscriptionRepository.getTotalRevenue());
        stats.put("activeSubscriptions", subscriptionRepository.countByActiveTrue());
        stats.put("pendingReports", reportRepository.countByStatus(
                com.matrimony.entity.Report.Status.PENDING));
        return stats;
    }
}
