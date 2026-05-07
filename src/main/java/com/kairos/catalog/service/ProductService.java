package com.kairos.catalog.service;

import com.kairos.catalog.dto.ProductRequest;
import com.kairos.catalog.dto.ProductResponse;
import com.kairos.catalog.exception.ProductNotFoundException;
import com.kairos.catalog.repository.ProductRepository;
import org.jspecify.annotations.NonNull;
import lombok.RequiredArgsConstructor;
import com.kairos.catalog.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final MinioService minioService;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return  productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(@NonNull UUID id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategory(@NonNull String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchByName(@NonNull String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse create(@NonNull ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .imageUrl(null)
                .build();

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(@NonNull UUID id, @NonNull ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());

        return toResponse(productRepository.save(product));
    }

@Transactional
public ProductResponse uploadImage(@NonNull UUID id, @NonNull MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        if(product.getImageUrl() != null) {
            minioService.deleteImage(product.getImageUrl());
        }

        String imageUrl = minioService.uploadImage(file);
        product.setImageUrl(imageUrl);

        return  toResponse(productRepository.save(product));

}

@Transactional
    public void delete(@NonNull UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
}

private  ProductResponse toResponse(@NonNull Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
}
}
