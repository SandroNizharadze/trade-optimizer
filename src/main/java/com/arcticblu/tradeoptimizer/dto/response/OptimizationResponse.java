package com.arcticblu.tradeoptimizer.dto.response;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OptimizationResponse(
        UUID requestId,
        List<TradeResponse> selectedTrades,
        BigDecimal totalMarginRequired,
        Instant createdAt
) {
}
