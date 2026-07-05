package com.ems.service.impl;

import com.ems.dto.DashboardStatsDTO;
import com.ems.entity.Employee;
import com.ems.entity.LeaveRequest;
import com.ems.repository.*;
import com.ems.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        // Summary cards
        long total       = employeeRepository.count();
        long active      = employeeRepository.countByStatus(Employee.EmployeeStatus.ACTIVE);
        long departments = departmentRepository.count();
        long todayAttn   = attendanceRepository.countPresentToday(today);
        long pendingLeaves = leaveRequestRepository.countByStatus(LeaveRequest.LeaveStatus.PENDING);
        long newThisMonth = employeeRepository.countNewEmployeesThisMonth(currentMonth, currentYear);

        // Attendance percentage
        double attendancePct = total > 0 ? (todayAttn * 100.0) / total : 0.0;

        // Department-wise employee count
        Map<String, Long> deptMap = new LinkedHashMap<>();
        employeeRepository.countByDepartment()
                .forEach(row -> deptMap.put((String) row[0], (Long) row[1]));

        // Monthly joining trend (last 12 months)
        Map<String, Long> trendMap = new LinkedHashMap<>();
        LocalDate fromDate = today.minusMonths(11).withDayOfMonth(1);
        employeeRepository.countMonthlyJoining(fromDate)
                .forEach(row -> {
                    String label = Month.of(((Number) row[0]).intValue()).name().substring(0, 3)
                            + " " + row[1];
                    trendMap.put(label, ((Number) row[2]).longValue());
                });

        // Leave type breakdown
        Map<String, Long> leaveMap = new LinkedHashMap<>();
        leaveRequestRepository.countApprovedByType()
                .forEach(row -> leaveMap.put(row[0].toString(), ((Number) row[1]).longValue()));

        return DashboardStatsDTO.builder()
                .totalEmployees(total)
                .activeEmployees(active)
                .totalDepartments(departments)
                .todayAttendance(todayAttn)
                .pendingLeaves(pendingLeaves)
                .monthlyNewEmployees(newThisMonth)
                .attendancePercentageToday(Math.round(attendancePct * 10.0) / 10.0)
                .departmentEmployeeCount(deptMap)
                .monthlyJoiningTrend(trendMap)
                .leaveTypeBreakdown(leaveMap)
                .build();
    }
}
