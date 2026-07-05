package com.ems.service.impl;

import com.ems.dto.SalaryDTO;
import com.ems.entity.Employee;
import com.ems.entity.Salary;
import com.ems.exception.EmployeeNotFoundException;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.SalaryRepository;
import com.ems.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Salary save(SalaryDTO dto) {
        if (salaryRepository.existsByEmployeeId(dto.getEmployeeId())) {
            throw new IllegalArgumentException("Salary record already exists for this employee. Use update instead.");
        }

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(dto.getEmployeeId()));

        LocalDate now = LocalDate.now();
        Salary salary = Salary.builder()
                .employee(employee)
                .basicSalary(dto.getBasicSalary())
                .allowance(dto.getAllowance())
                .bonus(dto.getBonus())
                .deductions(dto.getDeductions())
                .month(dto.getMonth() != null ? dto.getMonth() : now.getMonthValue())
                .year(dto.getYear() != null ? dto.getYear() : now.getYear())
                .build();
        // net salary is auto-calculated in @PrePersist
        return salaryRepository.save(salary);
    }

    @Override
    public Salary update(Long id, SalaryDTO dto) {
        Salary salary = findById(id);
        salary.setBasicSalary(dto.getBasicSalary());
        salary.setAllowance(dto.getAllowance());
        salary.setBonus(dto.getBonus());
        salary.setDeductions(dto.getDeductions());
        if (dto.getMonth() != null) salary.setMonth(dto.getMonth());
        if (dto.getYear() != null) salary.setYear(dto.getYear());
        return salaryRepository.save(salary);
    }

    @Override
    public void deleteById(Long id) {
        Salary salary = findById(id);
        salaryRepository.delete(salary);
    }

    @Override
    @Transactional(readOnly = true)
    public Salary findById(Long id) {
        return salaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary record not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Salary> findByEmployeeId(Long employeeId) {
        return salaryRepository.findByEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Salary> findAll(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return salaryRepository.searchSalaries(keyword.trim(), pageable);
        }
        return salaryRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Salary> findAll() {
        return salaryRepository.findAllByOrderByEmployee_FirstNameAsc();
    }

    @Override
    public SalaryDTO toDTO(Salary s) {
        SalaryDTO dto = new SalaryDTO();
        dto.setId(s.getId());
        dto.setBasicSalary(s.getBasicSalary());
        dto.setAllowance(s.getAllowance());
        dto.setBonus(s.getBonus());
        dto.setDeductions(s.getDeductions());
        dto.setNetSalary(s.getNetSalary());
        dto.setMonth(s.getMonth());
        dto.setYear(s.getYear());
        if (s.getEmployee() != null) {
            dto.setEmployeeId(s.getEmployee().getId());
            dto.setEmployeeName(s.getEmployee().getFullName());
            dto.setEmployeeEmpId(s.getEmployee().getEmpId());
            if (s.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(s.getEmployee().getDepartment().getName());
            }
        }
        return dto;
    }
}
