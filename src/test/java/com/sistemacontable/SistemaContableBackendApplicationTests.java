package com.sistemacontable;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import tools.jackson.databind.ObjectMapper;
import com.sistemacontable.shared.exception.ApiErrors;
import org.springframework.mock.web.MockHttpServletResponse;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SistemaContableBackendApplicationTests {

    @Autowired
    private ObjectMapper mapper;

    @Test
    void problemDetailExtensionsAreSerializedAtRoot() throws Exception {
        var problem = ApiErrors.create(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Datos inválidos",
                "/api/users", Map.of("email", List.of("Email inválido")));
        var json = mapper.readTree(mapper.writeValueAsString(problem));
        assertEquals("BAD_REQUEST", json.get("code").asText());
        assertEquals("Email inválido", json.get("errors").get("email").get(0).asText());
        assertFalse(json.has("properties"));

        var response = new MockHttpServletResponse();
        ApiErrors.write(response, mapper, problem);
        assertEquals(json, mapper.readTree(response.getContentAsString()));
        assertEquals(400, response.getStatus());
        assertTrue(response.getContentType().startsWith("application/problem+json"));
    }

    @Test
    void problemDetailWithoutOptionalFieldsCanBeWritten() throws Exception {
        var response = new MockHttpServletResponse();
        ApiErrors.write(response, mapper, ProblemDetail.forStatus(HttpStatus.NOT_FOUND));
        var json = mapper.readTree(response.getContentAsString());
        assertEquals(404, json.get("status").asInt());
        assertFalse(json.has("instance"));
        assertFalse(json.has("properties"));
    }

    @Test
    void contextLoads() {
    }

}
