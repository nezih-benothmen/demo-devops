package com.beedigital.beestore.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProduitTest {

    private static final LocalDateTime DATE = LocalDateTime.of(2026, 1, 15, 10, 30);

    private Produit build() {
        return Produit.builder()
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

    // ---------- @PrePersist ----------

    @Test
    void onCreate_setsDateCreation() {
        Produit p = new Produit();
        assertNull(p.getDateCreation());

        p.onCreate();

        assertNotNull(p.getDateCreation());
    }

    @Test
    void onCreate_defaultsActifToTrueWhenNull() {
        Produit p = new Produit();
        p.setActif(null);

        p.onCreate();

        assertTrue(p.getActif());
    }

    @Test
    void onCreate_keepsActifFalse() {
        Produit p = new Produit();
        p.setActif(false);

        p.onCreate();

        assertFalse(p.getActif());
    }

    @Test
    void onCreate_builderWithoutActifEndsUpTrue() {
        // @Builder ignores the "= true" initializer, so actif is null here
        Produit p = Produit.builder().nom("X").build();

        p.onCreate();

        assertTrue(p.getActif());
    }

    // ---------- constructors, getters, setters ----------

    @Test
    void noArgsConstructor_hasActifTrueByDefault() {
        Produit p = new Produit();

        assertNull(p.getId());
        assertTrue(p.getActif());
    }

    @Test
    void allArgsConstructor_setsEveryField() {
        Produit p = new Produit(1L, "Miel", "desc", 12.5, 10, "Alimentaire", true, DATE);

        assertEquals(1L, p.getId());
        assertEquals("Miel", p.getNom());
        assertEquals("desc", p.getDescription());
        assertEquals(12.5, p.getPrix());
        assertEquals(10, p.getStock());
        assertEquals("Alimentaire", p.getCategorie());
        assertTrue(p.getActif());
        assertEquals(DATE, p.getDateCreation());
    }

    @Test
    void settersAndGetters() {
        Produit p = new Produit();

        p.setId(2L);
        p.setNom("Huile");
        p.setDescription("Huile d'olive");
        p.setPrix(30.0);
        p.setStock(5);
        p.setCategorie("Bio");
        p.setActif(false);
        p.setDateCreation(DATE);

        assertEquals(2L, p.getId());
        assertEquals("Huile", p.getNom());
        assertEquals("Huile d'olive", p.getDescription());
        assertEquals(30.0, p.getPrix());
        assertEquals(5, p.getStock());
        assertEquals("Bio", p.getCategorie());
        assertFalse(p.getActif());
        assertEquals(DATE, p.getDateCreation());
    }

    @Test
    void builder_setsEveryField() {
        Produit p = build();

        assertEquals(1L, p.getId());
        assertEquals("Miel", p.getNom());
        assertEquals(12.5, p.getPrix());
        assertEquals(DATE, p.getDateCreation());
    }

    @Test
    void builder_toStringIsNotEmpty() {
        assertNotNull(Produit.builder().toString());
    }

    // ---------- equals / hashCode / canEqual / toString ----------

    @Test
    void equals_sameInstanceAndEqualCopy() {
        Produit a = build();
        Produit b = build();

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_nullAndOtherType() {
        Produit a = build();

        assertNotEquals(null, a);
        assertNotEquals("not a produit", a);
    }

    @Test
    void equals_differsOnEachField() {
        Produit base = build();

        Produit p = build(); p.setId(99L);
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
        Produit base = build();

        Produit p = build(); p.setId(null);
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
        Produit a = new Produit(null, null, null, null, null, null, null, null);
        Produit b = new Produit(null, null, null, null, null, null, null, null);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_returnsFalseWhenOtherRefusesCanEqual() {
        // Covers Lombok's "if (!other.canEqual(this)) return false;" branch,
        // which is only reachable with a subclass overriding canEqual.
        Produit a = build();
        Produit subclass = new Produit() {
            @Override
            public boolean canEqual(Object other) {
                return false;
            }
        };

        assertNotEquals(a, subclass);
    }

    @Test
    void canEqual() {
        Produit a = build();

        assertTrue(a.canEqual(build()));
        assertFalse(a.canEqual("not a produit"));
    }

    @Test
    void hashCode_differsWhenStateDiffers() {
        Produit a = build();
        Produit b = build();
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