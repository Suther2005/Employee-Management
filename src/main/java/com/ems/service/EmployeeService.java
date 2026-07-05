package com.ems.service;

import com.ems.dto.EmployeeDTO;
import com.ems.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    Employee save(EmployeeDTO dto, MultipartFile photo);
    Employee update(Long id, EmployeeDTO dto, MultipartFile photo);
    void deleteById(Long id);
    Employee findById(Long id);
    Employee findByEmpId(String empId);
    Employee findByUserId(Long userId);
    Page<Employee> findAll(String keyword, Pageable pageable);
    List<Employee> findAll();
    boolean existsByEmail(String email);
    EmployeeDTO toDTO(Employee employee);
}
