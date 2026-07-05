package com.ems.controller;

import com.ems.dto.DashboardStatsDTO;
import com.ems.service.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Admin dashboard controller — displays analytics, stat cards, and Chart.js charts.
 */
@Controller
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public String dashboard(Model model) throws Exception {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);

        // Serialize chart data to JSON for Chart.js
        model.addAttribute("deptLabels",   objectMapper.writeValueAsString(stats.getDepartmentEmployeeCount().keySet()));
        model.addAttribute("deptData",     objectMapper.writeValueAsString(stats.getDepartmentEmployeeCount().values()));
        model.addAttribute("trendLabels",  objectMapper.writeValueAsString(stats.getMonthlyJoiningTrend().keySet()));
        model.addAttribute("trendData",    objectMapper.writeValueAsString(stats.getMonthlyJoiningTrend().values()));
        model.addAttribute("leaveLabels",  objectMapper.writeValueAsString(stats.getLeaveTypeBreakdown().keySet()));
        model.addAttribute("leaveData",    objectMapper.writeValueAsString(stats.getLeaveTypeBreakdown().values()));

        return "dashboard/index";
    }
}
