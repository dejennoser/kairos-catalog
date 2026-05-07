package com.kairos.catalog.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<String> errors;

}
