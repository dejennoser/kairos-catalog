package com.kairos.catalog.repository;

import com.kairos.catalog.entity.ProductTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProductTranslationRepository extends JpaRepository< ProductTranslation, UUID> {

    Optional<ProductTranslation> findByProductIdAndLocale (UUID productId, String locale);
    List<ProductTranslation> findByProductIdInAndLocale(List<UUID> productIds, String locale);
}
