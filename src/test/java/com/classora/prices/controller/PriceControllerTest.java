package com.classora.prices.controller;

import com.classora.prices.dto.PriceResponse;
import com.classora.prices.exceptions.GlobalExceptionHandler;
import com.classora.prices.exceptions.PriceNotFoundException;
import com.classora.prices.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PriceControllerTest {

    @Mock
    private PriceService priceService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PriceController priceController = new PriceController(priceService);
        mockMvc = MockMvcBuilders.standaloneSetup(priceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnOkWithPriceResponseWhenPriceApplies() throws Exception {
        PriceResponse response = new PriceResponse(
                35455L, 1L, 1L,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                new BigDecimal("35.50"), "EUR"
        );

        when(priceService.getApplicablePrice(eq(1L), eq(35455L), any(LocalDateTime.class)))
                .thenReturn(response);

        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.curr").value("EUR"));
    }

    @Test
    void shouldReturnNotFoundWhenNoPriceApplies() throws Exception {
        when(priceService.getApplicablePrice(eq(1L), eq(35455L), any(LocalDateTime.class)))
                .thenThrow(new PriceNotFoundException(35455L, 1L, "2020-06-14T21:00:00"));

        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T21:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void shouldReturnBadRequestWhenProductIdIsMissing() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }
}