package com.kairos.catalog.product.search;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.SplittableRandom;
import java.util.UUID;

@Value
@Builder
public class SearchProductResponse {
    UUID id;
    String name;
    String description;
    BigDecimal price;
    List<String> imageUrls;
}
