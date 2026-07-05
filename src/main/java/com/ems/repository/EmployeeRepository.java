package com.ems.repository;

import com.ems.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmpId(String empId);

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByUserId(Long userId);

    boolean existsByEmpId(String empId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEmpIdAndIdNot(String empId, Long id);

    // Global search across name, email, empId, designation, department
    @Query("""
            SELECT e FROM Employee e
            LEFT JOIN e.department d
            WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.lastName)  LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.email)     LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.empId)     LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.designation) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(d.name)      LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Employee> searchEmployees(@Param("keyword") String keyword, Pageable pageable);

    // Count by department for dashboard charts
    @Query("SELECT d.name, COUNT(e) FROM Employee e JOIN e.department d GROUP BY d.name")
    List<Object[]> countByDepartment();

    // Monthly join trend for charts (last 12 months)
    @Query("""
            SELECT MONTH(e.joiningDate), YEAR(e.joiningDate), COUNT(e)
            FROM Employee e
            WHERE e.joiningDate >= :fromDate
            GROUP BY YEAR(e.joiningDate), MONTH(e.joiningDate)
            ORDER BY YEAR(e.joiningDate), MONTH(e.joiningDate)
            """)
    List<Object[]> countMonthlyJoining(@Param("fromDate") LocalDate fromDate);

    long countByStatus(Employee.EmployeeStatus status);

    long countByDepartmentId(Long departmentId);

    @Query("SELECT e FROM Employee e WHERE e.department.id = :deptId ORDER BY e.firstName")
    List<Employee> findByDepartmentId(@Param("deptId") Long departmentId);

    // Count new employees this month
    @Query("SELECT COUNT(e) FROM Employee e WHERE MONTH(e.joiningDate) = :month AND YEAR(e.joiningDate) = :year")
    long countNewEmployeesThisMonth(@Param("month") int month, @Param("year") int year);
}
