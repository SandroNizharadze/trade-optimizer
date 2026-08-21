package com.arcticblu.tradeoptimizer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor
public class TradeEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "optimization_run_id", nullable = false)
    private OptimizationRunEntity optimizationRun;

    @Column(name = "trade_name", nullable = false)
    private String tradeName;

    @Column(name = "margin_required", nullable = false, precision = 19, scale = 4)
    private BigDecimal marginRequired;

    @Column(name = "expected_pnl", nullable = false, precision = 19, scale = 4)
    private BigDecimal expectedPnl;

    @Column(name = "selected", nullable = false)
    private boolean selected;

}
