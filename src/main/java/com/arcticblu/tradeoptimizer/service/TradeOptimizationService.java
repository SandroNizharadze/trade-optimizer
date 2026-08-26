package com.arcticblu.tradeoptimizer.service;

import com.arcticblu.tradeoptimizer.algorithm.OptimizationResult;
import com.arcticblu.tradeoptimizer.algorithm.TradeCandidate;
import com.arcticblu.tradeoptimizer.algorithm.TradeOptimizer;
import com.arcticblu.tradeoptimizer.dto.request.OptimizeTradesRequest;
import com.arcticblu.tradeoptimizer.dto.response.OptimizationResponse;
import com.arcticblu.tradeoptimizer.dto.response.TradeResponse;
import com.arcticblu.tradeoptimizer.entity.OptimizationRunEntity;
import com.arcticblu.tradeoptimizer.entity.TradeEntity;
import com.arcticblu.tradeoptimizer.repository.OptimizationRunRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeOptimizationService {

    private final TradeOptimizer tradeOptimizer;
    private final OptimizationRunRepository optimizationRunRepository;


    @Transactional
    public OptimizationResponse optimize(OptimizeTradesRequest request) {
        List<TradeCandidate> candidates = request.candidateTrades().stream()
                .map(trade -> new TradeCandidate(
                        trade.tradeName(),
                        trade.marginRequired(),
                        trade.expectedPnl()
                ))
                .toList();

        OptimizationResult result = tradeOptimizer.optimize(
                candidates,
                request.maxMargin()
        );

        OptimizationRunEntity run = new OptimizationRunEntity();

        run.setRequestId(UUID.randomUUID());
        run.setMaxMargin(request.maxMargin());
        run.setTotalExpectedPnl(result.totalExpectedPnl());
        run.setTotalMarginRequired(result.totalMarginRequired());
        run.setCreatedAt(Instant.now());

        for (TradeCandidate candidate : candidates) {
            TradeEntity tradeEntity = new TradeEntity();

            tradeEntity.setId(UUID.randomUUID());
            tradeEntity.setTradeName(candidate.tradeName());
            tradeEntity.setMarginRequired(candidate.marginRequired());
            tradeEntity.setExpectedPnl(candidate.expectedPnl());

            boolean selected = result.selectedTrades().contains(candidate);

            tradeEntity.setSelected(selected);

            run.addTrade(tradeEntity);
        }

        OptimizationRunEntity saved = optimizationRunRepository.save(run);

        List<TradeResponse> selectedTrades = result.selectedTrades().stream()
                .map(trade -> new TradeResponse(
                        trade.tradeName(),
                        trade.marginRequired(),
                        trade.expectedPnl()
                ))
                .toList();

        return new OptimizationResponse(
                saved.getRequestId(),
                selectedTrades,
                saved.getTotalMarginRequired(),
                saved.getTotalExpectedPnl(),
                saved.getCreatedAt()
        );

    }

}
