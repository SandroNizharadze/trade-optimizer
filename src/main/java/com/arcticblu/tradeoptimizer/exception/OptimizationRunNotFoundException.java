package com.arcticblu.tradeoptimizer.exception;

import java.util.UUID;

public class OptimizationRunNotFoundException extends RuntimeException {

    public OptimizationRunNotFoundException(UUID requestId) {
        super("Optimization run not found: " + requestId);
    }
}