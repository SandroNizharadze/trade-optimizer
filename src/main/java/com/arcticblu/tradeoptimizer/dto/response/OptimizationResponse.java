package com.arcticblu.tradeoptimizer.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OptimizationResponse(
        UUID requestId,
        List<TradeResponse> selectedTrades,
        BigDecimal totalExpectedPnl,
        BigDecimal totalMarginRequired,
        Instant createdAt
) {
}
