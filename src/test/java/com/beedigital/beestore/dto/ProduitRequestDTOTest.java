package com.beedigital.beestore.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProduitRequestDTOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        factory.close();
    }

    private ProduitRequestDTO valid() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Miel");
        dto.setDescription("Miel de montagne");
        dto.setPrix(12.5);
        dto.setStock(10);
        dto.setCategorie("Alimentaire");
        dto.setActif(true);
        return dto;
    }

    private Set<String> violatedFields(ProduitRequestDTO dto) {
        return validator.validate(dto).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    // ---------- getters / setters ----------

    @Test
    void defaultActifIsTrue() {
        assertTrue(new ProduitRequestDTO().getActif());
    }

    @Test
    void settersAndGetters() {
        ProduitRequestDTO dto = valid();
        dto.setActif(false);

        assertEquals("Miel", dto.getNom());
        assertEquals("Miel de montagne", dto.getDescription());
        assertEquals(12.5, dto.getPrix());
        assertEquals(10, dto.getStock());
        assertEquals("Alimentaire", dto.getCategorie());
        assertFalse(dto.getActif());
    }

    // ---------- validation ----------

    @Test
    void validDto_hasNoViolations() {
        assertTrue(validator.validate(valid()).isEmpty());
    }

    @Test
    void nom_blankOrNull_isRejected() {
        ProduitRequestDTO dto = valid();
        dto.setNom("  ");
        assertTrue(violatedFields(dto).contains("nom"));

        dto.setNom(null);
        assertTrue(violatedFields(dto).contains("nom"));
    }

    @Test
    void nom_sizeBounds() {
        ProduitRequestDTO dto = valid();

        dto.setNom("A");
        assertTrue(violatedFields(dto).contains("nom"));

        dto.setNom("A".repeat(101));
        assertTrue(violatedFields(dto).contains("nom"));

        dto.setNom("AB");
        assertFalse(violatedFields(dto).contains("nom"));

        dto.setNom("A".repeat(100));
        assertFalse(violatedFields(dto).contains("nom"));
    }

    @Test
    void description_tooLong_isRejected() {
        ProduitRequestDTO dto = valid();

        dto.setDescription("a".repeat(501));
        assertTrue(violatedFields(dto).contains("description"));

        dto.setDescription("a".repeat(500));
        assertFalse(violatedFields(dto).contains("description"));

        dto.setDescription(null);
        assertFalse(violatedFields(dto).contains("description"));
    }

    @Test
    void prix_nullZeroNegative_isRejected() {
        ProduitRequestDTO dto = valid();

        dto.setPrix(null);
        assertTrue(violatedFields(dto).contains("prix"));

        dto.setPrix(0.0);
        assertTrue(violatedFields(dto).contains("prix"));

        dto.setPrix(-1.0);
        assertTrue(violatedFields(dto).contains("prix"));

        dto.setPrix(0.01);
        assertFalse(violatedFields(dto).contains("prix"));
    }

    @Test
    void stock_nullOrNegative_isRejected_zeroAllowed() {
        ProduitRequestDTO dto = valid();

        dto.setStock(null);
        assertTrue(violatedFields(dto).contains("stock"));

        dto.setStock(-1);
        assertTrue(violatedFields(dto).contains("stock"));

        dto.setStock(0);
        assertFalse(violatedFields(dto).contains("stock"));
    }

    @Test
    void categorie_tooLong_isRejected() {
        ProduitRequestDTO dto = valid();

        dto.setCategorie("c".repeat(101));
        assertTrue(violatedFields(dto).contains("categorie"));

        dto.setCategorie("c".repeat(100));
        assertFalse(violatedFields(dto).contains("categorie"));
    }

    @Test
    void validationMessages_areTheExpectedOnes() {
        ProduitRequestDTO dto = valid();
        dto.setPrix(-5.0);

        Set<ConstraintViolation<ProduitRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("Le prix doit être strictement positif",
                violations.iterator().next().getMessage());
    }

    // ---------- equals / hashCode / canEqual / toString ----------

    @Test
    void equals_sameInstanceAndEqualCopy() {
        ProduitRequestDTO a = valid();
        ProduitRequestDTO b = valid();

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_nullAndOtherType() {
        ProduitRequestDTO a = valid();

        assertNotEquals(null, a);
        assertNotEquals("not a dto", a);
    }

    @Test
    void equals_differsOnEachField() {
        ProduitRequestDTO base = valid();

        ProduitRequestDTO p = valid(); p.setNom("Autre");
        assertNotEquals(base, p);

        p = valid(); p.setDescription("Autre");
        assertNotEquals(base, p);

        p = valid(); p.setPrix(99.0);
        assertNotEquals(base, p);

        p = valid(); p.setStock(99);
        assertNotEquals(base, p);

        p = valid(); p.setCategorie("Autre");
        assertNotEquals(base, p);

        p = valid(); p.setActif(false);
        assertNotEquals(base, p);
    }

    @Test
    void equals_nullFieldsOnEitherSide() {
        ProduitRequestDTO base = valid();

        ProduitRequestDTO p = valid(); p.setNom(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = valid(); p.setDescription(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = valid(); p.setPrix(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = valid(); p.setStock(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = valid(); p.setCategorie(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);

        p = valid(); p.setActif(null);
        assertNotEquals(base, p);
        assertNotEquals(p, base);
    }

    @Test
    void equals_allFieldsNullOnBothSides() {
        ProduitRequestDTO a = new ProduitRequestDTO();
        ProduitRequestDTO b = new ProduitRequestDTO();
        a.setActif(null);
        b.setActif(null);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void canEqual() {
        ProduitRequestDTO a = valid();

        assertTrue(a.canEqual(valid()));
        assertFalse(a.canEqual("not a dto"));
    }

    @Test
    void hashCode_differsWhenStateDiffers() {
        ProduitRequestDTO a = valid();
        ProduitRequestDTO b = valid();
        b.setNom("Autre");

        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_containsFieldValues() {
        String s = valid().toString();

        assertTrue(s.contains("Miel"));
        assertTrue(s.contains("Alimentaire"));
        assertTrue(s.contains("12.5"));
    }
}