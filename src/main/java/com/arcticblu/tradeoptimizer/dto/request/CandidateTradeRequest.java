package com.arcticblu.tradeoptimizer.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CandidateTradeRequest (
    @NotBlank
    String tradeName,

    @Positive
    BigDecimal marginRequired,

    @NotNull
    BigDecimal expectedPnl
) {
}

