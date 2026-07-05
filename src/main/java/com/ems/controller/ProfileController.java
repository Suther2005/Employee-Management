package com.ems.controller;

import com.ems.entity.Employee;
import com.ems.entity.User;
import com.ems.repository.UserRepository;
import com.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ems.dto.EmployeeDTO;
import com.ems.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;

/**
 * Profile controller — allows employees to view and edit their own profile.
 */
@Controller
@RequestMapping("/profile")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
@RequiredArgsConstructor
public class ProfileController {

    private final EmployeeService employeeService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:src/main/resources/static/uploads/}")
    private String uploadDir;

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model,
                              RedirectAttributes redirectAttributes) {
        Employee employee = getLoggedInEmployee(userDetails);
        if (employee == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Admin account has no employee profile.");
            return "redirect:/dashboard";
        }
        model.addAttribute("employee",  employee);
        model.addAttribute("pageTitle", "My Profile");
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal UserDetails userDetails, Model model,
                           RedirectAttributes redirectAttributes) {
        Employee employee = getLoggedInEmployee(userDetails);
        if (employee == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Admin account has no employee profile.");
            return "redirect:/dashboard";
        }
        EmployeeDTO dto = employeeService.toDTO(employee);
        model.addAttribute("employeeDTO", dto);
        model.addAttribute("employee",    employee);
        model.addAttribute("pageTitle",   "Edit Profile");
        return "profile/edit";
    }

    @PostMapping("/update")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute EmployeeDTO employeeDTO,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {

        try {
            Employee employee = getLoggedInEmployee(userDetails);
            // Only allow updating personal info, not role/department via this page
            employeeDTO.setDepartmentId(employee.getDepartment() != null
                    ? employee.getDepartment().getId() : null);
            employeeDTO.setEmpId(employee.getEmpId());
            employeeDTO.setStatus(employee.getStatus());

            employeeService.update(employee.getId(), employeeDTO, photo);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        model.addAttribute("pageTitle", "Change Password");
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect.");
                return "redirect:/profile/change-password";
            }

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
                return "redirect:/profile/change-password";
            }

            if (newPassword.length() < 8) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 8 characters.");
                return "redirect:/profile/change-password";
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/profile";
    }

    private Employee getLoggedInEmployee(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            return employeeService.findByUserId(user.getId());
        } catch (Exception e) {
            // Admin user may not have a linked employee record — return null
            return null;
        }
    }
}
