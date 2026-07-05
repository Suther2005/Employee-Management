package com.ems.controller;

import com.ems.dto.LeaveRequestDTO;
import com.ems.entity.Employee;
import com.ems.entity.LeaveRequest;
import com.ems.entity.User;
import com.ems.repository.UserRepository;
import com.ems.service.EmployeeService;
import com.ems.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;
    private final EmployeeService employeeService;
    private final UserRepository userRepository;

    // ---- EMPLOYEE: Apply Leave ----
    @GetMapping("/apply")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String applyForm(Model model) {
        model.addAttribute("leaveRequestDTO", new LeaveRequestDTO());
        model.addAttribute("leaveTypes", LeaveRequest.LeaveType.values());
        model.addAttribute("pageTitle",  "Apply for Leave");
        return "leave/apply";
    }

    @PostMapping("/apply")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String applyLeave(
            @Valid @ModelAttribute LeaveRequestDTO leaveRequestDTO,
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("leaveTypes", LeaveRequest.LeaveType.values());
            model.addAttribute("pageTitle",  "Apply for Leave");
            return "leave/apply";
        }

        try {
            Employee employee = getLoggedInEmployee(userDetails);
            leaveService.applyLeave(employee.getId(), leaveRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Leave application submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/leave/my-leaves";
    }

    // ---- EMPLOYEE: My Leaves ----
    @GetMapping("/my-leaves")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String myLeaves(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Employee employee = getLoggedInEmployee(userDetails);
        model.addAttribute("leaves",    leaveService.findByEmployeeId(employee.getId()));
        model.addAttribute("pageTitle", "My Leave History");
        return "leave/my-leaves";
    }

    // ---- EMPLOYEE: Cancel Leave ----
    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String cancelLeave(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            Employee employee = getLoggedInEmployee(userDetails);
            leaveService.cancelLeave(id, employee.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Leave request cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/leave/my-leaves";
    }

    // ---- ADMIN: All Leaves ----
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String adminList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedDate").descending());
        Page<LeaveRequest> leavePage = leaveService.findAll(pageable);

        model.addAttribute("leaves",       leavePage.getContent());
        model.addAttribute("currentPage",  page);
        model.addAttribute("totalPages",   leavePage.getTotalPages());
        model.addAttribute("totalItems",   leavePage.getTotalElements());
        model.addAttribute("pendingCount", leaveService.countPending());
        model.addAttribute("pageTitle",    "Leave Management");
        return "leave/list";
    }

    // ---- ADMIN: Approve ----
    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String approve(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            leaveService.approveLeave(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Leave approved and email sent.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/leave";
    }

    // ---- ADMIN: Reject ----
    @PostMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String reject(
            @PathVariable Long id,
            @RequestParam String rejectionReason,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            leaveService.rejectLeave(id, userDetails.getUsername(), rejectionReason);
            redirectAttributes.addFlashAttribute("successMessage", "Leave rejected and employee notified.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/leave";
    }

    // ---- ADMIN: View Leave Detail ----
    @GetMapping("/view/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewLeave(@PathVariable Long id, Model model) {
        LeaveRequest leave = leaveService.findById(id);
        model.addAttribute("leave",     leave);
        model.addAttribute("pageTitle", "Leave Request Details");
        return "leave/view";
    }

    private Employee getLoggedInEmployee(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return employeeService.findByUserId(user.getId());
    }
}
