package com.ems.service.impl;

import com.ems.dto.LeaveRequestDTO;
import com.ems.entity.Employee;
import com.ems.entity.LeaveRequest;
import com.ems.exception.EmployeeNotFoundException;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.LeaveRequestRepository;
import com.ems.service.EmailService;
import com.ems.service.LeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    @Override
    public LeaveRequest applyLeave(Long employeeId, LeaveRequestDTO dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        LeaveRequest leave = LeaveRequest.builder()
                .employee(employee)
                .leaveType(dto.getLeaveType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .status(LeaveRequest.LeaveStatus.PENDING)
                .build();

        return leaveRequestRepository.save(leave);
    }

    @Override
    public LeaveRequest approveLeave(Long leaveId, String reviewedBy) {
        LeaveRequest leave = findById(leaveId);
        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new IllegalStateException("Only pending leave requests can be approved.");
        }
        leave.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        leave.setReviewedAt(LocalDateTime.now());
        leave.setReviewedBy(reviewedBy);

        LeaveRequest saved = leaveRequestRepository.save(leave);

        // Send email notification (async, non-blocking)
        emailService.sendLeaveApprovalEmail(
                saved.getEmployee().getEmail(),
                saved.getEmployee().getFullName(),
                saved.getLeaveType().name(),
                saved.getStartDate().toString(),
                saved.getEndDate().toString()
        );

        return saved;
    }

    @Override
    public LeaveRequest rejectLeave(Long leaveId, String reviewedBy, String reason) {
        LeaveRequest leave = findById(leaveId);
        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new IllegalStateException("Only pending leave requests can be rejected.");
        }
        leave.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        leave.setReviewedAt(LocalDateTime.now());
        leave.setReviewedBy(reviewedBy);
        leave.setRejectionReason(reason);

        LeaveRequest saved = leaveRequestRepository.save(leave);

        emailService.sendLeaveRejectionEmail(
                saved.getEmployee().getEmail(),
                saved.getEmployee().getFullName(),
                saved.getLeaveType().name(),
                reason
        );

        return saved;
    }

    @Override
    public LeaveRequest cancelLeave(Long leaveId, Long employeeId) {
        LeaveRequest leave = findById(leaveId);
        if (!leave.getEmployee().getId().equals(employeeId)) {
            throw new IllegalStateException("You can only cancel your own leave requests.");
        }
        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new IllegalStateException("Only pending leave requests can be cancelled.");
        }
        leave.setStatus(LeaveRequest.LeaveStatus.CANCELLED);
        return leaveRequestRepository.save(leave);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequest findById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> findByEmployeeId(Long employeeId) {
        return leaveRequestRepository.findByEmployeeIdOrderByAppliedDateDesc(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveRequest> findAll(Pageable pageable) {
        return leaveRequestRepository.findAllByOrderByAppliedDateDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPending() {
        return leaveRequestRepository.countByStatus(LeaveRequest.LeaveStatus.PENDING);
    }
}
