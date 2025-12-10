package cz.upce.fei.redsys.controller;

import cz.upce.fei.redsys.dto.ErrorDto.ErrorResponse;
import cz.upce.fei.redsys.dto.ErrorDto.ValidationErrorResponse;
import cz.upce.fei.redsys.service.WorkflowService.WorkflowException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private static final String TEST_PATH = "/api/test";

    @Test
    void handleMethodArgumentNotValid_ShouldReturn400WithValidationErrors() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        when(messageSource.getMessage(any(FieldError.class), any(Locale.class)))
                .thenReturn("Field is required");

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("object", "field", "Field is required");
        when(ex.getBindingResult()).thenReturn(
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "object")
        );
        ex.getBindingResult().addError(fieldError);

        // Act
        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Validation failed", response.getBody().message());
        assertEquals(TEST_PATH, response.getBody().path());
        assertNotNull(response.getBody().validationErrors());
    }

    @Test
    void handleConstraintViolation_ShouldReturn400WithValidationErrors() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Value is invalid");

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        // Act
        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleConstraintViolation(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Validation failed", response.getBody().message());
        assertEquals(TEST_PATH, response.getBody().path());
        assertTrue(response.getBody().validationErrors().contains("Value is invalid"));
    }

    @Test
    void handleTypeMismatch_ShouldReturn400() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleBadCredentials_ShouldReturn401() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentials(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals("Unauthorized", response.getBody().error());
        assertEquals("Invalid username or password", response.getBody().message());
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleAccessDenied_ShouldReturn403() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDenied(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().status());
        assertEquals("Forbidden", response.getBody().error());
        assertEquals("Access denied", response.getBody().message());
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleNotFound_ShouldReturn404() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("Entity not found", response.getBody().message());
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleMethodNotSupported_ShouldReturn405() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodNotSupported(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(405, response.getBody().status());
        assertEquals("Method Not Allowed", response.getBody().error());
        assertTrue(response.getBody().message().contains("POST"));
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleIllegalState_ShouldReturn409() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        IllegalStateException ex = new IllegalStateException("Illegal state");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalState(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Conflict", response.getBody().error());
        assertEquals("Illegal state", response.getBody().message());
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleOptimisticLockingFailure_ShouldReturn409() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        OptimisticLockingFailureException ex = new OptimisticLockingFailureException("Locking failure");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOptimisticLockingFailure(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Conflict", response.getBody().error());
        assertTrue(response.getBody().message().contains("updated by another user"));
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleMediaTypeNotSupported_ShouldReturn415() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        HttpMediaTypeNotSupportedException ex = mock(HttpMediaTypeNotSupportedException.class);
        when(ex.getContentType()).thenReturn(org.springframework.http.MediaType.APPLICATION_XML);
        when(ex.getSupportedMediaTypes()).thenReturn(List.of(org.springframework.http.MediaType.APPLICATION_JSON));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMediaTypeNotSupported(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(415, response.getBody().status());
        assertEquals("Unsupported Media Type", response.getBody().error());
        assertTrue(response.getBody().message().contains("not supported"));
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleValidationException_ShouldReturn422() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        ValidationException ex = new ValidationException("Validation error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWorkflow(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().status());
        assertEquals("Unprocessable Entity", response.getBody().error());
        assertEquals("Validation error", response.getBody().message());
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleWorkflowException_ShouldReturn422() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        WorkflowException ex = new WorkflowException("Workflow error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWorkflowException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().status());
        assertEquals("Unprocessable Entity", response.getBody().error());
        assertEquals("Workflow error", response.getBody().message());
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void handleUnexpected_ShouldReturn500() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        Exception ex = new Exception("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnexpected(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals("Unexpected error occurred", response.getBody().message());
        assertEquals(TEST_PATH, response.getBody().path());
    }

    @Test
    void errorResponse_ShouldHaveCorrectStructure() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        EntityNotFoundException ex = new EntityNotFoundException("Test message");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(ex, request);

        // Assert
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.error());
        assertNotNull(body.message());
        assertNotNull(body.path());
    }

    @Test
    void validationErrorResponse_ShouldHaveCorrectStructure() {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Error message");

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        // Act
        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleConstraintViolation(ex, request);

        // Assert
        ValidationErrorResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.error());
        assertNotNull(body.message());
        assertNotNull(body.path());
        assertNotNull(body.validationErrors());
        assertFalse(body.validationErrors().isEmpty());
    }
}