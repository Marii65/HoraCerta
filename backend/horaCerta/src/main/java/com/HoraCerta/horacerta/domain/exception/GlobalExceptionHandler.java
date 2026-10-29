package com.HoraCerta.horacerta.domain.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{
    public record ErrorResponse(
            LocalDateTime timestamp,
            int Status,
            String error,
            String message,
            String path,
            Map<String, String> validationErros
    ) {}

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErros
    ){
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErros
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ){
        return buildResponse(status, message, request, Map.of());
    }

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            DuplicateEquipmentCodeException.class,
            EquipmentPlanAlreadyActiveException.class,
            EquipmentHasHistoryException.class,
            MaintenancePlanHasHistoryException.class,
            MaintenancePlanItemHasHistoryException.class,
            DataIntegrityViolationException.class
    })

    public ResponseEntity<ErrorResponse> handleConflict(
            Exception ex,
            HttpServletRequest request
    ){
        String message = ex instanceof DataIntegrityViolationException
                ? "The operation conflicts with existing data."
                : ex.getMessage();

        return buildResponse(HttpStatus.CONFLICT, message, request);
    }

    @ExceptionHandler(InactiveMaintenancePlanException.class)
    public ResponseEntity<ErrorResponse> handleInactiveMaintenancePlan(
            InactiveMaintenancePlanException ex,
            HttpServletRequest request
    ){
        return buildResponse(
                HttpStatus.CONFLICT,ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ){
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }
        return buildResponse(HttpStatus.BAD_REQUEST,
                "One or more fields are invalid",
                request,
                errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.BAD_REQUEST,
                "The request body is invalid or contains an invalid value",
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT, ex.getMessage(), request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ){
        return buildResponse(HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action",
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred.",
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                request
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
