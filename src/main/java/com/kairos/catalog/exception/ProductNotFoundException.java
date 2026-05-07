package com.kairos.catalog.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    private final UUID id;

    public ProductNotFoundException(UUID id){
        super(id.toString());
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

}
