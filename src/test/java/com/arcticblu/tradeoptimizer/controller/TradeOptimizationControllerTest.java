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
import com.arcticblu.tradeoptimizer.exception.OptimizationRunNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

    @Test
    void shouldReturnOptimizationRunByRequestId() throws Exception {
        // given
        UUID requestId = UUID.randomUUID();

        OptimizationResponse response = createOptimizationResponse(requestId);

        when(tradeOptimizationService.getByRequestId(requestId))
                .thenReturn(response);

        // when / then
        mockMvc.perform(
                        get("/api/v1/trades/{requestId}", requestId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId")
                        .value(requestId.toString()))
                .andExpect(jsonPath("$.selectedTrades.length()")
                        .value(2))
                .andExpect(jsonPath("$.selectedTrades[0].tradeName")
                        .value("Trade Alpha"))
                .andExpect(jsonPath("$.selectedTrades[1].tradeName")
                        .value("Trade Beta"))
                .andExpect(jsonPath("$.totalMarginRequired")
                        .value(15))
                .andExpect(jsonPath("$.totalExpectedPnl")
                        .value(320))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-08-26T12:00:00Z"));

        verify(tradeOptimizationService)
                .getByRequestId(requestId);
    }

    @Test
    void shouldReturnNotFoundWhenRequestIdDoesNotExist()
            throws Exception {

        // given
        UUID requestId = UUID.randomUUID();

        when(tradeOptimizationService.getByRequestId(requestId))
                .thenThrow(
                        new OptimizationRunNotFoundException(requestId)
                );

        // when / then
        mockMvc.perform(
                        get("/api/v1/trades/{requestId}", requestId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Optimization run not found: "
                                        + requestId
                        ))
                .andExpect(jsonPath("$.path")
                        .value(
                                "/api/v1/trades/" + requestId
                        ));

        verify(tradeOptimizationService)
                .getByRequestId(requestId);
    }

    @Test
    void shouldReturnPaginatedOptimizationRuns() throws Exception {
        // given
        UUID requestId = UUID.randomUUID();

        OptimizationResponse response = createOptimizationResponse(requestId);

        Pageable pageable = PageRequest.of(0, 2);

        Page<OptimizationResponse> responsePage =
                new PageImpl<>(
                        List.of(response),
                        pageable,
                        1
                );

        when(tradeOptimizationService.getAll(any(Pageable.class)))
                .thenReturn(responsePage);

        // when / then
        mockMvc.perform(
                        get("/api/v1/trades")
                                .param("page", "0")
                                .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(1))
                .andExpect(jsonPath("$.content[0].requestId")
                        .value(requestId.toString()))
                .andExpect(jsonPath(
                        "$.content[0].totalExpectedPnl"
                ).value(320));

        verify(tradeOptimizationService).getAll(
                argThat(receivedPageable ->
                        receivedPageable.getPageNumber() == 0
                                && receivedPageable.getPageSize() == 2
                )
        );
    }

    private OptimizationResponse createOptimizationResponse(
            UUID requestId
    ) {
        return new OptimizationResponse(
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
                Instant.parse("2026-08-26T12:00:00Z")
        );
    }
}