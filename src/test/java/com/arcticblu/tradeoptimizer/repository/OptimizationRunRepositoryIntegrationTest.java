package com.arcticblu.tradeoptimizer.repository;

import com.arcticblu.tradeoptimizer.entity.OptimizationRunEntity;
import com.arcticblu.tradeoptimizer.entity.TradeEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class OptimizationRunRepositoryIntegrationTest {

    @Autowired
    OptimizationRunRepository optimizationRunRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void shouldSaveAndLoadOptimizationRunWithTrades() {
        UUID requestId = UUID.randomUUID();


        OptimizationRunEntity run = new OptimizationRunEntity();
        run.setRequestId(requestId);
        run.setMaxMargin(new BigDecimal("15.00"));
        run.setTotalMarginRequired(new BigDecimal("15.00"));
        run.setTotalExpectedPnl(new BigDecimal("320.00"));
        run.setCreatedAt(Instant.now());

        TradeEntity alpha = new TradeEntity();
        alpha.setId(UUID.randomUUID());
        alpha.setTradeName("Trade Alpha");
        alpha.setMarginRequired(new BigDecimal("5.00"));
        alpha.setExpectedPnl(new BigDecimal("120.00"));
        alpha.setSelected(true);

        TradeEntity beta = new TradeEntity();
        beta.setId(UUID.randomUUID());
        beta.setTradeName("Trade Beta");
        beta.setMarginRequired(new BigDecimal("10.00"));
        beta.setExpectedPnl(new BigDecimal("200.00"));
        beta.setSelected(true);

        run.addTrade(alpha);
        run.addTrade(beta);


        // when
        optimizationRunRepository.saveAndFlush(run);

        entityManager.clear();

        OptimizationRunEntity savedRun = optimizationRunRepository
                .findById(requestId)
                .orElse(null);

        // then
        assertThat(savedRun).isNotNull();
        assertThat(savedRun.getRequestId()).isEqualTo(requestId);

        assertThat(savedRun.getMaxMargin())
                .isEqualByComparingTo(new BigDecimal("15.00"));

        assertThat(savedRun.getTotalMarginRequired())
                .isEqualByComparingTo(new BigDecimal("15.00"));

        assertThat(savedRun.getTotalExpectedPnl())
                .isEqualByComparingTo(new BigDecimal("320.00"));

        assertThat(savedRun.getTrades())
                .extracting(TradeEntity::getTradeName)
                .containsExactlyInAnyOrder(
                        "Trade Alpha",
                        "Trade Beta"
                );
    }
}
