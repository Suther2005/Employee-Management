package com.ems.controller;

import com.ems.dto.EmployeeDTO;
import com.ems.entity.Employee;
import com.ems.service.DepartmentService;
import com.ems.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Employee CRUD controller — handles list, add, edit, delete, view.
 * Admin-only except for view profile.
 */
@Controller
@RequestMapping("/employees")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    // ---- LIST ----
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employeePage = employeeService.findAll(keyword, pageable);

        model.addAttribute("employees",   employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  employeePage.getTotalPages());
        model.addAttribute("totalItems",  employeePage.getTotalElements());
        model.addAttribute("keyword",     keyword);
        model.addAttribute("sortBy",      sortBy);
        model.addAttribute("sortDir",     sortDir);
        model.addAttribute("size",        size);
        model.addAttribute("pageTitle",   "Employees");
        return "employee/list";
    }

    // ---- ADD FORM ----
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employeeDTO",  new EmployeeDTO());
        model.addAttribute("departments",  departmentService.findAll());
        model.addAttribute("genders",      Employee.Gender.values());
        model.addAttribute("statuses",     Employee.EmployeeStatus.values());
        model.addAttribute("pageTitle",    "Add Employee");
        return "employee/form";
    }

    // ---- SAVE ----
    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute EmployeeDTO employeeDTO,
            BindingResult result,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("genders",     Employee.Gender.values());
            model.addAttribute("statuses",    Employee.EmployeeStatus.values());
            model.addAttribute("pageTitle",   "Add Employee");
            return "employee/form";
        }

        try {
            employeeService.save(employeeDTO, photo);
            redirectAttributes.addFlashAttribute("successMessage", "Employee added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/employees";
    }

    // ---- EDIT FORM ----
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employeeDTO",  employeeService.toDTO(employee));
        model.addAttribute("departments",  departmentService.findAll());
        model.addAttribute("genders",      Employee.Gender.values());
        model.addAttribute("statuses",     Employee.EmployeeStatus.values());
        model.addAttribute("employee",     employee);
        model.addAttribute("pageTitle",    "Edit Employee");
        return "employee/form";
    }

    // ---- UPDATE ----
    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute EmployeeDTO employeeDTO,
            BindingResult result,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("genders",     Employee.Gender.values());
            model.addAttribute("statuses",    Employee.EmployeeStatus.values());
            model.addAttribute("pageTitle",   "Edit Employee");
            return "employee/form";
        }

        try {
            employeeService.update(id, employeeDTO, photo);
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/employees";
    }

    // ---- VIEW ----
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employee",  employee);
        model.addAttribute("pageTitle", "Employee Details — " + employee.getFullName());
        return "employee/view";
    }

    // ---- DELETE ----
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/employees";
    }
}
