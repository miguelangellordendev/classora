package com.classora.prices.mapper;

import com.classora.prices.dto.PriceResponse;
import com.classora.prices.model.Price;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceMapper {

    PriceResponse toResponse(Price price);
}