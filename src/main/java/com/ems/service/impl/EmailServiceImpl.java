package com.ems.service.impl;

import com.ems.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Email service implementation using JavaMailSender.
 * All methods are @Async — they do not block the request thread.
 *
 * NOTE: Configure spring.mail.* in application.properties to enable actual sending.
 * If not configured, emails will be logged to console only.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendLeaveApprovalEmail(String toEmail, String employeeName,
                                       String leaveType, String startDate, String endDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("✅ Leave Request Approved — Smart EMS");
            message.setText("""
                    Dear %s,
                    
                    Your %s leave request has been APPROVED.
                    
                    📅 Leave Period: %s to %s
                    
                    Please ensure your work is handed over before your leave begins.
                    
                    Best regards,
                    HR Team | Smart EMS
                    """.formatted(employeeName, leaveType, startDate, endDate));
            mailSender.send(message);
            log.info("Leave approval email sent to: {}", toEmail);
        } catch (Exception e) {
            log.warn("Failed to send leave approval email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendLeaveRejectionEmail(String toEmail, String employeeName,
                                        String leaveType, String reason) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("❌ Leave Request Rejected — Smart EMS");
            message.setText("""
                    Dear %s,
                    
                    Your %s leave request has been REJECTED.
                    
                    📝 Reason: %s
                    
                    Please contact your HR manager for further clarification.
                    
                    Best regards,
                    HR Team | Smart EMS
                    """.formatted(employeeName, leaveType, reason));
            mailSender.send(message);
            log.info("Leave rejection email sent to: {}", toEmail);
        } catch (Exception e) {
            log.warn("Failed to send leave rejection email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendWelcomeEmail(String toEmail, String employeeName, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("🎉 Welcome to Smart EMS — Your Account is Ready");
            message.setText("""
                    Dear %s,
                    
                    Welcome to Smart Employee Management System!
                    
                    Your login credentials:
                    📧 Username: %s
                    🔑 Password: %s
                    
                    Please log in at http://localhost:8080/login and change your password immediately.
                    
                    Best regards,
                    HR Team | Smart EMS
                    """.formatted(employeeName, toEmail, tempPassword));
            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.warn("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }
}
