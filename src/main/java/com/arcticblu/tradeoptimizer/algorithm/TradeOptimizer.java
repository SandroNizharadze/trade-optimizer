package com.arcticblu.tradeoptimizer.algorithm;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public interface TradeOptimizer {
    OptimizationResult optimize(
            List<TradeCandidate> candidates,
            @PositiveOrZero BigDecimal maxMargin);
}
