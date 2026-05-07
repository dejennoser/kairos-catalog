package com.kairos.catalog.controller;


import com.kairos.catalog.dto.ProductRequest;
import com.kairos.catalog.dto.ProductResponse;
import com.kairos.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
@Tag(name = "products", description = "Product catalog management API")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductResponse>> findAll() {
        return ResponseEntity.ok(productService.findAll());

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ProductResponse> findById(@PathVariable @NonNull UUID id){
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get a product by category")
    public ResponseEntity<List<ProductResponse>> findByCategory(@PathVariable @NonNull String category){
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    @GetMapping("/search")
    @Operation(summary = "Get a product by name")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam @NonNull String name){
        return ResponseEntity.ok(productService.searchByName(name));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }


    @PostMapping("/{id}/image")
    @Operation(summary = "Upload product image")
    public ResponseEntity<ProductResponse> uploadImage(
            @PathVariable @NonNull UUID id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productService.uploadImage(id, file));
    }



    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")

    public ResponseEntity<ProductResponse> update(
            @PathVariable @NonNull UUID id,
            @RequestBody @Valid ProductRequest request) {
        return  ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing product")
    public ResponseEntity<Void> delete(@PathVariable @NonNull UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
