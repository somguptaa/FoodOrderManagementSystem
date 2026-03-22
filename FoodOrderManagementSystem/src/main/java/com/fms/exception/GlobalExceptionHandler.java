package com.fms.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fms.dto.ErrorResponse.FieldValidationError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 1. Bean Validation failures (@Valid) ───────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<FieldValidationError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new FieldValidationError(
                        fe.getField(),
                        fe.getRejectedValue(),
                        fe.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "One or more fields have invalid values",
                request.getRequestURI(),
                fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ── 2. Order not found ─────────────────────────────────────────────────
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(
            OrderNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Order Not Found",
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ── 3. Malformed JSON body ─────────────────────────────────────────────
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON",
                "Request body is missing or cannot be parsed",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ── 4. Catch-all for unexpected errors ─────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherErrors(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
    
    
    
 // Add these two methods inside your existing GlobalExceptionHandler class

 // Invalid status transition (e.g. DELIVERED → PENDING)
 @ExceptionHandler(InvalidStatusTransitionException.class)
 public ResponseEntity<ErrorResponse> handleInvalidTransition(
         InvalidStatusTransitionException ex,
         HttpServletRequest request) {

     ErrorResponse body = new ErrorResponse(
             HttpStatus.CONFLICT.value(),
             "Invalid Status Transition",
             ex.getMessage(),
             request.getRequestURI());

     return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
 }

 // Bad enum value in request body (e.g. "status": "SHIPPED")
 @ExceptionHandler(HttpMessageNotReadableException.class)
 public ResponseEntity<ErrorResponse> handleUnreadableMessage(
         HttpMessageNotReadableException ex,
         HttpServletRequest request) {

     String message = ex.getMessage() != null && ex.getMessage().contains("OrderStatus")
             ? "Invalid status value. Allowed: PENDING, CONFIRMED, PREPARING, DELIVERED, CANCELLED"
             : "Request body is missing or cannot be parsed";

     ErrorResponse body = new ErrorResponse(
             HttpStatus.BAD_REQUEST.value(),
             "Malformed Request",
             message,
             request.getRequestURI());

     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
 }
}