package com.ems.service;

import com.ems.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    Attendance checkIn(Long employeeId);
    Attendance checkOut(Long employeeId);
    Optional<Attendance> findTodayAttendance(Long employeeId);
    Page<Attendance> searchAttendance(Long employeeId, LocalDate fromDate, LocalDate toDate, Pageable pageable);
    List<Attendance> getMonthlyAttendance(Long employeeId, int month, int year);
    long countPresentToday();
}
