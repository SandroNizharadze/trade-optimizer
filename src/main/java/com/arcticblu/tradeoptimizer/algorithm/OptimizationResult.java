package com.arcticblu.tradeoptimizer.algorithm;

import java.math.BigDecimal;
import java.util.List;

public record OptimizationResult(
        List<TradeCandidate> selectedTrades,
        BigDecimal totalMarginRequired,
        BigDecimal totalExpectedPnl
) {
}
