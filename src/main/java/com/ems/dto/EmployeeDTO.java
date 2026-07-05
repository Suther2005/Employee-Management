package com.ems.dto;

import com.ems.entity.Employee;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO for Employee form submissions and data transfer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private Long id;

    @NotBlank(message = "Employee ID is required")
    @Pattern(regexp = "^EMP[0-9]{4,}$", message = "Employee ID must follow format: EMP0001")
    private String empId;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be 2–50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be 2–50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be 10–15 digits")
    private String phone;

    private Employee.Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String address;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Department is required")
    private Long departmentId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;

    private Employee.EmployeeStatus status;

    private String profileImage;

    // Password (only for new employee creation)
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
