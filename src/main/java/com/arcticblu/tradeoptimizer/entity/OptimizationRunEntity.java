package com.arcticblu.tradeoptimizer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "optimization_runs")
@Getter
@Setter
@NoArgsConstructor
public class OptimizationRunEntity {
    @Id
    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "max_margin", nullable = false, precision = 19, scale = 4)
    private BigDecimal maxMargin;

    @Column(name = "total_margin_required", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalMarginRequired;

    @Column(name = "total_expected_pnl", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalExpectedPnl;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(
            mappedBy = "optimizationRun",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TradeEntity> trades = new ArrayList<>();

    public void addTrade(TradeEntity trade) {
        trades.add(trade);
        trade.setOptimizationRun(this);
    }

}
