package com.fms.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // hides null fields from JSON output
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private List<FieldValidationError> fieldErrors; // only present on validation failures

    // Constructor for general errors
    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for validation errors (includes field-level details)
    public ErrorResponse(int status, String error, String message, String path,
                         List<FieldValidationError> fieldErrors) {
        this(status, error, message, path);
        this.fieldErrors = fieldErrors;
    }

    // Getters
    public int getStatus()                          { return status; }
    public String getError()                        { return error; }
    public String getMessage()                      { return message; }
    public String getPath()                         { return path; }
    public LocalDateTime getTimestamp()             { return timestamp; }
    public List<FieldValidationError> getFieldErrors() { return fieldErrors; }

    // ── Inner DTO for per-field validation errors ──────────────────────────
    public static class FieldValidationError {
        private String field;
        private Object rejectedValue;
        private String message;

        public FieldValidationError(String field, Object rejectedValue, String message) {
            this.field         = field;
            this.rejectedValue = rejectedValue;
            this.message       = message;
        }

        public String getField()         { return field; }
        public Object getRejectedValue() { return rejectedValue; }
        public String getMessage()       { return message; }
    }
}