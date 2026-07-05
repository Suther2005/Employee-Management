package com.ems.util;

import com.ems.entity.Employee;
import com.ems.entity.LeaveRequest;
import com.ems.entity.Salary;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for generating Excel (.xlsx) reports using Apache POI.
 */
public class ExcelExportUtil {

    // --------------------------------------------------------
    // Employee Excel Export
    // --------------------------------------------------------
    public static void exportEmployees(List<Employee> employees, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=employees.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");

            // Header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle bodyStyle = createBodyStyle(workbook);

            // Title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Employee Report — Smart EMS");
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            // Column headers
            String[] headers = {"#", "Emp ID", "Full Name", "Email", "Phone",
                                 "Department", "Designation", "Gender", "Joining Date", "Status"};
            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowNum = 3;
            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(emp.getEmpId());
                row.createCell(2).setCellValue(emp.getFullName());
                row.createCell(3).setCellValue(emp.getEmail());
                row.createCell(4).setCellValue(emp.getPhone() != null ? emp.getPhone() : "");
                row.createCell(5).setCellValue(emp.getDepartment() != null ? emp.getDepartment().getName() : "");
                row.createCell(6).setCellValue(emp.getDesignation());
                row.createCell(7).setCellValue(emp.getGender() != null ? emp.getGender().name() : "");
                row.createCell(8).setCellValue(emp.getJoiningDate() != null ? emp.getJoiningDate().toString() : "");
                row.createCell(9).setCellValue(emp.getStatus() != null ? emp.getStatus().name() : "");
                // Apply body style
                for (int c = 0; c < 10; c++) {
                    row.getCell(c).setCellStyle(bodyStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    // --------------------------------------------------------
    // Salary Excel Export
    // --------------------------------------------------------
    public static void exportSalaries(List<Salary> salaries, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=salary_report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Salary Report");
            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {"#", "Emp ID", "Employee Name", "Department",
                                 "Basic Salary", "Allowance", "Bonus", "Deductions", "Net Salary"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (int i = 0; i < salaries.size(); i++) {
                Salary s = salaries.get(i);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(s.getEmployee() != null ? s.getEmployee().getEmpId() : "");
                row.createCell(2).setCellValue(s.getEmployee() != null ? s.getEmployee().getFullName() : "");
                row.createCell(3).setCellValue(s.getEmployee() != null && s.getEmployee().getDepartment() != null
                        ? s.getEmployee().getDepartment().getName() : "");
                row.createCell(4).setCellValue(s.getBasicSalary().doubleValue());
                row.createCell(5).setCellValue(s.getAllowance() != null ? s.getAllowance().doubleValue() : 0);
                row.createCell(6).setCellValue(s.getBonus() != null ? s.getBonus().doubleValue() : 0);
                row.createCell(7).setCellValue(s.getDeductions() != null ? s.getDeductions().doubleValue() : 0);
                row.createCell(8).setCellValue(s.getNetSalary() != null ? s.getNetSalary().doubleValue() : 0);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(response.getOutputStream());
        }
    }

    // --------------------------------------------------------
    // Leave Excel Export
    // --------------------------------------------------------
    public static void exportLeaves(List<LeaveRequest> leaves, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=leave_report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Leave Report");
            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {"#", "Employee", "Leave Type", "Start Date", "End Date", "Days", "Reason", "Status", "Applied On"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (int i = 0; i < leaves.size(); i++) {
                LeaveRequest l = leaves.get(i);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(l.getEmployee() != null ? l.getEmployee().getFullName() : "");
                row.createCell(2).setCellValue(l.getLeaveType().name());
                row.createCell(3).setCellValue(l.getStartDate().toString());
                row.createCell(4).setCellValue(l.getEndDate().toString());
                row.createCell(5).setCellValue(l.getTotalDays());
                row.createCell(6).setCellValue(l.getReason());
                row.createCell(7).setCellValue(l.getStatus().name());
                row.createCell(8).setCellValue(l.getAppliedDate() != null ? l.getAppliedDate().toString() : "");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(response.getOutputStream());
        }
    }

    // -------  Styles -------

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createBodyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
