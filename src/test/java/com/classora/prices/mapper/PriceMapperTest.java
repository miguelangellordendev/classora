package com.classora.prices.mapper;

import com.classora.prices.dto.PriceResponse;
import com.classora.prices.model.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PriceMapperTest {

    private final PriceMapper priceMapper = new PriceMapperImpl();

    @Test
    void shouldMapPriceToPriceResponse() {
        Price price = new Price(
                1L,
                1L,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                1L,
                35455L,
                0,
                new BigDecimal("35.50"),
                "EUR"
        );

        PriceResponse result = priceMapper.toResponse(price);

        assertThat(result.productId()).isEqualTo(35455L);
        assertThat(result.brandId()).isEqualTo(1L);
        assertThat(result.priceList()).isEqualTo(1L);
        assertThat(result.startDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0, 0));
        assertThat(result.endDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(result.curr()).isEqualTo("EUR");
    }

    @Test
    void shouldReturnNullWhenPriceIsNull() {
        PriceResponse result = priceMapper.toResponse(null);

        assertThat(result).isNull();
    }
}