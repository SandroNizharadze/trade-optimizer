package com.arcticblu.tradeoptimizer.dto.response;

import java.math.BigDecimal;

public record TradeResponse(
        String tradeName,
        BigDecimal marginRequired,
        BigDecimal expectedPnl
) {
}
