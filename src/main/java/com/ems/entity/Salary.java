package com.ems.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Salary entity — represents a salary record for an employee for a given month/year.
 * Net salary is auto-calculated via JPA lifecycle hooks.
 */
@Entity
@Table(name = "salary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal basicSalary = BigDecimal.ZERO;

    @Column(name = "allowance", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal allowance = BigDecimal.ZERO;

    @Column(name = "bonus", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "deductions", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "net_salary", precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Auto-calculates net salary = basic + allowance + bonus - deductions
     * before any persist or update operation.
     */
    @PrePersist
    @PreUpdate
    public void calculateNetSalary() {
        BigDecimal earnings = basicSalary
                .add(allowance != null ? allowance : BigDecimal.ZERO)
                .add(bonus != null ? bonus : BigDecimal.ZERO);
        BigDecimal total = earnings.subtract(deductions != null ? deductions : BigDecimal.ZERO);
        this.netSalary = total.compareTo(BigDecimal.ZERO) > 0 ? total : BigDecimal.ZERO;
    }
}
