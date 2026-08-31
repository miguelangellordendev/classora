package com.classora.prices.service;

import com.classora.prices.dto.PriceResponse;
import com.classora.prices.exceptions.PriceNotFoundException;
import com.classora.prices.mapper.PriceMapper;
import com.classora.prices.model.Price;
import com.classora.prices.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    private static final Long BRAND_ID = 1L;

    private static final Long PRODUCT_ID = 35455L;

    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0, 0);

    @InjectMocks
    private PriceServiceImpl priceServiceImpl;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private PriceMapper priceMapper;

    private Price price;

    private PriceResponse priceResponse;

    @BeforeEach
    void setUp() {
        price = new Price(
                1L, BRAND_ID,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                1L, PRODUCT_ID, 0,
                new BigDecimal("35.50"), "EUR"
        );

        priceResponse = new PriceResponse(
                PRODUCT_ID, BRAND_ID, 1L,
                price.getStartDate(), price.getEndDate(),
                price.getPrice(), price.getCurr()
        );
    }

    @Test
    void shouldReturnApplicablePriceWhenFound() {
        when(priceRepository.findApplicablePrices(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of(price));
        when(priceMapper.toResponse(price)).thenReturn(priceResponse);

        PriceResponse result = priceServiceImpl.getApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        assertThat(result).isEqualTo(priceResponse);
        verify(priceMapper).toResponse(price);
    }

    @Test
    void shouldThrowPriceNotFoundExceptionWhenNoPriceApplies() {
        when(priceRepository.findApplicablePrices(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(List.of());

        assertThatThrownBy(() ->
                priceServiceImpl.getApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE)
        ).isInstanceOf(PriceNotFoundException.class);
    }
}