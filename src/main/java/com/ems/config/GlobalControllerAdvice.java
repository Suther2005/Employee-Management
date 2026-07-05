package com.ems.config;

import com.ems.repository.LeaveRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds common model attributes to every controller response.
 * Required for Thymeleaf 3.1+ which removed #request, #session, etc.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final LeaveRequestRepository leaveRequestRepository;

    /**
     * Exposes the current request URI as ${currentUri} in all Thymeleaf templates.
     * Used by the sidebar to highlight the active navigation link.
     */
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * Exposes pending leave count for the admin notification bell in the navbar.
     * Only queries DB when user is authenticated.
     */
    @ModelAttribute("pendingLeaveCount")
    public long pendingLeaveCount() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                return leaveRequestRepository.countByStatus(
                        com.ems.entity.LeaveRequest.LeaveStatus.PENDING);
            }
        } catch (Exception ignored) {
            // Silently fail — not critical for page rendering
        }
        return 0;
    }
}
