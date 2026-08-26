package com.arcticblu.tradeoptimizer.algorithm;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class KnapsackTradeOptimizer implements TradeOptimizer {

    @Override
    public OptimizationResult optimize(
            List<TradeCandidate> candidates,
            BigDecimal maxMargin
    ) {
        return new OptimizationResult(
                List.of(),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }
}