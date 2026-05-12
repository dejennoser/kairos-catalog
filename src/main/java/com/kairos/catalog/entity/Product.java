package com.kairos.catalog.entity;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
   @Column(nullable = false)
   private String name;

    @Nullable
    @Column(columnDefinition = "TEXT")
    private String description;

    @NonNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NonNull
    @Column(nullable = false)
    private String category;

   @Column(nullable = false)
    private Integer stock;

   @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @ElementCollection
    @CollectionTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ProductTranslation> translations = new java.util.ArrayList<>();

    @PrePersist
    protected  void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}



