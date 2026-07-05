package com.ems.controller;

import com.ems.dto.SalaryDTO;
import com.ems.entity.Employee;
import com.ems.entity.Salary;
import com.ems.entity.User;
import com.ems.repository.UserRepository;
import com.ems.service.EmployeeService;
import com.ems.service.SalaryService;
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

import java.util.Optional;

/**
 * Salary CRUD controller (Admin) + My Salary view (Employee).
 */
@Controller
@RequestMapping("/salary")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;
    private final EmployeeService employeeService;
    private final UserRepository userRepository;

    // ---- ADMIN: List All Salaries ----
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Salary> salaryPage = salaryService.findAll(keyword, pageable);

        model.addAttribute("salaries",    salaryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  salaryPage.getTotalPages());
        model.addAttribute("keyword",     keyword);
        model.addAttribute("pageTitle",   "Salary Management");
        return "salary/list";
    }

    // ---- ADMIN: Add Salary ----
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addForm(Model model) {
        model.addAttribute("salaryDTO",  new SalaryDTO());
        model.addAttribute("employees",  employeeService.findAll());
        model.addAttribute("pageTitle",  "Add Salary Record");
        return "salary/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(
            @Valid @ModelAttribute SalaryDTO salaryDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("employees", employeeService.findAll());
            model.addAttribute("pageTitle",  "Add Salary Record");
            return "salary/form";
        }

        try {
            salaryService.save(salaryDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Salary record added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/salary";
    }

    // ---- ADMIN: Edit Salary ----
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Salary salary = salaryService.findById(id);
        model.addAttribute("salaryDTO",  salaryService.toDTO(salary));
        model.addAttribute("employees",  employeeService.findAll());
        model.addAttribute("pageTitle",  "Edit Salary Record");
        return "salary/form";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute SalaryDTO salaryDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("employees", employeeService.findAll());
            model.addAttribute("pageTitle",  "Edit Salary Record");
            return "salary/form";
        }

        try {
            salaryService.update(id, salaryDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Salary updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/salary";
    }

    // ---- ADMIN: Delete Salary ----
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            salaryService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Salary record deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/salary";
    }

    // ---- EMPLOYEE: View Own Salary ----
    @GetMapping("/my-salary")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public String mySalary(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Employee employee = employeeService.findByUserId(user.getId());
        Optional<Salary> salary = salaryService.findByEmployeeId(employee.getId());

        model.addAttribute("salary",    salary.orElse(null));
        model.addAttribute("employee",  employee);
        model.addAttribute("pageTitle", "My Salary Details");
        return "salary/my-salary";
    }
}
