package com.ems.controller;

import com.ems.service.EmployeeService;
import com.ems.service.LeaveService;
import com.ems.service.SalaryService;
import com.ems.util.ExcelExportUtil;
import com.ems.util.PdfExportUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Report controller — handles Excel and PDF export for employees, leaves, and salaries.
 */
@Controller
@RequestMapping("/reports")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ReportController {

    private final EmployeeService employeeService;
    private final LeaveService leaveService;
    private final SalaryService salaryService;

    @GetMapping
    public String reportsPage(Model model) {
        model.addAttribute("pageTitle", "Reports & Exports");
        return "reports/index";
    }

    // ----- Employee Exports -----
    @GetMapping("/employees/excel")
    public void exportEmployeesExcel(HttpServletResponse response) throws Exception {
        ExcelExportUtil.exportEmployees(employeeService.findAll(), response);
    }

    @GetMapping("/employees/pdf")
    public void exportEmployeesPdf(HttpServletResponse response) throws Exception {
        PdfExportUtil.exportEmployees(employeeService.findAll(), response);
    }

    // ----- Salary Exports -----
    @GetMapping("/salary/excel")
    public void exportSalaryExcel(HttpServletResponse response) throws Exception {
        ExcelExportUtil.exportSalaries(salaryService.findAll(), response);
    }

    @GetMapping("/salary/pdf")
    public void exportSalaryPdf(HttpServletResponse response) throws Exception {
        PdfExportUtil.exportSalaries(salaryService.findAll(), response);
    }

    // ----- Leave Exports -----
    @GetMapping("/leave/excel")
    public void exportLeaveExcel(HttpServletResponse response) throws Exception {
        ExcelExportUtil.exportLeaves(
                leaveService.findAll(org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                        .getContent(),
                response);
    }

    @GetMapping("/leave/pdf")
    public void exportLeavePdf(HttpServletResponse response) throws Exception {
        PdfExportUtil.exportLeaves(
                leaveService.findAll(org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                        .getContent(),
                response);
    }
}
