package com.ems.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

/**
 * Attendance entity — tracks daily employee check-in and check-out times.
 * Working hours are automatically calculated on persist/update.
 */
@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;

    @Column(name = "working_hours")
    private Double workingHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    /**
     * Automatically calculates working hours from check-in and check-out times.
     */
    @PreUpdate
    @PrePersist
    public void calculateWorkingHours() {
        if (checkIn != null && checkOut != null) {
            long minutes = Duration.between(checkIn, checkOut).toMinutes();
            if (minutes > 0) {
                workingHours = Math.round(minutes / 60.0 * 100.0) / 100.0;
            }
        }
    }

    public enum AttendanceStatus {
        PRESENT, ABSENT, HALF_DAY, ON_LEAVE
    }
}
