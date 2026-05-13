package com.kairos.catalog.product.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.catalog.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v2/products")
@RequiredArgsConstructor
public class CreateProductController {

    private final CreateProductHandler handler;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public CreateProductResponse create(
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws Exception {

        CreateProductRequest request =
                objectMapper.readValue(productJson, CreateProductRequest.class);

        Product product = handler.handle(request, images);

        return CreateProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrls(product.getImageUrls())
                .build();
    }
}