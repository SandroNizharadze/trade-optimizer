package com.arcticblu.tradeoptimizer.controller;

import com.arcticblu.tradeoptimizer.dto.request.OptimizeTradesRequest;
import com.arcticblu.tradeoptimizer.dto.response.OptimizationResponse;
import com.arcticblu.tradeoptimizer.service.TradeOptimizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeOptimizationController {

    private final TradeOptimizationService tradeOptimizationService;

    @PostMapping("/optimize")
    public ResponseEntity<OptimizationResponse> optimize(
            @Valid @RequestBody OptimizeTradesRequest request
    ) {
        OptimizationResponse response =
                tradeOptimizationService.optimize(request);

        if (response.selectedTrades().isEmpty()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<OptimizationResponse> getByRequestId(
            @PathVariable UUID requestId
    ) {
        return ResponseEntity.ok(
                tradeOptimizationService.getByRequestId(requestId)
        );
    }

    @GetMapping
    public ResponseEntity<Page<OptimizationResponse>> getAll(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                tradeOptimizationService.getAll(pageable)
        );
    }
}