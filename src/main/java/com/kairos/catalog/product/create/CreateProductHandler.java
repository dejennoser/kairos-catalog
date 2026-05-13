package com.kairos.catalog.product.create;

import com.kairos.catalog.entity.Product;
import com.kairos.catalog.entity.ProductTranslation;
import com.kairos.catalog.repository.ProductRepository;
import com.kairos.catalog.repository.ProductTranslationRepository;
import com.kairos.catalog.service.MinioService;
import com.kairos.catalog.service.OpenSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateProductHandler {

    private final ProductRepository productRepository;
    private final ProductTranslationRepository translationRepository;
    private final MinioService minioService;
    private final OpenSearchService openSearchService;

    @Transactional //  REQUIRED
    public Product handle(CreateProductRequest request, List<MultipartFile> images) {

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .imageUrls(new ArrayList<>()) // initialized
                .build();

        productRepository.save(product);

        // translations
        if (request.getTranslations() != null) {
            request.getTranslations().forEach((locale, t) -> {
                translationRepository.save(
                        ProductTranslation.builder()
                                .product(product)
                                .locale(locale.toLowerCase())
                                .name(t.getName())
                                .description(t.getDescription())
                                .build()
                );
            });
        }

        // images
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                String imageUrl = minioService.uploadImage(file);
                product.getImageUrls().add(imageUrl);
            }
        }

        //  SAVE AGAIN so imageUrls are persisted
        productRepository.save(product);

        // index search
        openSearchService.indexProduct(product);

        return product;
    }
}