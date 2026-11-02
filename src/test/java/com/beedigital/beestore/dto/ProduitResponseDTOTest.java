package com.beedigital.beestore.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProduitResponseDTOTest {

    private static final LocalDateTime DATE = LocalDateTime.of(2026, 1, 15, 10, 30);

    private ProduitResponseDTO build() {
        return ProduitResponseDTO.builder()
                .id(1L)
                .nom("Miel")
                .description("Miel de montagne")
                .prix(12.5)
                .stock(10)
                .categorie("Alimentaire")
                .actif(true)
                .dateCreation(DATE)
                .build();
    }

    // ---------- constructors, builder, getters, setters ----------

    @Test
    void noArgsConstructor_leavesFieldsNull() {
        ProduitResponseDTO dto = new ProduitResponseDTO();

        assertNull(dto.getId());
        assertNull(dto.getNom());
        assertNull(dto.getDescription());
        assertNull(dto.getPrix());
        assertNull(dto.getStock());
        assertNull(dto.getCategorie());
        assertNull(dto.getActif());
        assertNull(dto.getDateCreation());
    }

    @Test
    void allArgsConstructor_setsEveryField() {
        ProduitResponseDTO dto =
                new ProduitResponseDTO(1L, "Miel", "desc", 12.5, 10, "Alimentaire", true, DATE);

        assertEquals(1L, dto.getId());
        assertEquals("Miel", dto.getNom());
        assertEquals("desc", dto.getDescription());
        assertEquals(12.5, dto.getPrix());
        assertEquals(10, dto.getStock());
        assertEquals("Alimentaire", dto.getCategorie());
        assertTrue(dto.getActif());
        assertEquals(DATE, dto.getDateCreation());
    }

    @Test
    void builder_setsEveryField() {
        ProduitResponseDTO dto = build();

        assertEquals(1L, dto.getId());
        assertEquals("Miel", dto.getNom());
        assertEquals("Miel de montagne", dto.getDescription());
        assertEquals(12.5, dto.getPrix());
        assertEquals(10, dto.getStock());
        assertEquals("Alimentaire", dto.getCategorie());
        assertTrue(dto.getActif());
        assertEquals(DATE, dto.getDateCreation());
    }

    @Test
    void builder_toStringIsNotEmpty() {
        assertNotNull(ProduitResponseDTO.builder().toString());
    }

    @Test
    void settersAndGetters() {
        ProduitResponseDTO dto = new ProduitResponseDTO();

        dto.setId(2L);
        dto.setNom("Huile");
        dto.setDescription("Huile d'olive");
        dto.setPrix(30.0);
        dto.setStock(5);
        dto.setCategorie("Bio");
        dto.setActif(false);
        dto.setDateCreation(DATE);

        assertEquals(2L, dto.getId());
        assertEquals("Huile", dto.getNom());
        assertEquals("Huile d'olive", dto.getDescription());
        assertEquals(30.0, dto.getPrix());
        assertEquals(5, dto.getStock());
        assertEquals("Bio", dto.getCategorie());
        assertFalse(dto.getActif());
        assertEquals(DATE, dto.getDateCreation());
    }

    // ---------- equals / hashCode / canEqual / toString ----------

    @Test
    void equals_sameInstanceAndEqualCopy() {
        ProduitResponseDTO a = build();
        ProduitResponseDTO b = build();

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_nullAndOtherType() {
        ProduitResponseDTO a = build();

        assertNotEquals(null, a);
        assertNotEquals("not a dto", a);
    }

    @Test
    void equals_differsOnEachField() {
        ProduitResponseDTO base = build();

        ProduitResponseDTO p = build(); p.setId(99L);
        assertNotEquals(base, p);

        p = build(); p.setNom("Autre");
        assertNotEquals(base, p);

        p = build(); p.setDescription("Autre");
        assertNotEquals(base, p);

        p = build(); p.setPrix(99.0);
        assertNotEquals(base, p);

        p = build(); p.setStock(99);
        assertNotEquals(base, p);

        p = build(); p.setCategorie("Autre");
        assertNotEquals(base, p);

        p = build(); p.setActif(false);
        assertNotEquals(base, p);

        p = build(); p.setDateCreation(DATE.plusDays(1));
        assertNotEquals(base, p);
    }

    @Test
    void equals_nullFieldsOnEitherSide() {
        ProduitResponseDTO base = build();

        ProduitResponseDTO p = build(); p.setId(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = build(); p.setNom(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = build(); p.setDescription(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = build(); p.setPrix(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = build(); p.setStock(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = build(); p.setCategorie(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = build(); p.setActif(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = build(); p.setDateCreation(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);
    }

    @Test
    void equals_allFieldsNullOnBothSides() {
        ProduitResponseDTO a = new ProduitResponseDTO();
        ProduitResponseDTO b = new ProduitResponseDTO();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void canEqual() {
        ProduitResponseDTO a = build();

        assertTrue(a.canEqual(build()));
        assertFalse(a.canEqual("not a dto"));
    }

    @Test
    void hashCode_differsWhenStateDiffers() {
        ProduitResponseDTO a = build();
        ProduitResponseDTO b = build();
        b.setNom("Autre");

        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_containsFieldValues() {
        String s = build().toString();

        assertTrue(s.contains("Miel"));
        assertTrue(s.contains("Alimentaire"));
        assertTrue(s.contains("12.5"));
    }
}