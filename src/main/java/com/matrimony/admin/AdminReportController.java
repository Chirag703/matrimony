package com.matrimony.admin;

import com.matrimony.entity.Report;
import com.matrimony.entity.Report.Status;
import com.matrimony.repository.ReportRepository;
import com.matrimony.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportRepository reportRepository;
    private final UserService userService;

    @GetMapping
    public String listReports(Model model) {
        List<Report> pending = reportRepository.findByStatus(Status.PENDING);
        List<Report> all = reportRepository.findAll();
        model.addAttribute("pendingReports", pending);
        model.addAttribute("allReports", all);
        return "admin/reports";
    }

    @PostMapping("/suspend/{reportId}")
    public String suspendUser(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        reportRepository.findById(reportId).ifPresent(report -> {
            userService.banUser(report.getReportedUser().getId());
            report.setStatus(Status.RESOLVED);
            reportRepository.save(report);
        });
        redirectAttributes.addFlashAttribute("successMessage", "User suspended");
        return "redirect:/admin/reports";
    }

    @PostMapping("/warn/{reportId}")
    public String warnUser(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        reportRepository.findById(reportId).ifPresent(report -> {
            report.setStatus(Status.REVIEWED);
            reportRepository.save(report);
        });
        redirectAttributes.addFlashAttribute("successMessage", "User warned");
        return "redirect:/admin/reports";
    }

    @PostMapping("/dismiss/{reportId}")
    public String dismissReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        reportRepository.findById(reportId).ifPresent(report -> {
            report.setStatus(Status.RESOLVED);
            reportRepository.save(report);
        });
        redirectAttributes.addFlashAttribute("successMessage", "Report dismissed");
        return "redirect:/admin/reports";
    }
}
