package com.kairos.catalog.product.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
public class CreateProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String category;

    @NotNull
    private Integer stock;

    private Map<String, Translation> translations;

    @Data
    @NoArgsConstructor
    public static class Translation {
        private String name;
        private String description;
    }
}