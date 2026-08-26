package com.arcticblu.tradeoptimizer.algorithm;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class KnapsackTradeOptimizerTest {

    private final TradeOptimizer optimizer = new KnapsackTradeOptimizer();

    @Test
    void shouldReturnEmptyResultWhenCandidatesAreEmpty() {

        OptimizationResult result = optimizer.optimize(
                List.of(),
                new BigDecimal("10")
        );

        assertThat(result.selectedTrades()).isEmpty();
        assertThat(result.totalMarginRequired()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalExpectedPnl()).isEqualByComparingTo(BigDecimal.ZERO);

    }

    @Test
    void shouldSelectSingleTradeWhenItFits() {
        TradeCandidate alpha = new TradeCandidate(
                "Trade Alpha",
                new BigDecimal("5"),
                new BigDecimal("120")
        );

        OptimizationResult result = optimizer.optimize(
                List.of(alpha),
                new BigDecimal("10")
        );

        assertThat(result.selectedTrades())
                .containsExactly(alpha);

        assertThat(result.totalMarginRequired())
                .isEqualByComparingTo("5");

        assertThat(result.totalExpectedPnl())
                .isEqualByComparingTo("120");
    }

    @Test
    void shouldReturnEmptyResultWhenSingleTradeDoesNotFit() {
        TradeCandidate alpha = new TradeCandidate(
                "Trade Alpha",
                new BigDecimal("15"),
                new BigDecimal("120")
        );

        OptimizationResult result = optimizer.optimize(
                List.of(alpha),
                new BigDecimal("10")
        );

        assertThat(result.selectedTrades()).isEmpty();
        assertThat(result.totalMarginRequired())
                .isEqualByComparingTo("0");
        assertThat(result.totalExpectedPnl())
                .isEqualByComparingTo("0");
    }

    @Test
    void shouldChooseTradeWithHigherPnlWhenOnlyOneCanFit() {
        TradeCandidate alpha = new TradeCandidate(
                "Trade Alpha",
                new BigDecimal("10"),
                new BigDecimal("100")
        );

        TradeCandidate beta = new TradeCandidate(
                "Trade Beta",
                new BigDecimal("10"),
                new BigDecimal("150")
        );

        OptimizationResult result = optimizer.optimize(
                List.of(alpha, beta),
                new BigDecimal("10")
        );

        assertThat(result.selectedTrades())
                .containsExactly(beta);

        assertThat(result.totalExpectedPnl())
                .isEqualByComparingTo("150");
    }

    @Test
    void shouldChooseCombinationWithHighestTotalPnl() {
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

        TradeCandidate gamma = new TradeCandidate(
                "Trade Gamma",
                new BigDecimal("3"),
                new BigDecimal("80")
        );

        TradeCandidate delta = new TradeCandidate(
                "Trade Delta",
                new BigDecimal("8"),
                new BigDecimal("160")
        );

        OptimizationResult result = optimizer.optimize(
                List.of(alpha, beta, gamma, delta),
                new BigDecimal("15")
        );

        assertThat(result.selectedTrades())
                .containsExactlyInAnyOrder(alpha, beta);

        assertThat(result.totalMarginRequired())
                .isEqualByComparingTo("15");

        assertThat(result.totalExpectedPnl())
                .isEqualByComparingTo("320");
    }

    @Test
    void shouldNeverExceedMaximumMargin() {
        TradeCandidate alpha = new TradeCandidate(
                "Trade Alpha",
                new BigDecimal("8"),
                new BigDecimal("100")
        );

        TradeCandidate beta = new TradeCandidate(
                "Trade Beta",
                new BigDecimal("8"),
                new BigDecimal("100")
        );

        OptimizationResult result = optimizer.optimize(
                List.of(alpha, beta),
                new BigDecimal("10")
        );

        assertThat(result.totalMarginRequired()
                .compareTo(new BigDecimal("10")))
                .isLessThanOrEqualTo(0);

        assertThat(result.selectedTrades())
                .hasSize(1);
    }

    @Test
    void shouldNotSelectTradeWithNegativePnl() {
        TradeCandidate badTrade = new TradeCandidate(
                "Bad Trade",
                new BigDecimal("5"),
                new BigDecimal("-100")
        );

        OptimizationResult result = optimizer.optimize(
                List.of(badTrade),
                new BigDecimal("10")
        );

        assertThat(result.selectedTrades()).isEmpty();
        assertThat(result.totalExpectedPnl())
                .isEqualByComparingTo("0");
    }

    @Test
    void shouldPreferLowerMarginWhenPnlIsEqual() {
        TradeCandidate alpha = new TradeCandidate(
                "Trade Alpha",
                new BigDecimal("10"),
                new BigDecimal("100")
        );

        TradeCandidate beta = new TradeCandidate(
                "Trade Beta",
                new BigDecimal("5"),
                new BigDecimal("100")
        );

        OptimizationResult result = optimizer.optimize(
                List.of(alpha, beta),
                new BigDecimal("10")
        );

        assertThat(result.selectedTrades())
                .containsExactly(beta);

        assertThat(result.totalMarginRequired())
                .isEqualByComparingTo("5");
    }

}
