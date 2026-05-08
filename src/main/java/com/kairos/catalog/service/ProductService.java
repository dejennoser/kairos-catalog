package com.kairos.catalog.service;

import com.kairos.catalog.dto.ProductRequest;
import com.kairos.catalog.dto.ProductResponse;
import com.kairos.catalog.entity.Product;
import com.kairos.catalog.entity.ProductTranslation;
import com.kairos.catalog.exception.ProductNotFoundException;
import com.kairos.catalog.repository.ProductRepository;
import com.kairos.catalog.repository.ProductTranslationRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTranslationRepository translationRepository;
    private final MinioService minioService;
    private final OpenSearchService openSearchService;

    // ✅ GET ALL
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll(String locale) {
        return productRepository.findAll()
                .stream()
                .map(product -> toResponse(product, locale))
                .toList();
    }

    // ✅ GET BY ID
    @Transactional(readOnly = true)
    public ProductResponse findById(@NonNull UUID id, @NonNull String locale) {
        return productRepository.findById(id)
                .map(product -> toResponse(product, locale))
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    // ✅ GET BY CATEGORY
    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategory(@NonNull String category, @NonNull String locale) {
        return productRepository.findByCategory(category)
                .stream()
                .map(product -> toResponse(product, locale))
                .toList();
    }

    // ✅ SEARCH BY NAME
    @Transactional(readOnly = true)
    public List<ProductResponse> searchByName(@NonNull String name, @NonNull String locale) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(product -> toResponse(product, locale))
                .toList();
    }

    // ✅ CREATE PRODUCT
    @Transactional
    public ProductResponse create(@NonNull ProductRequest request, @NonNull String locale) {

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .imageUrl(null)
                .build();

        Product saved = productRepository.save(product);

        // ✅ save translations
        if (request.getTranslations() != null) {
            request.getTranslations().forEach((lang, translation) -> {
                ProductTranslation pt = ProductTranslation.builder()
                        .product(saved)
                        .locale(lang.toLowerCase()) // ✅ normalize
                        .name(translation.getName())
                        .description(translation.getDescription())
                        .build();
                translationRepository.save(pt);
            });
        }

        // ✅ index in OpenSearch
        openSearchService.indexProduct(saved);

        return toResponse(saved, locale);
    }

    // ✅ UPDATE PRODUCT
    @Transactional
    public ProductResponse update(@NonNull UUID id,
                                  @NonNull ProductRequest request,
                                  @NonNull String locale) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());

        return toResponse(productRepository.save(product), locale);
    }

    // ✅ UPLOAD IMAGE
    @Transactional
    public ProductResponse uploadImage(@NonNull UUID id,
                                       @NonNull MultipartFile file,
                                       @NonNull String locale) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // ✅ delete old image if exists
        if (product.getImageUrl() != null) {
            minioService.deleteImage(product.getImageUrl());
        }

        // ✅ upload new image
        String imageUrl = minioService.uploadImage(file);
        product.setImageUrl(imageUrl);

        return toResponse(productRepository.save(product), locale);
    }

    // ✅ DELETE PRODUCT
    @Transactional
    public void delete(@NonNull UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);

        // ✅ remove from search index
        openSearchService.deleteProduct(id);
    }

    // ✅ FUZZY SEARCH
    @Transactional(readOnly = true)
    public List<ProductResponse> fuzzySearch(@NonNull String query,
                                             @NonNull String locale) {

        List<UUID> ids = openSearchService.fuzzySearch(query);

        return ids.stream()
                .map(productRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(product -> toResponse(product, locale))
                .toList();
    }

    // ✅ RESPONSE MAPPING + FALLBACK
    private ProductResponse toResponse(@NonNull Product product,
                                       @NonNull String locale) {

        ProductTranslation translation =
                translationRepository.findByProductIdAndLocale(product.getId(), locale)
                        .orElseGet(() ->
                                translationRepository.findByProductIdAndLocale(product.getId(), "en")
                                        .orElse(null)
                        );

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .translatedName(translation != null ? translation.getName() : null)
                .translatedDescription(translation != null ? translation.getDescription() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}