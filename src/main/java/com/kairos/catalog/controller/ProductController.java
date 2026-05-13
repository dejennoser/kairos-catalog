package com.kairos.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.catalog.dto.ProductRequest;
import com.kairos.catalog.dto.ProductResponse;
import com.kairos.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "products", description = "Product catalog management API")
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    // =========================
    // READ
    // =========================

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductResponse>> findAll(
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale
    ) {
        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.findAll(locale));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ProductResponse> findById(
            @PathVariable @NonNull UUID id,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale
    ) {
        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.findById(id, locale));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<List<ProductResponse>> findByCategory(
            @PathVariable @NonNull String category,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale
    ) {
        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.findByCategory(category, locale));
    }

    // =========================
    // SEARCH (ONLY ONE!)
    // =========================

    @GetMapping("/search")
    @Operation(summary = "Search products (OpenSearch, prefix-based, paginated)")
    public ResponseEntity<List<ProductResponse>> search(
            @RequestParam @NonNull String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale
    ) {
        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(
                productService.search(query, page, size, locale)
        );
    }

    // =========================
    // CREATE
    // =========================

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponse> create(
            @RequestBody @Valid ProductRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale
    ) {
        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(request, locale));
    }

    @PostMapping(
            value = "/with-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Create product with images")
    public ProductResponse createWithImages(
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader(name = "Accept-Language", defaultValue = "en") String locale
    ) throws Exception {

        ProductRequest product =
                objectMapper.readValue(productJson, ProductRequest.class);

        return productService.createWithImages(product, images, locale);
    }

    // =========================
    // UPDATE / DELETE
    // =========================

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ProductResponse> update(
            @PathVariable @NonNull UUID id,
            @RequestBody @Valid ProductRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale
    ) {
        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.update(id, request, locale));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing product")
    public ResponseEntity<Void> delete(@PathVariable @NonNull UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // HELPER
    // =========================

    private String resolveLocale(String headerLocale, Locale systemLocale) {
        String locale = (headerLocale != null && !headerLocale.isBlank())
                ? headerLocale
                : systemLocale.getLanguage();

        locale = locale.split("-")[0].toLowerCase();

        if (!List.of("en", "de", "fr", "it").contains(locale)) {
            locale = "en";
        }

        return locale;
    }
}