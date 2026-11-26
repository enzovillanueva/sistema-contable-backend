package com.sistemacontable.shared.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public final class ApiErrors {
    private ApiErrors() {
    }

    public static ProblemDetail create(HttpStatusCode status, String code, String detail, String path) {
        return create(status, code, detail, path, Map.of());
    }

    public static ProblemDetail create(HttpStatusCode status, String code, String detail, String path,
                                       Map<String, List<String>> errors) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("about:blank"));
        problem.setInstance(URI.create(path));
        problem.setProperty("code", code);
        problem.setProperty("errors", errors);
        return problem;
    }

    public static String code(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "AUTHENTICATION_REQUIRED";
            case 403 -> "ACCESS_DENIED";
            case 404 -> "NOT_FOUND";
            case 405 -> "METHOD_NOT_ALLOWED";
            case 406 -> "NOT_ACCEPTABLE";
            case 409 -> "DATA_CONFLICT";
            case 415 -> "UNSUPPORTED_MEDIA_TYPE";
            default -> status.is5xxServerError() ? "INTERNAL_ERROR" : "HTTP_ERROR";
        };
    }

    public static String detail(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> "La solicitud no es válida.";
            case 401 -> "Autenticación requerida.";
            case 403 -> "No tenés permisos para acceder a este recurso.";
            case 404 -> "El recurso solicitado no existe.";
            case 405 -> "El método HTTP no está permitido para este recurso.";
            case 406 -> "El formato de respuesta solicitado no está disponible.";
            case 409 -> "Los datos entran en conflicto con un registro existente.";
            case 415 -> "El tipo de contenido enviado no está soportado.";
            default -> status.is5xxServerError()
                    ? "Ocurrió un error interno. Intentá nuevamente más tarde."
                    : "No se pudo procesar la solicitud.";
        };
    }

    /**
     * Serializa con el ObjectMapper de Spring, que incluye soporte para ProblemDetail.
     */
    public static void write(
            HttpServletResponse response,
            ObjectMapper mapper,
            ProblemDetail problem
    ) throws IOException {
        response.setStatus(problem.getStatus());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        mapper.writeValue(response.getWriter(), problem);
    }
}
