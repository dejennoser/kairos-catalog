package com.kairos.catalog.product.list;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/products")
@RequiredArgsConstructor
public class ListProductsController {

    private final ListProductsHandler handler;

@GetMapping
    public List<ListProductResponse> list() {

    return handler.handle();

 }

}
