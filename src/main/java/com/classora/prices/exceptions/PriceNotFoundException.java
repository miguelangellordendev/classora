package com.classora.prices.exceptions;

public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(Long productId, Long brandId, String applicationDate) {
        super(String.format(
                "No se encuentra ninguna tarifa aplicable para el producto con id: %d - cadena %d en la fecha: fecha %s",
                productId, brandId, applicationDate));
    }
}