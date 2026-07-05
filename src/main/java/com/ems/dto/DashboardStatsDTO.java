package com.ems.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * DTO carrying all analytics data needed by the admin dashboard.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    // Summary cards
    private long totalEmployees;
    private long activeEmployees;
    private long totalDepartments;
    private long todayAttendance;
    private long pendingLeaves;
    private long monthlyNewEmployees;

    // Chart data: department name → employee count
    private Map<String, Long> departmentEmployeeCount;

    // Chart data: month-year label → new employee count
    private Map<String, Long> monthlyJoiningTrend;

    // Chart data: leave type → count
    private Map<String, Long> leaveTypeBreakdown;

    // Attendance summary for the dashboard gauge
    private double attendancePercentageToday;

    // Recent activity
    private List<String> recentActivities;
}
