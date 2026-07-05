package com.ems.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.ems.entity.Employee;
import com.ems.entity.Salary;
import com.ems.entity.LeaveRequest;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for generating PDF reports using OpenPDF (iText fork).
 */
public class PdfExportUtil {

    private static final Color HEADER_COLOR = new Color(25, 42, 86);
    private static final Color ROW_ALT_COLOR = new Color(240, 245, 255);

    // --------------------------------------------------------
    // Employee PDF Export
    // --------------------------------------------------------
    public static void exportEmployees(List<Employee> employees, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=employees.pdf");

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Title
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.WHITE);
        Paragraph title = new Paragraph("Employee Report — Smart EMS\n\n", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Table
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.5f, 1f, 2f, 2.5f, 1.5f, 2f, 2f, 1f});

        String[] headers = {"#", "Emp ID", "Name", "Email", "Phone", "Department", "Designation", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        boolean alternate = false;
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            Color rowColor = alternate ? ROW_ALT_COLOR : Color.WHITE;
            addCell(table, String.valueOf(i + 1), rowColor, 9);
            addCell(table, emp.getEmpId(), rowColor, 9);
            addCell(table, emp.getFullName(), rowColor, 9);
            addCell(table, emp.getEmail(), rowColor, 9);
            addCell(table, emp.getPhone() != null ? emp.getPhone() : "", rowColor, 9);
            addCell(table, emp.getDepartment() != null ? emp.getDepartment().getName() : "", rowColor, 9);
            addCell(table, emp.getDesignation(), rowColor, 9);
            addCell(table, emp.getStatus() != null ? emp.getStatus().name() : "", rowColor, 9);
            alternate = !alternate;
        }

        document.add(table);
        document.close();
    }

    // --------------------------------------------------------
    // Salary PDF Export
    // --------------------------------------------------------
    public static void exportSalaries(List<Salary> salaries, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=salary_report.pdf");

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Paragraph title = new Paragraph("Salary Report — Smart EMS\n\n",
                new Font(Font.HELVETICA, 16, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        String[] headers = {"#", "Emp ID", "Name", "Basic", "Allowance", "Deductions", "Net Salary"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        boolean alt = false;
        for (int i = 0; i < salaries.size(); i++) {
            Salary s = salaries.get(i);
            Color rowColor = alt ? ROW_ALT_COLOR : Color.WHITE;
            addCell(table, String.valueOf(i + 1), rowColor, 9);
            addCell(table, s.getEmployee() != null ? s.getEmployee().getEmpId() : "", rowColor, 9);
            addCell(table, s.getEmployee() != null ? s.getEmployee().getFullName() : "", rowColor, 9);
            addCell(table, "₹" + s.getBasicSalary(), rowColor, 9);
            addCell(table, "₹" + (s.getAllowance() != null ? s.getAllowance() : 0), rowColor, 9);
            addCell(table, "₹" + (s.getDeductions() != null ? s.getDeductions() : 0), rowColor, 9);
            addCell(table, "₹" + (s.getNetSalary() != null ? s.getNetSalary() : 0), rowColor, 9);
            alt = !alt;
        }

        document.add(table);
        document.close();
    }

    // --------------------------------------------------------
    // Leave PDF Export
    // --------------------------------------------------------
    public static void exportLeaves(List<LeaveRequest> leaves, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=leave_report.pdf");

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Paragraph title = new Paragraph("Leave Report — Smart EMS\n\n",
                new Font(Font.HELVETICA, 16, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        String[] headers = {"#", "Employee", "Leave Type", "Start", "End", "Days", "Status"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        boolean alt = false;
        for (int i = 0; i < leaves.size(); i++) {
            LeaveRequest l = leaves.get(i);
            Color rowColor = alt ? ROW_ALT_COLOR : Color.WHITE;
            addCell(table, String.valueOf(i + 1), rowColor, 9);
            addCell(table, l.getEmployee() != null ? l.getEmployee().getFullName() : "", rowColor, 9);
            addCell(table, l.getLeaveType().name(), rowColor, 9);
            addCell(table, l.getStartDate().toString(), rowColor, 9);
            addCell(table, l.getEndDate().toString(), rowColor, 9);
            addCell(table, String.valueOf(l.getTotalDays()), rowColor, 9);
            addCell(table, l.getStatus().name(), rowColor, 9);
            alt = !alt;
        }

        document.add(table);
        document.close();
    }

    private static void addCell(PdfPTable table, String text, Color bgColor, int fontSize) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.HELVETICA, fontSize)));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
