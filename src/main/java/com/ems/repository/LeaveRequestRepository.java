package com.ems.repository;

import com.ems.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeIdOrderByAppliedDateDesc(Long employeeId);

    Page<LeaveRequest> findAllByOrderByAppliedDateDesc(Pageable pageable);

    long countByStatus(LeaveRequest.LeaveStatus status);

    @Query("SELECT l FROM LeaveRequest l WHERE l.employee.id = :empId ORDER BY l.appliedDate DESC")
    Page<LeaveRequest> findByEmployeeId(@Param("empId") Long employeeId, Pageable pageable);

    @Query("""
            SELECT l.leaveType, COUNT(l) FROM LeaveRequest l
            WHERE l.status = 'APPROVED'
            GROUP BY l.leaveType
            """)
    List<Object[]> countApprovedByType();

    boolean existsByEmployeeIdAndStatus(Long employeeId, LeaveRequest.LeaveStatus status);
}
