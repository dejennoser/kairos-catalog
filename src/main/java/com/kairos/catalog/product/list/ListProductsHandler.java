package com.kairos.catalog.product.list;

import com.kairos.catalog.entity.Product;
import com.kairos.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListProductsHandler {
    private final ProductRepository productRepository;

    public List<ListProductsResponse> handle() {
        return productRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    private ListProductsResponse map(Product product) {
        return ListProductsResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrls(product.getImageUrls())
                .build();
    }
}
