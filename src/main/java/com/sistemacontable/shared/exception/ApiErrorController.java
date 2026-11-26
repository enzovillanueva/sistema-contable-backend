package com.sistemacontable.shared.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Gestiona los despachos de errores de servlet que no llegan al *controller advice*. */
@Slf4j
@RestController
public class ApiErrorController implements ErrorController {

    @RequestMapping("${server.error.path:/error}")
    public ProblemDetail error(HttpServletRequest request) {
        Object statusAttribute = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = statusAttribute instanceof Integer value ? HttpStatus.resolve(value) : null;
        if (status == null || !status.isError()) status = HttpStatus.INTERNAL_SERVER_ERROR;
        Object originalPath = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String path = originalPath instanceof String value ? value : request.getRequestURI();
        if (status.is5xxServerError()
                && request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) instanceof Throwable exception) {
            log.error("Error procesando {}", path, exception);
        }
        return ApiErrors.create(status, ApiErrors.code(status), ApiErrors.detail(status), path);
    }
}
