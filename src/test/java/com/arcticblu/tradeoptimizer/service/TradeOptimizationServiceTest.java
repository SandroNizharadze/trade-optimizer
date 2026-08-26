package com.arcticblu.tradeoptimizer.service;

import com.arcticblu.tradeoptimizer.algorithm.OptimizationResult;
import com.arcticblu.tradeoptimizer.algorithm.TradeCandidate;
import com.arcticblu.tradeoptimizer.algorithm.TradeOptimizer;
import com.arcticblu.tradeoptimizer.dto.request.CandidateTradeRequest;
import com.arcticblu.tradeoptimizer.dto.request.OptimizeTradesRequest;
import com.arcticblu.tradeoptimizer.entity.OptimizationRunEntity;
import com.arcticblu.tradeoptimizer.entity.TradeEntity;
import com.arcticblu.tradeoptimizer.repository.OptimizationRunRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeOptimizationServiceTest {

    @Mock
    private TradeOptimizer tradeOptimizer;

    @Mock
    private OptimizationRunRepository optimizationRunRepository;

    @InjectMocks
    private TradeOptimizationService service;

    @Test
    void shouldOptimizeAndPersistRun() {
        // given
        OptimizeTradesRequest request = new OptimizeTradesRequest(
                new BigDecimal("15"),
                List.of(
                        new CandidateTradeRequest(
                                "Trade Alpha",
                                new BigDecimal("5"),
                                new BigDecimal("120")
                        ),
                        new CandidateTradeRequest(
                                "Trade Beta",
                                new BigDecimal("10"),
                                new BigDecimal("200")
                        ),
                        new CandidateTradeRequest(
                                "Trade Gamma",
                                new BigDecimal("3"),
                                new BigDecimal("80")
                        )
                )
        );

        TradeCandidate alpha = new TradeCandidate(
                "Trade Alpha",
                new BigDecimal("5"),
                new BigDecimal("120")
        );

        TradeCandidate beta = new TradeCandidate(
                "Trade Beta",
                new BigDecimal("10"),
                new BigDecimal("200")
        );

        OptimizationResult result = new OptimizationResult(
                List.of(alpha, beta),
                new BigDecimal("15"),
                new BigDecimal("320")
        );

        when(tradeOptimizer.optimize(anyList(), any()))
                .thenReturn(result);

        when(optimizationRunRepository.save(any(OptimizationRunEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        var response = service.optimize(request);

        // then
        assertThat(response.totalMarginRequired())
                .isEqualByComparingTo("15");

        assertThat(response.totalExpectedPnl())
                .isEqualByComparingTo("320");

        assertThat(response.selectedTrades())
                .hasSize(2);

        ArgumentCaptor<OptimizationRunEntity> captor =
                ArgumentCaptor.forClass(OptimizationRunEntity.class);

        verify(optimizationRunRepository).save(captor.capture());

        OptimizationRunEntity savedRun = captor.getValue();

        assertThat(savedRun.getTrades()).hasSize(3);

        assertThat(savedRun.getTrades())
                .filteredOn(TradeEntity::isSelected)
                .extracting("tradeName")
                .containsExactlyInAnyOrder(
                        "Trade Alpha",
                        "Trade Beta"
                );
    }
}

