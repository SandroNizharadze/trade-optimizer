package com.arcticblu.tradeoptimizer.algorithm;

import java.util.List;

public interface TradeOptimizer {
    OptimizationResult optimize(
            List<TradeCandidate> candidates,
            long maxMargin);
}
