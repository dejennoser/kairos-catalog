package com.kairos.catalog.product.create;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class CreateProductResponse {

    UUID id;
    String name;
    String description;
    BigDecimal price;
    List<String> imageUrls;
}