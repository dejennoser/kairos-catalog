package com.kairos.catalog.exception;

import com.kairos.catalog.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> hadleProuctNptFound(
            ProductNotFoundException ex, Locale locale) {
        String message = messageSource.getMessage(
                "error.product.not.found",
                new Object[]{ex.getId()},
                locale
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()


        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, Locale locale) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> messageSource.getMessage(
                        error.getDefaultMessage(),
                        null,
                        error.getDefaultMessage(),
                        locale
                ))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(messageSource.getMessage(
                                "error.validation.name.required", null, locale))
                        .timestamp(LocalDateTime.now())
                        .errors(errors)
                        .build()
        );
    }
     /*
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, Locale locale) {
        if (ex.getMessage() != null && ex.getMessage().contains("swagger")){
            throw new RuntimeException(ex);
        }

        String message = messageSource.getMessage(
                "error.internal", null, locale);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

    }*/
}


