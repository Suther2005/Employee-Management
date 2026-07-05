package com.ems.service.impl;

import com.ems.entity.Attendance;
import com.ems.entity.Employee;
import com.ems.exception.EmployeeNotFoundException;
import com.ems.repository.AttendanceRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Attendance checkIn(Long employeeId) {
        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByEmployeeIdAndDate(employeeId, today)) {
            throw new IllegalStateException("You have already checked in today.");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .date(today)
                .checkIn(LocalTime.now())
                .status(Attendance.AttendanceStatus.PRESENT)
                .build();

        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance checkOut(Long employeeId) {
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new IllegalStateException("No check-in found for today. Please check in first."));

        if (attendance.getCheckOut() != null) {
            throw new IllegalStateException("You have already checked out today.");
        }

        attendance.setCheckOut(LocalTime.now());
        // calculateWorkingHours is called by @PreUpdate
        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findTodayAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndDate(employeeId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Attendance> searchAttendance(Long employeeId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        return attendanceRepository.searchAttendance(employeeId, fromDate, toDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getMonthlyAttendance(Long employeeId, int month, int year) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
        return attendanceRepository.findByEmployeeIdAndDateBetweenOrderByDate(employeeId, from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPresentToday() {
        return attendanceRepository.countPresentToday(LocalDate.now());
    }
}
