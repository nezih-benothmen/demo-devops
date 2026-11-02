package com.beedigital.beestore.controller;

import com.beedigital.beestore.dto.ProduitRequestDTO;
import com.beedigital.beestore.dto.ProduitResponseDTO;
import com.beedigital.beestore.exception.GlobalExceptionHandler;
import com.beedigital.beestore.service.ProduitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Plain unit test (no Spring context, no database): MockMvc in standalone mode,
 * service mocked. Runs with `mvn test`, so JaCoCo picks it up.
 */
@ExtendWith(MockitoExtension.class)
class ProduitControllerTest {

    private static final String BASE = "/api/v1/produits";
    private static final String VALID_JSON = """
            { "nom": "Miel", "prix": 12.5, "stock": 10, "categorie": "Alimentaire" }""";

    @Mock
    private ProduitService produitService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProduitController(produitService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ProduitResponseDTO dto(Long id, String nom) {
        return ProduitResponseDTO.builder()
                .id(id).nom(nom).prix(12.5).stock(10)
                .categorie("Alimentaire").actif(true)
                .build();
    }

    // ---------- POST ----------

    @Test
    void creerProduit_valid_returns201() throws Exception {
        when(produitService.creerProduit(any(ProduitRequestDTO.class))).thenReturn(dto(1L, "Miel"));

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Miel"));
    }

    @Test
    void creerProduit_invalid_returns400AndNeverCallsService() throws Exception {
        String invalid = """
                { "nom": "", "prix": 10.0, "stock": 1 }""";

        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.nom").exists());

        verify(produitService, never()).creerProduit(any());
    }

    // ---------- GET all / by id ----------

    @Test
    void getTousProduits_returns200() throws Exception {
        when(produitService.getTousProduits()).thenReturn(List.of(dto(1L, "A"), dto(2L, "B")));

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("A"));
    }

    @Test
    void getProduitById_returns200() throws Exception {
        when(produitService.getProduitById(5L)).thenReturn(dto(5L, "Miel"));

        mockMvc.perform(get(BASE + "/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void getProduitById_serviceFailure_returns500ViaAdvice() throws Exception {
        when(produitService.getProduitById(9L)).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get(BASE + "/{id}", 9))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    // ---------- PUT ----------

    @Test
    void modifierProduit_valid_returns200() throws Exception {
        when(produitService.modifierProduit(eq(3L), any(ProduitRequestDTO.class)))
                .thenReturn(dto(3L, "Miel modifié"));

        mockMvc.perform(put(BASE + "/{id}", 3).contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Miel modifié"));
    }

    @Test
    void modifierProduit_invalid_returns400() throws Exception {
        String invalid = """
                { "nom": "Miel", "prix": -1, "stock": 1 }""";

        mockMvc.perform(put(BASE + "/{id}", 3).contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.prix").exists());

        verify(produitService, never()).modifierProduit(any(), any());
    }

    // ---------- DELETE ----------

    @Test
    void supprimerProduit_returns204() throws Exception {
        mockMvc.perform(delete(BASE + "/{id}", 4))
                .andExpect(status().isNoContent());

        verify(produitService).supprimerProduit(4L);
    }

    // ---------- search (3 branches) ----------

    @Test
    void rechercher_byCategorie() throws Exception {
        when(produitService.rechercherParCategorie("Bio")).thenReturn(List.of(dto(1L, "A")));

        mockMvc.perform(get(BASE + "/search").param("categorie", "Bio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(produitService, never()).rechercherParNom(any());
    }

    @Test
    void rechercher_byNom() throws Exception {
        when(produitService.rechercherParNom("Mi")).thenReturn(List.of(dto(1L, "Miel")));

        mockMvc.perform(get(BASE + "/search").param("nom", "Mi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Miel"));

        verify(produitService, never()).rechercherParCategorie(any());
    }

    @Test
    void rechercher_categorieHasPriorityOverNom() throws Exception {
        when(produitService.rechercherParCategorie("Bio")).thenReturn(List.of());

        mockMvc.perform(get(BASE + "/search").param("categorie", "Bio").param("nom", "Mi"))
                .andExpect(status().isOk());

        verify(produitService).rechercherParCategorie("Bio");
        verify(produitService, never()).rechercherParNom(any());
    }

    @Test
    void rechercher_withoutParams_returnsAll() throws Exception {
        when(produitService.getTousProduits()).thenReturn(List.of(dto(1L, "A"), dto(2L, "B")));

        mockMvc.perform(get(BASE + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ---------- rupture de stock ----------

    @Test
    void getProduitsEnRuptureDeStock_returns200() throws Exception {
        when(produitService.getProduitsEnRuptureDeStock()).thenReturn(List.of(dto(7L, "Vide")));

        mockMvc.perform(get(BASE + "/rupture-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7));
    }
}