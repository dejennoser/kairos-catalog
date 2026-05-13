package com.kairos.catalog.product.search;

import com.kairos.catalog.entity.Product;
import com.kairos.catalog.repository.ProductRepository;
import com.kairos.catalog.service.OpenSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SearchProductsHandler {

    private final OpenSearchService openSearchService;
    private final ProductRepository productRepository;

    public List<SearchProductsResponse> handle(String query, int page, int size) {

        List<UUID> ids = openSearchService.search(query, page, size);

        return ids.stream()
                .map(productRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(this::map)
                .toList();
    }

    private SearchProductsResponse map(Product product) {
        return SearchProductsResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrls(product.getImageUrls())
                .build();
    }
}
