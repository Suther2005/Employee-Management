package com.ems.service;

import com.ems.dto.LeaveRequestDTO;
import com.ems.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaveService {
    LeaveRequest applyLeave(Long employeeId, LeaveRequestDTO dto);
    LeaveRequest approveLeave(Long leaveId, String reviewedBy);
    LeaveRequest rejectLeave(Long leaveId, String reviewedBy, String reason);
    LeaveRequest cancelLeave(Long leaveId, Long employeeId);
    LeaveRequest findById(Long id);
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    Page<LeaveRequest> findAll(Pageable pageable);
    long countPending();
}
