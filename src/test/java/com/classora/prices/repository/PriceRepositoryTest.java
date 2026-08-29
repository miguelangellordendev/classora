package com.classora.prices.repository;

import com.classora.prices.model.Price;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class PriceRepositoryTest {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private void persistPrice(Long brandId, LocalDateTime start, LocalDateTime end,
                              Long priceList, Long productId, Integer priority,
                              BigDecimal price, String curr) {
        Price p = new Price(null, brandId, start, end, priceList, productId, priority, price, curr);
        entityManager.persist(p);
    }

    @Test
    void shouldReturnPricesOrderedByPriorityDescWhenMultipleApply() {
        persistPrice(1L,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                1L, 35455L, 0, new BigDecimal("35.50"), "EUR");

        persistPrice(1L,
                LocalDateTime.of(2020, 6, 14, 15, 0, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30, 0),
                2L, 35455L, 1, new BigDecimal("25.45"), "EUR");

        entityManager.flush();

        List<Price> result = priceRepository.findApplicablePrices(
                1L, 35455L, LocalDateTime.of(2020, 6, 14, 16, 0, 0));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPriority()).isEqualTo(1);
        assertThat(result.get(0).getPriceList()).isEqualTo(2L);
        assertThat(result.get(1).getPriority()).isEqualTo(0);
    }

    @Test
    void shouldReturnEmptyListWhenNoPriceAppliesForGivenDate() {
        persistPrice(1L,
                LocalDateTime.of(2020, 6, 14, 15, 0, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30, 0),
                2L, 35455L, 1, new BigDecimal("25.45"), "EUR");

        entityManager.flush();

        List<Price> result = priceRepository.findApplicablePrices(
                1L, 35455L, LocalDateTime.of(2020, 6, 14, 21, 0, 0));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotReturnPricesFromDifferentBrandOrProduct() {
        persistPrice(1L,
                LocalDateTime.of(2020, 6, 14, 0, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                1L, 35455L, 0, new BigDecimal("35.50"), "EUR");

        entityManager.flush();

        List<Price> resultOtherBrand = priceRepository.findApplicablePrices(
                2L, 35455L, LocalDateTime.of(2020, 6, 14, 10, 0, 0));
        List<Price> resultOtherProduct = priceRepository.findApplicablePrices(
                1L, 99999L, LocalDateTime.of(2020, 6, 14, 10, 0, 0));

        assertThat(resultOtherBrand).isEmpty();
        assertThat(resultOtherProduct).isEmpty();
    }
}