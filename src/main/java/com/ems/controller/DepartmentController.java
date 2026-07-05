package com.ems.controller;

import com.ems.dto.DepartmentDTO;
import com.ems.entity.Department;
import com.ems.service.DepartmentService;
import com.ems.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/departments")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(Model model) {
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("pageTitle", "Departments");
        return "department/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("departmentDTO", new DepartmentDTO());
        model.addAttribute("pageTitle", "Add Department");
        return "department/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute DepartmentDTO departmentDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Add Department");
            return "department/form";
        }

        try {
            departmentService.save(departmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/departments";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Department dept = departmentService.findById(id);
        model.addAttribute("departmentDTO", departmentService.toDTO(dept));
        model.addAttribute("pageTitle", "Edit Department");
        return "department/form";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute DepartmentDTO departmentDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Department");
            return "department/form";
        }

        try {
            departmentService.update(id, departmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/departments";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Department dept = departmentService.findById(id);
        model.addAttribute("department", dept);
        model.addAttribute("employees",  employeeService.findAll().stream()
                .filter(e -> e.getDepartment() != null && e.getDepartment().getId().equals(id))
                .toList());
        model.addAttribute("pageTitle", "Department: " + dept.getName());
        return "department/view";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/departments";
    }
}
