package com.arcticblu.tradeoptimizer.algorithm;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class KnapsackTradeOptimizer implements TradeOptimizer {

    @Override
    public OptimizationResult optimize(
            List<TradeCandidate> candidates,
            BigDecimal maxMargin
    ) {
        return optimizeFrom(
                candidates,
                0,
                maxMargin
        );
    }

    private OptimizationResult optimizeFrom(
            List<TradeCandidate> candidates,
            int index,
            BigDecimal remainingMargin
    ) {
        if (index >= candidates.size()) {
            return emptyResult();
        }

        TradeCandidate current = candidates.get(index);

        OptimizationResult withoutCurrent =
                optimizeFrom(
                        candidates,
                        index + 1,
                        remainingMargin
                );

        if (current.marginRequired()
                .compareTo(remainingMargin) > 0) {
            return withoutCurrent;
        }

        OptimizationResult remainder =
                optimizeFrom(
                        candidates,
                        index + 1,
                        remainingMargin.subtract(
                                current.marginRequired()
                        )
                );

        OptimizationResult withCurrent =
                addTrade(current, remainder);

        return betterOf(
                withCurrent,
                withoutCurrent
        );
    }

    private OptimizationResult addTrade(
            TradeCandidate trade,
            OptimizationResult result
    ) {
        List<TradeCandidate> selected =
                new ArrayList<>(result.selectedTrades());

        selected.add(trade);

        return new OptimizationResult(
                selected,
                result.totalMarginRequired()
                        .add(trade.marginRequired()),
                result.totalExpectedPnl()
                        .add(trade.expectedPnl())
        );
    }

    private OptimizationResult betterOf(
            OptimizationResult first,
            OptimizationResult second
    ) {
        int pnlComparison = first.totalExpectedPnl()
                .compareTo(second.totalExpectedPnl());

        if (pnlComparison > 0) {
            return first;
        }

        if (pnlComparison < 0) {
            return second;
        }

        if (first.totalMarginRequired()
                .compareTo(second.totalMarginRequired()) < 0) {
            return first;
        }

        return second;
    }

    private OptimizationResult emptyResult() {
        return new OptimizationResult(
                List.of(),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }
}