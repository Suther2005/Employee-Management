package com.ems.service;

/**
 * Email notification service for leave management events.
 */
public interface EmailService {
    void sendLeaveApprovalEmail(String toEmail, String employeeName,
                                String leaveType, String startDate, String endDate);

    void sendLeaveRejectionEmail(String toEmail, String employeeName,
                                 String leaveType, String reason);

    void sendWelcomeEmail(String toEmail, String employeeName, String tempPassword);
}
