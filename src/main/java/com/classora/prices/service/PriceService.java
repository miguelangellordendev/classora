package com.classora.prices.service;

import com.classora.prices.dto.PriceResponse;

import java.time.LocalDateTime;

public interface PriceService {

    PriceResponse getApplicablePrice(Long brandId, Long productId, LocalDateTime applicationDate);

}