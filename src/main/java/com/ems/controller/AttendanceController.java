package com.ems.controller;

import com.ems.entity.Attendance;
import com.ems.entity.Employee;
import com.ems.entity.User;
import com.ems.repository.UserRepository;
import com.ems.service.AttendanceService;
import com.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
    private final UserRepository userRepository;

    // ---- EMPLOYEE: Check-In/Out Dashboard ----
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String todayAttendance(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Employee employee = getLoggedInEmployee(userDetails);
        Optional<Attendance> todayRecord = attendanceService.findTodayAttendance(employee.getId());

        model.addAttribute("employee",    employee);
        model.addAttribute("attendance",  todayRecord.orElse(null));
        model.addAttribute("hasCheckedIn",  todayRecord.isPresent());
        model.addAttribute("hasCheckedOut", todayRecord.isPresent() && todayRecord.get().getCheckOut() != null);
        model.addAttribute("pageTitle",   "Today's Attendance");
        return "attendance/today";
    }

    @PostMapping("/checkin")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String checkIn(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            Employee employee = getLoggedInEmployee(userDetails);
            attendanceService.checkIn(employee.getId());
            ra.addFlashAttribute("successMessage", "✅ Check-in recorded at " + java.time.LocalTime.now().withNano(0));
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/attendance/today";
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String checkOut(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            Employee employee = getLoggedInEmployee(userDetails);
            attendanceService.checkOut(employee.getId());
            ra.addFlashAttribute("successMessage", "✅ Check-out recorded successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/attendance/today";
    }

    // ---- EMPLOYEE: View own attendance history ----
    @GetMapping("/my-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String myHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Employee employee = getLoggedInEmployee(userDetails);
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Attendance> attendancePage = attendanceService.searchAttendance(
                employee.getId(), null, null, pageable);

        model.addAttribute("attendance",    attendancePage.getContent());
        model.addAttribute("currentPage",   page);
        model.addAttribute("totalPages",    attendancePage.getTotalPages());
        model.addAttribute("employee",      employee);
        model.addAttribute("pageTitle",     "My Attendance History");
        return "attendance/history";
    }

    // ---- ADMIN: View All Attendance ----
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String adminList(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Attendance> attendancePage = attendanceService.searchAttendance(
                employeeId, fromDate, toDate, pageable);

        model.addAttribute("attendance",    attendancePage.getContent());
        model.addAttribute("currentPage",   page);
        model.addAttribute("totalPages",    attendancePage.getTotalPages());
        model.addAttribute("totalItems",    attendancePage.getTotalElements());
        model.addAttribute("employees",     employeeService.findAll());
        model.addAttribute("selectedEmpId", employeeId);
        model.addAttribute("fromDate",      fromDate);
        model.addAttribute("toDate",        toDate);
        model.addAttribute("pageTitle",     "Attendance Records");
        return "attendance/list";
    }

    // ---- Helper ----
    private Employee getLoggedInEmployee(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return employeeService.findByUserId(user.getId());
    }
}
