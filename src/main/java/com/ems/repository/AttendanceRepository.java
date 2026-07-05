package com.ems.repository;

import com.ems.entity.Attendance;
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
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<Attendance> findByEmployeeIdOrderByDateDesc(Long employeeId);

    @Query("""
            SELECT a FROM Attendance a JOIN a.employee e
            WHERE (:employeeId IS NULL OR e.id = :employeeId)
              AND (:fromDate IS NULL  OR a.date >= :fromDate)
              AND (:toDate IS NULL    OR a.date <= :toDate)
            ORDER BY a.date DESC
            """)
    Page<Attendance> searchAttendance(
            @Param("employeeId") Long employeeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.date = :date AND a.status = 'PRESENT'")
    long countPresentToday(@Param("date") LocalDate date);

    @Query("""
            SELECT COUNT(a) FROM Attendance a
            WHERE a.employee.id = :employeeId
              AND MONTH(a.date) = :month AND YEAR(a.date) = :year
              AND a.status = 'PRESENT'
            """)
    long countPresentDaysInMonth(
            @Param("employeeId") Long employeeId,
            @Param("month") int month,
            @Param("year") int year);

    List<Attendance> findByEmployeeIdAndDateBetweenOrderByDate(Long employeeId, LocalDate from, LocalDate to);
}
