package com.arcticblu.tradeoptimizer.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public record OptimizeTradesRequest(
        @PositiveOrZero
        BigDecimal maxMargin,

        @NotNull
        List<@Valid CandidateTradeRequest> candidateTrades
) {
}
