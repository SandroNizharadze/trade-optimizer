package com.arcticblu.tradeoptimizer.service;

import com.arcticblu.tradeoptimizer.algorithm.OptimizationResult;
import com.arcticblu.tradeoptimizer.algorithm.TradeCandidate;
import com.arcticblu.tradeoptimizer.algorithm.TradeOptimizer;
import com.arcticblu.tradeoptimizer.dto.request.OptimizeTradesRequest;
import com.arcticblu.tradeoptimizer.dto.response.OptimizationResponse;
import com.arcticblu.tradeoptimizer.dto.response.TradeResponse;
import com.arcticblu.tradeoptimizer.entity.OptimizationRunEntity;
import com.arcticblu.tradeoptimizer.entity.TradeEntity;
import com.arcticblu.tradeoptimizer.exception.OptimizationRunNotFoundException;
import com.arcticblu.tradeoptimizer.repository.OptimizationRunRepository;
import com.arcticblu.tradeoptimizer.repository.TradeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeOptimizationService {

    private final TradeOptimizer tradeOptimizer;
    private final OptimizationRunRepository optimizationRunRepository;
    private final TradeRepository tradeRepository;


    @Transactional
    public OptimizationResponse optimize(OptimizeTradesRequest request) {
        List<TradeCandidate> candidates = request.candidateTrades().stream()
                .map(trade -> new TradeCandidate(
                        trade.tradeName(),
                        trade.marginRequired(),
                        trade.expectedPnl()
                ))
                .toList();

        OptimizationResult initialResult = tradeOptimizer.optimize(
                candidates,
                request.maxMargin()
        );

        BigDecimal remainingMargin = request.maxMargin().subtract(initialResult.totalMarginRequired());

        OptimizationResult result = initialResult;

        if (remainingMargin.compareTo(BigDecimal.ZERO) > 0) {

            List<TradeEntity> databaseTrades =
                    tradeRepository
                            .findBySelectedFalseAndMarginRequiredLessThanEqual(
                                    remainingMargin
                            );

            List<TradeCandidate> databaseCandidates =
                    databaseTrades.stream()
                            .map(trade -> new TradeCandidate(
                                    trade.getTradeName(),
                                    trade.getMarginRequired(),
                                    trade.getExpectedPnl()
                            ))
                            .toList();

            OptimizationResult databaseResult =
                    tradeOptimizer.optimize(
                            databaseCandidates,
                            remainingMargin
                    );

            for (TradeEntity databaseTrade : databaseTrades) {

                TradeCandidate candidate = new TradeCandidate(
                        databaseTrade.getTradeName(),
                        databaseTrade.getMarginRequired(),
                        databaseTrade.getExpectedPnl()
                );

                if (databaseResult.selectedTrades().contains(candidate)) {
                    databaseTrade.setSelected(true);
                }
            }

            result = combineResults(
                    initialResult,
                    databaseResult
            );
        }


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

    private OptimizationResult combineResults(OptimizationResult initialResult, OptimizationResult databaseResult) {

        List<TradeCandidate> selected = new ArrayList<>(initialResult.selectedTrades());

        selected.addAll(databaseResult.selectedTrades());

        return new OptimizationResult(
                selected,
                initialResult.totalMarginRequired().add(databaseResult.totalMarginRequired()),
                initialResult.totalExpectedPnl().add(databaseResult.totalExpectedPnl())
        );

    }

    @Transactional(readOnly = true)
    public OptimizationResponse getByRequestId(UUID requestId) {
        OptimizationRunEntity run = optimizationRunRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new OptimizationRunNotFoundException(requestId)
                );

        return toResponse(run);
    }

    @Transactional(readOnly = true)
    public Page<OptimizationResponse> getAll(Pageable pageable) {
        return optimizationRunRepository
                .findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    private OptimizationResponse toResponse(
            OptimizationRunEntity run
    ) {
        List<TradeResponse> selectedTrades = run.getTrades()
                .stream()
                .filter(TradeEntity::isSelected)
                .map(trade -> new TradeResponse(
                        trade.getTradeName(),
                        trade.getMarginRequired(),
                        trade.getExpectedPnl()
                ))
                .toList();

        return new OptimizationResponse(
                run.getRequestId(),
                selectedTrades,
                run.getTotalMarginRequired(),
                run.getTotalExpectedPnl(),
                run.getCreatedAt()
        );
    }

}
