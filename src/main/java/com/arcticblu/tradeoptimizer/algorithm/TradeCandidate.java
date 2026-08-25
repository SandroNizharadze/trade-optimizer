package com.arcticblu.tradeoptimizer.algorithm;

import java.math.BigDecimal;

public record TradeCandidate(
        String tradeName,
        BigDecimal marginRequired,
        BigDecimal expectedPnl
) {
}
