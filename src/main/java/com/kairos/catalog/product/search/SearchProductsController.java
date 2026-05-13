package com.kairos.catalog.product.search;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/products/search")
@RequiredArgsConstructor
public class SearchProductsController {

    private final SearchProductsHandler handler;

    @GetMapping
    public List<SearchProductsResponse> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return handler.handle(query, page, size);
    }
}
