package com.laundrypos.modules.cash_closure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_closures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at", nullable = false)
    private LocalDateTime closedAt;

    @Column(name = "opened_by", nullable = false)
    private String openedBy;

    @Column(name = "closed_by")
    private String closedBy;

    @Column(name = "initial_cash", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal initialCash = BigDecimal.ZERO;

    @Column(name = "total_sales", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalSales = BigDecimal.ZERO;

    @Column(name = "total_expenses", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalExpenses = BigDecimal.ZERO;

    @Column(name = "expected_cash", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal expectedCash = BigDecimal.ZERO;

    @Column(name = "declared_cash", precision = 10, scale = 2)
    private BigDecimal declaredCash;

    @Column(name = "difference", precision = 10, scale = 2)
    private BigDecimal difference;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CashClosureStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum CashClosureStatus {
        ABIERTO, CERRADO
    }
}
