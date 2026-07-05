package com.ems.service;

import com.ems.dto.DepartmentDTO;
import com.ems.entity.Department;

import java.util.List;

public interface DepartmentService {
    Department save(DepartmentDTO dto);
    Department update(Long id, DepartmentDTO dto);
    void deleteById(Long id);
    Department findById(Long id);
    List<Department> findAll();
    DepartmentDTO toDTO(Department department);
    boolean existsByName(String name);
}
