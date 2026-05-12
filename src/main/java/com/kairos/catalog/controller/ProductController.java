
package com.kairos.catalog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
@Tag(name = "products", description = "Product catalog management API")
public class ProductController {

    private final ProductService productService;

    private final ObjectMapper objectMapper;


    // GET ALL
    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductResponse>> findAll(
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale) {

        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.findAll(locale));
    }

    // GET BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ProductResponse> findById(
            @PathVariable @NonNull UUID id,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale) {

        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.findById(id, locale));
    }

    // GET BY CATEGORY
    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<List<ProductResponse>> findByCategory(
            @PathVariable @NonNull String category,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale) {

        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.findByCategory(category, locale));
    }

    // SEARCH
    @GetMapping("/search")
    @Operation(summary = "Search products by name")
    public ResponseEntity<List<ProductResponse>> search(
            @RequestParam @NonNull String name,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale) {

        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.searchByName(name, locale));
    }

    // FUZZY SEARCH
    @GetMapping("/fuzzy-search")
    @Operation(summary = "Fuzzy search products")
    public ResponseEntity<List<ProductResponse>> fuzzySearch(
            @RequestParam @NonNull String query,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale) {

        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.fuzzySearch(query, locale));
    }

    // CREATE
    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponse> create(
            @RequestBody @Valid ProductRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale) {

        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(request, locale));
    }

    // UPLOAD IMAGE
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload product image")
    public ResponseEntity<ProductResponse> uploadImage(
            @PathVariable @NonNull UUID id,
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale) {

        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.uploadImage(id, file, locale));
    }

    @PostMapping(
            value = "/with-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProductResponse createWithImages(
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader(name = "Accept-Language", defaultValue = "en") String locale
    ) throws Exception {

        ProductRequest product =
                objectMapper.readValue(productJson, ProductRequest.class);

        return productService.createWithImages(product, images, locale);
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ProductResponse> update(
            @PathVariable @NonNull UUID id,
            @RequestBody @Valid ProductRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String headerLocale,
            Locale systemLocale) {

        String locale = resolveLocale(headerLocale, systemLocale);
        return ResponseEntity.ok(productService.update(id, request, locale));
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing product")
    public ResponseEntity<Void> delete(@PathVariable @NonNull UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // HELPER (BOTTOM)
    private String resolveLocale(String headerLocale, Locale systemLocale) {
        String locale = (headerLocale != null && !headerLocale.isBlank())
                ? headerLocale
                : systemLocale.getLanguage();

        locale = locale.split("-")[0].toLowerCase();

        // whitelist validation
        if (!List.of("en", "de", "fr", "it").contains(locale)) {
            locale = "en";
        }

        return locale;
    }
}

