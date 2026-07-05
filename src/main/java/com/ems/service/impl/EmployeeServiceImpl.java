package com.ems.service.impl;

import com.ems.dto.EmployeeDTO;
import com.ems.entity.*;
import com.ems.exception.*;
import com.ems.repository.*;
import com.ems.service.EmployeeService;
import com.ems.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    @Override
    public Employee save(EmployeeDTO dto, MultipartFile photo) {
        // Validate uniqueness
        if (employeeRepository.existsByEmpId(dto.getEmpId())) {
            throw new IllegalArgumentException("Employee ID '" + dto.getEmpId() + "' already exists.");
        }
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException(dto.getEmail());
        }

        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(dto.getDepartmentId()));

        // Create login account for the employee
        Role employeeRole = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("EMPLOYEE role not configured"));

        String rawPassword = StringUtils.hasText(dto.getPassword()) ? dto.getPassword() : "Employee@123";
        User user = User.builder()
                .username(dto.getEmail())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .role(employeeRole)
                .enabled(true)
                .build();
        userRepository.save(user);

        // Handle photo upload
        String photoFilename = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                photoFilename = FileUploadUtil.saveFile(uploadDir, photo);
            } catch (IOException e) {
                log.warn("Profile photo upload failed: {}", e.getMessage());
            }
        }

        Employee employee = Employee.builder()
                .empId(dto.getEmpId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .designation(dto.getDesignation())
                .joiningDate(dto.getJoiningDate() != null ? dto.getJoiningDate() : LocalDate.now())
                .status(dto.getStatus() != null ? dto.getStatus() : Employee.EmployeeStatus.ACTIVE)
                .profileImage(photoFilename)
                .department(dept)
                .user(user)
                .build();

        return employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long id, EmployeeDTO dto, MultipartFile photo) {
        Employee employee = findById(id);

        // Check email uniqueness (excluding current employee)
        if (employeeRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateEmailException(dto.getEmail());
        }
        if (employeeRepository.existsByEmpIdAndIdNot(dto.getEmpId(), id)) {
            throw new IllegalArgumentException("Employee ID '" + dto.getEmpId() + "' already exists.");
        }

        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(dto.getDepartmentId()));

        // Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            try {
                // Delete old photo
                FileUploadUtil.deleteFile(uploadDir, employee.getProfileImage());
                String photoFilename = FileUploadUtil.saveFile(uploadDir, photo);
                employee.setProfileImage(photoFilename);
            } catch (IOException e) {
                log.warn("Profile photo update failed: {}", e.getMessage());
            }
        }

        employee.setEmpId(dto.getEmpId());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setGender(dto.getGender());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setAddress(dto.getAddress());
        employee.setDesignation(dto.getDesignation());
        employee.setJoiningDate(dto.getJoiningDate());
        employee.setStatus(dto.getStatus());
        employee.setDepartment(dept);

        // Update linked user email/username
        if (employee.getUser() != null) {
            employee.getUser().setEmail(dto.getEmail());
            employee.getUser().setUsername(dto.getEmail());
        }

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteById(Long id) {
        Employee employee = findById(id);
        // Delete profile photo
        FileUploadUtil.deleteFile(uploadDir, employee.getProfileImage());
        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findByEmpId(String empId) {
        return employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + empId));
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findByUserId(Long userId) {
        return employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new EmployeeNotFoundException("No employee linked to user ID: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employee> findAll(String keyword, Pageable pageable) {
        if (StringUtils.hasText(keyword)) {
            return employeeRepository.searchEmployees(keyword.trim(), pageable);
        }
        return employeeRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }

    @Override
    public EmployeeDTO toDTO(Employee emp) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(emp.getId());
        dto.setEmpId(emp.getEmpId());
        dto.setFirstName(emp.getFirstName());
        dto.setLastName(emp.getLastName());
        dto.setEmail(emp.getEmail());
        dto.setPhone(emp.getPhone());
        dto.setGender(emp.getGender());
        dto.setDateOfBirth(emp.getDateOfBirth());
        dto.setAddress(emp.getAddress());
        dto.setDesignation(emp.getDesignation());
        dto.setJoiningDate(emp.getJoiningDate());
        dto.setStatus(emp.getStatus());
        dto.setProfileImage(emp.getProfileImage());
        if (emp.getDepartment() != null) {
            dto.setDepartmentId(emp.getDepartment().getId());
        }
        return dto;
    }
}
