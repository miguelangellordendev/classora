package com.classora.prices.controller;

import com.classora.prices.dto.PriceResponse;
import com.classora.prices.service.PriceService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Validated
public class PriceController {

    private final PriceService priceService;

    @GetMapping
    public ResponseEntity<PriceResponse> getApplicablePrice(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @RequestParam @NotNull Long productId,
            @RequestParam @NotNull Long brandId) {

        PriceResponse response = priceService.getApplicablePrice(brandId, productId, applicationDate);
        return ResponseEntity.ok(response);
    }
}