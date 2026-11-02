package com.beedigital.beestore.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void customOpenAPI_exposesExpectedMetadata() {
        OpenAPI openAPI = new SwaggerConfig().customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("BeeStore API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertTrue(openAPI.getInfo().getDescription().contains("gestion des produits"));
        assertNotNull(openAPI.getInfo().getLicense());
        assertEquals("MIT", openAPI.getInfo().getLicense().getName());
    }
}