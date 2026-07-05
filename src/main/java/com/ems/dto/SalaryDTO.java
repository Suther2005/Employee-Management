package com.ems.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDTO {

    private Long id;

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.0", message = "Basic salary must be non-negative")
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.0", message = "Allowance must be non-negative")
    private BigDecimal allowance;

    @DecimalMin(value = "0.0", message = "Bonus must be non-negative")
    private BigDecimal bonus;

    @DecimalMin(value = "0.0", message = "Deductions must be non-negative")
    private BigDecimal deductions;

    private BigDecimal netSalary;

    private Integer month;
    private Integer year;

    private String employeeName;
    private String employeeEmpId;
    private String departmentName;
}
