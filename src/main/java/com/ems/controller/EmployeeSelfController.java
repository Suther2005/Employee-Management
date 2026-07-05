package com.ems.controller;

import com.ems.entity.Employee;
import com.ems.entity.User;
import com.ems.repository.UserRepository;
import com.ems.service.AttendanceService;
import com.ems.service.EmployeeService;
import com.ems.service.LeaveService;
import com.ems.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Employee-facing controller — serves the employee self-service dashboard
 * and routes to employee-specific views.
 */
@Controller
@RequestMapping("/employee")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeSelfController {

    private final EmployeeService employeeService;
    private final UserRepository userRepository;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;
    private final SalaryService salaryService;

    @GetMapping("/dashboard")
    public String employeeDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Employee employee = employeeService.findByUserId(user.getId());

        // Today's attendance
        model.addAttribute("employee",         employee);
        model.addAttribute("todayAttendance",  attendanceService.findTodayAttendance(employee.getId()).orElse(null));
        model.addAttribute("pendingLeaves",    leaveService.findByEmployeeId(employee.getId()).stream()
                .filter(l -> l.getStatus() == com.ems.entity.LeaveRequest.LeaveStatus.PENDING).count());
        model.addAttribute("salary",           salaryService.findByEmployeeId(employee.getId()).orElse(null));
        model.addAttribute("pageTitle",        "Employee Dashboard");

        return "employee/dashboard";
    }
}
