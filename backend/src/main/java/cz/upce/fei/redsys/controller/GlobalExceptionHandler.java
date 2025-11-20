package cz.upce.fei.redsys.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ValidationErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> messageSource.getMessage(fe, LocaleContextHolder.getLocale()))
                .toList();

        log.warn("Validation failed for request {}: {}", path, ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", path, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(cv -> {
                    String msg = cv.getMessage();
                    return (msg != null && !msg.isBlank()) ? msg : "Validation failed";
                })
                .toList();

        log.warn("Constraint violation for request {}: {}", path, ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", path, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();

            if (targetType != null && targetType.isEnum()) {

                String fieldName = "field";
                if (ife.getPath() != null && !ife.getPath().isEmpty()) {
                    fieldName = ife.getPath().get(ife.getPath().size() - 1).getFieldName();
                }

                String options = String.join(", ", getEnumNames(targetType));

                String enumErrorMessage = messageSource.getMessage(
                        "common.enum",
                        new Object[]{fieldName, options},
                        LocaleContextHolder.getLocale()
                );

                return buildResponse(HttpStatus.BAD_REQUEST, enumErrorMessage, request.getRequestURI());
            }
        }

        log.warn("Malformed JSON or invalid type at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request or invalid data type.", request.getRequestURI());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Authentication failed for request {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("Entity not found at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        String supportedMethods = (ex.getSupportedHttpMethods() != null)
                ? ex.getSupportedHttpMethods().toString()
                : "N/A";

        String message = String.format("Method %s is not supported. Supported methods: %s",
                ex.getMethod(), supportedMethods);

        log.warn("Method not supported at {}: {}", request.getRequestURI(), message);
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, message, request.getRequestURI());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpServletRequest request) {

        String supportedTypes = ex.getSupportedMediaTypes().isEmpty()
                ? "N/A"
                : ex.getSupportedMediaTypes().toString();

        String message = String.format("Requested media type is not acceptable. Supported response types: %s",
                supportedTypes);

        log.warn("Media type not acceptable at {}: {}", request.getRequestURI(), message);
        return buildResponse(HttpStatus.NOT_ACCEPTABLE, message, request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Illegal state at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
            OptimisticLockingFailureException ex,
            HttpServletRequest request) {

        log.warn("Optimistic locking failure at {}: {}", request.getRequestURI(), ex.getMessage());

        return buildResponse(
                HttpStatus.CONFLICT,
                "The resource has been updated by another user. Please refresh and try again.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        String supportedTypes = ex.getSupportedMediaTypes().isEmpty()
                ? "N/A"
                : ex.getSupportedMediaTypes().toString();

        String message = String.format("Content type %s is not supported. Supported types: %s",
                ex.getContentType(), supportedTypes);

        log.warn("Media type not supported at {}: {}", request.getRequestURI(), message);
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message, request.getRequestURI());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleWorkflow(ValidationException ex, HttpServletRequest request) {
        log.warn("Workflow validation failed at {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(path)
                        .build()
        );
    }

    private ResponseEntity<ValidationErrorResponse> buildResponse(HttpStatus status, String message, String path, List<String> errors) {
        return ResponseEntity.status(status).body(
                ValidationErrorResponse.builder()
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(path)
                        .validationErrors(errors)
                        .build()
        );
    }

    private String[] getEnumNames(Class<?> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum.class::cast)
                .map(Enum::name)
                .toArray(String[]::new);
    }
}