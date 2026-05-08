package com.kairos.catalog.dto;

import lombok.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    @NonNull
    private UUID id;

    @NonNull
    private String name;

    @Nullable
    private String description;

    @NonNull
    private BigDecimal price;

    @NonNull
    private String category;

    @NonNull
    private Integer stock;

    @Nullable
    private String imageUrl;

    @Nullable
    private String translatedName;

    @Nullable
    private String translatedDescription;

    @NonNull
    private LocalDateTime createdAt;

    @NonNull
    private LocalDateTime updatedAt;

}
