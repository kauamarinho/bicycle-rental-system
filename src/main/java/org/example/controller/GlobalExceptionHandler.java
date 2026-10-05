package org.example.controller;

import org.example.controller.dto.ApiDtos.ErrorResponse;
import org.example.domain.exception.InvalidCpfException;
import org.example.domain.exception.InvalidEmailException;
import org.example.domain.exception.NotFoundException;
import org.example.domain.exception.RentalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(RentalException.class)
    public ResponseEntity<ErrorResponse> handleRental(RentalException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({InvalidCpfException.class, InvalidEmailException.class})
    public ResponseEntity<ErrorResponse> handleInvalidInput(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
}
