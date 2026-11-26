package com.sistemacontable.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String code = ApiErrors.code(status);
        String detail = ApiErrors.detail(status);
        Map<String, List<String>> errors = new LinkedHashMap<>();
        if (ex instanceof MethodArgumentNotValidException validation) {
            validation.getBindingResult().getFieldErrors().forEach(error ->
                    add(errors, error.getField(), error.getDefaultMessage()));
            validation.getBindingResult().getGlobalErrors().forEach(error ->
                    add(errors, "_global", error.getDefaultMessage()));
            code = "VALIDATION_ERROR";
            detail = "Hay campos inválidos.";
        } else if (ex instanceof HandlerMethodValidationException validation && !status.is5xxServerError()) {
            validation.getParameterValidationResults().forEach(result -> {
                String name = result.getMethodParameter().getParameterName();
                result.getResolvableErrors().forEach(error ->
                        add(errors, name == null ? "_global" : name, error.getDefaultMessage()));
            });
            code = "VALIDATION_ERROR";
            detail = "Hay parámetros inválidos.";
        } else if (ex instanceof ResponseStatusException response && !status.is5xxServerError()
                && response.getReason() != null) {
            detail = response.getReason();
        }
        if (status.is5xxServerError()) log.error("Error procesando {}", path, ex);
        return super.handleExceptionInternal(ex, ApiErrors.create(status, code, detail, path, errors),
                headers, status, request);
    }

    @ExceptionHandler(ApiException.class)
    public ProblemDetail applicationError(ApiException ex, HttpServletRequest request) {
        if (ex.getStatus().is5xxServerError()) log.error("Error procesando {}", request.getRequestURI(), ex);
        return ApiErrors.create(ex.getStatus(), ex.getCode().value(), ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail dataConflict(DataIntegrityViolationException ex, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail authentication(AuthenticationException ex, HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return problem(HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail validation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(error ->
                add(errors, error.getPropertyPath().toString(), error.getMessage()));
        return ApiErrors.create(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Hay campos inválidos.",
                request.getRequestURI(), errors);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado procesando {}", request.getRequestURI(), ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ProblemDetail problem(HttpStatus status, HttpServletRequest request) {
        return ApiErrors.create(status, ApiErrors.code(status), ApiErrors.detail(status), request.getRequestURI());
    }

    private static void add(Map<String, List<String>> errors, String field, String message) {
        errors.computeIfAbsent(field, key -> new ArrayList<>()).add(message == null ? "Valor inválido." : message);
    }
}
