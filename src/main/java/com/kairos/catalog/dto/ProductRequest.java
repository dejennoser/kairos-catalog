package com.kairos.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotNull
    @NotBlank(message = "error.validation.name.required")
    private String name;

    @Nullable
    private String description;

    @NonNull
    @NotNull(message = "error.validation.price.invalid")
    @DecimalMin(value = "0.0", inclusive = false, message = "error.validation.price.invalid")
    private BigDecimal price;

    @NonNull
    @NotBlank(message = "error.validation.category.required")
    private String category;

    @NotNull(message = "error.validation.stock.invalid")
    @Min(value = 0, message = "error.validation.stock.invalid")
    private Integer stock;

}
