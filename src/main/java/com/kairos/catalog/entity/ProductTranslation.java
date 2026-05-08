package com.kairos.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Entity
@Table(name = "product_translations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NonNull
    @Column(nullable = false, length = 5)
    private String locale;

    @NonNull
    @Column(nullable = false)
    private String name;

    @Nullable
    @Column(columnDefinition = "TEXT")
    private String description;
}
