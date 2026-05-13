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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTranslationRepository translationRepository;
    private final MinioService minioService;
    private final OpenSearchService openSearchService;

    // =========================
    // READ OPERATIONS
    // =========================

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll(String locale) {
        return productRepository.findAll()
                .stream()
                .map(product -> toResponse(product, locale))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(@NonNull UUID id, @NonNull String locale) {
        return productRepository.findById(id)
                .map(product -> toResponse(product, locale))
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategory(@NonNull String category, @NonNull String locale) {
        return productRepository.findByCategory(category)
                .stream()
                .map(product -> toResponse(product, locale))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchByName(@NonNull String name, @NonNull String locale) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(product -> toResponse(product, locale))
                .toList();
    }

    // =========================
    // CREATE (NO IMAGES)
    // =========================

    @Transactional
    public ProductResponse create(@NonNull ProductRequest request, @NonNull String locale) {

        Product product = buildProduct(request);
        productRepository.save(product);

        saveTranslations(product, request);
        openSearchService.indexProduct(product);

        return toResponse(product, locale);
    }

    // =========================
    // CREATE WITH IMAGES
    // =========================

    @Transactional
    public ProductResponse createWithImages(
            ProductRequest request,
            List<MultipartFile> images,
            String locale
    ) {
        Product product = buildProduct(request);
        productRepository.save(product);

        saveTranslations(product, request);

        if (images != null && !images.isEmpty()) {
            if (product.getImageUrls() == null) {
                product.setImageUrls(new ArrayList<>());
            }

            for (MultipartFile file : images) {
                String imageUrl = minioService.uploadImage(file);
                product.getImageUrls().add(imageUrl);
            }
        }

        openSearchService.indexProduct(product);

        return toResponse(product, locale);
    }

    // =========================
    // UPDATE PRODUCT (NO IMAGES)
    // =========================

    @Transactional
    public ProductResponse update(
            @NonNull UUID id,
            @NonNull ProductRequest request,
            @NonNull String locale
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());

        return toResponse(productRepository.save(product), locale);
    }

    // =========================
    // REPLACE IMAGES (ADMIN)
    // =========================

    @Transactional
    public ProductResponse uploadImage(
            @NonNull UUID id,
            @NonNull MultipartFile file,
            @NonNull String locale
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // delete old images
        product.getImageUrls().forEach(minioService::deleteImage);
        product.getImageUrls().clear();

        // upload new image
        product.getImageUrls().add(minioService.uploadImage(file));

        return toResponse(productRepository.save(product), locale);
    }

    // =========================
    // DELETE
    // =========================

    @Transactional
    public void delete(@NonNull UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);
        openSearchService.deleteProduct(id);
    }

    // =========================
    // SEARCH
    // =========================

    @Transactional(readOnly = true)
    public List<ProductResponse> search(
            @NonNull String query,
            int page,
            int size,
            @NonNull String locale
    ) {
        List<UUID> ids = openSearchService.search(query, page, size);

        return ids.stream()
                .map(productRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(product -> toResponse(product, locale))
                .toList();
    }

    // =========================
    // HELPERS
    // =========================

    private Product buildProduct(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .build();
    }

    private void saveTranslations(Product product, ProductRequest request) {
        if (request.getTranslations() != null) {
            request.getTranslations().forEach((lang, translation) -> {
                ProductTranslation pt = ProductTranslation.builder()
                        .product(product)
                        .locale(lang.toLowerCase())
                        .name(translation.getName())
                        .description(translation.getDescription())
                        .build();
                translationRepository.save(pt);
            });
        }
    }

    private ProductResponse toResponse(@NonNull Product product, @NonNull String locale) {

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
                .imageUrls(product.getImageUrls())
                .translatedName(translation != null ? translation.getName() : null)
                .translatedDescription(translation != null ? translation.getDescription() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}