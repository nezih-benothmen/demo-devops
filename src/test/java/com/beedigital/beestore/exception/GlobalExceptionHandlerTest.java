package com.beedigital.beestore.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @SuppressWarnings("unchecked")
    void handleValidation_returns400WithFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(List.of(
                new FieldError("produit", "nom", "Le nom est obligatoire"),
                new FieldError("produit", "prix", "Le prix doit être positif")));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("Données invalides", body.message());
        assertNotNull(body.timestamp());
        Map<String, String> details = (Map<String, String>) body.details();
        assertEquals(2, details.size());
        assertEquals("Le nom est obligatoire", details.get("nom"));
        assertEquals("Le prix doit être positif", details.get("prix"));
    }

    @Test
    void handleValidation_withNoErrors_returnsEmptyDetails() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(List.of());
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody().details()).isEmpty());
    }

    @Test
    void handleNotFound_returns404WithExceptionMessage() {
        // Mocked so the test doesn't depend on ProduitNotFoundException's constructor
        ProduitNotFoundException ex = mock(ProduitNotFoundException.class);
        when(ex.getMessage()).thenReturn("Produit 42 introuvable");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("Produit 42 introuvable", body.message());
        assertNotNull(body.timestamp());
        assertNull(body.details());
    }

    @Test
    void handleGeneric_returns500WithGenericMessage() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleGeneric(new RuntimeException("détail interne sensible"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.status());
        assertEquals("Une erreur interne est survenue. Contactez l'administrateur.", body.message());
        assertFalse(body.message().contains("sensible"), "internal details must not leak to the client");
        assertNotNull(body.timestamp());
        assertNull(body.details());
    }
}