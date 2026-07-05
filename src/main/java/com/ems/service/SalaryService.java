package com.ems.service;

import com.ems.dto.SalaryDTO;
import com.ems.entity.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SalaryService {
    Salary save(SalaryDTO dto);
    Salary update(Long id, SalaryDTO dto);
    void deleteById(Long id);
    Salary findById(Long id);
    Optional<Salary> findByEmployeeId(Long employeeId);
    Page<Salary> findAll(String keyword, Pageable pageable);
    List<Salary> findAll();
    SalaryDTO toDTO(Salary salary);
}
