package com.ems.repository;

import com.ems.entity.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    Optional<Salary> findByEmployeeId(Long employeeId);

    boolean existsByEmployeeId(Long employeeId);

    @Query("""
            SELECT s FROM Salary s JOIN s.employee e
            WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.lastName)  LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.empId)     LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Salary> searchSalaries(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT SUM(s.netSalary) FROM Salary s")
    Double getTotalMonthlySalaryExpense();

    List<Salary> findAllByOrderByEmployee_FirstNameAsc();
}
