package com.arcticblu.tradeoptimizer.controller;

import com.arcticblu.tradeoptimizer.dto.request.OptimizeTradesRequest;
import com.arcticblu.tradeoptimizer.dto.response.OptimizationResponse;
import com.arcticblu.tradeoptimizer.dto.response.TradeResponse;
import com.arcticblu.tradeoptimizer.service.TradeOptimizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeOptimizationController.class)
class TradeOptimizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TradeOptimizationService tradeOptimizationService;

    @Test
    void shouldOptimizeTradesAndReturnCreated() throws Exception {
        UUID requestId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-08-26T12:00:00Z");

        OptimizationResponse serviceResponse =
                new OptimizationResponse(
                        requestId,
                        List.of(
                                new TradeResponse(
                                        "Trade Alpha",
                                        new BigDecimal("5"),
                                        new BigDecimal("120")
                                ),
                                new TradeResponse(
                                        "Trade Beta",
                                        new BigDecimal("10"),
                                        new BigDecimal("200")
                                )
                        ),
                        new BigDecimal("15"),
                        new BigDecimal("320"),
                        createdAt
                );

        when(tradeOptimizationService.optimize(
                any(OptimizeTradesRequest.class)
        )).thenReturn(serviceResponse);

        mockMvc.perform(
                        post("/api/v1/trades/optimize")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "maxMargin": 15,
                                          "candidateTrades": [
                                            {
                                              "tradeName": "Trade Alpha",
                                              "marginRequired": 5,
                                              "expectedPnl": 120
                                            },
                                            {
                                              "tradeName": "Trade Beta",
                                              "marginRequired": 10,
                                              "expectedPnl": 200
                                            }
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestId")
                        .value(requestId.toString()))
                .andExpect(jsonPath("$.selectedTrades.length()")
                        .value(2))
                .andExpect(jsonPath("$.selectedTrades[0].tradeName")
                        .value("Trade Alpha"))
                .andExpect(jsonPath("$.totalMarginRequired")
                        .value(15))
                .andExpect(jsonPath("$.totalExpectedPnl")
                        .value(320))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-08-26T12:00:00Z"));

        verify(tradeOptimizationService)
                .optimize(any(OptimizeTradesRequest.class));
    }

    @Test
    void shouldReturnDescriptiveBadRequestForNegativeMaxMargin()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/trades/optimize")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "maxMargin": -1,
                                      "candidateTrades": []
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/trades/optimize"))
                .andExpect(jsonPath("$.validationErrors.maxMargin")
                        .exists());

        verifyNoInteractions(tradeOptimizationService);
    }
}