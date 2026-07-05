package com.ems.service.impl;

import com.ems.dto.DepartmentDTO;
import com.ems.entity.Department;
import com.ems.exception.DepartmentNotFoundException;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Department save(DepartmentDTO dto) {
        if (departmentRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Department '" + dto.getName() + "' already exists.");
        }
        Department dept = Department.builder()
                .name(dto.getName())
                .head(dto.getHead())
                .description(dto.getDescription())
                .build();
        return departmentRepository.save(dept);
    }

    @Override
    public Department update(Long id, DepartmentDTO dto) {
        if (departmentRepository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new IllegalArgumentException("Department '" + dto.getName() + "' already exists.");
        }
        Department dept = findById(id);
        dept.setName(dto.getName());
        dept.setHead(dto.getHead());
        dept.setDescription(dto.getDescription());
        return departmentRepository.save(dept);
    }

    @Override
    public void deleteById(Long id) {
        long empCount = employeeRepository.countByDepartmentId(id);
        if (empCount > 0) {
            throw new IllegalStateException("Cannot delete department with " + empCount + " employee(s). Reassign them first.");
        }
        Department dept = findById(id);
        departmentRepository.delete(dept);
    }

    @Override
    @Transactional(readOnly = true)
    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public DepartmentDTO toDTO(Department dept) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(dept.getId());
        dto.setName(dept.getName());
        dto.setHead(dept.getHead());
        dto.setDescription(dept.getDescription());
        dto.setEmployeeCount((int) employeeRepository.countByDepartmentId(dept.getId()));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }
}
