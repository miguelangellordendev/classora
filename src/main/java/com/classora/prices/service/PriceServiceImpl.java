package com.classora.prices.service;

import com.classora.prices.dto.PriceResponse;
import com.classora.prices.mapper.PriceMapper;
import com.classora.prices.model.Price;
import com.classora.prices.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    private final PriceMapper priceMapper;

    @Override
    public PriceResponse getApplicablePrice(Long brandId, Long productId, LocalDateTime applicationDate) {
        Optional<Price> applicablePrice = priceRepository
                .findApplicablePrices(brandId, productId, applicationDate)
                .stream()
                .findFirst();

        return null;
    }
}
