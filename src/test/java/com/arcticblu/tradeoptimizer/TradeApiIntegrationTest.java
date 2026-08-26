package com.arcticblu.tradeoptimizer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class TradeApiIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldOptimizePersistAndRetrieveRun() throws Exception {

        MvcResult created = mockMvc.perform(
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
                                            },
                                            {
                                              "tradeName": "Trade Gamma",
                                              "marginRequired": 3,
                                              "expectedPnl": 80
                                            },
                                            {
                                              "tradeName": "Trade Delta",
                                              "marginRequired": 8,
                                              "expectedPnl": 160
                                            }
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.selectedTrades.length()")
                        .value(2))
                .andExpect(jsonPath("$.totalMarginRequired")
                        .value(15))
                .andExpect(jsonPath("$.totalExpectedPnl")
                        .value(320))
                .andReturn();

        mockMvc.perform(
                        get("/api/v1/trades")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(1));
    }
}