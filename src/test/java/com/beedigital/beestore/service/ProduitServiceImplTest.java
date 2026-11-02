package com.beedigital.beestore.service;

import com.beedigital.beestore.dto.ProduitRequestDTO;
import com.beedigital.beestore.dto.ProduitResponseDTO;
import com.beedigital.beestore.entity.Produit;
import com.beedigital.beestore.exception.ProduitNotFoundException;
import com.beedigital.beestore.repository.ProduitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitServiceImpl produitService;

    private ProduitRequestDTO request(Boolean actif) {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Laptop Pro");
        dto.setDescription("Ultraportable");
        dto.setPrix(1299.99);
        dto.setStock(10);
        dto.setCategorie("Informatique");
        dto.setActif(actif);
        return dto;
    }

    // =====================================================
    // creerProduit
    // =====================================================

    @Test
    @DisplayName("creerProduit() doit sauvegarder et retourner le produit créé")
    void creerProduit_doitSauvegarderEtRetournerLeProduit() {
        ProduitRequestDTO requestDTO = new ProduitRequestDTO();
        requestDTO.setNom("Laptop Pro");
        requestDTO.setPrix(1299.99);
        requestDTO.setStock(10);

        Produit produitSauvegarde = Produit.builder()
                .id(1L).nom("Laptop Pro").prix(1299.99).stock(10).actif(true)
                .build();
        when(produitRepository.save(any(Produit.class))).thenReturn(produitSauvegarde);

        ProduitResponseDTO resultat = produitService.creerProduit(requestDTO);

        assertThat(resultat.getId()).isEqualTo(1L);
        assertThat(resultat.getNom()).isEqualTo("Laptop Pro");
        verify(produitRepository, times(1)).save(any(Produit.class));
    }

    @Test
    @DisplayName("creerProduit() copie tous les champs vers l'entité et garde actif=false")
    void creerProduit_actifFalse_estConserve() {
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));

        produitService.creerProduit(request(false));

        ArgumentCaptor<Produit> captor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(captor.capture());
        Produit saved = captor.getValue();
        assertThat(saved.getNom()).isEqualTo("Laptop Pro");
        assertThat(saved.getDescription()).isEqualTo("Ultraportable");
        assertThat(saved.getPrix()).isEqualTo(1299.99);
        assertThat(saved.getStock()).isEqualTo(10);
        assertThat(saved.getCategorie()).isEqualTo("Informatique");
        assertThat(saved.getActif()).isFalse();
    }

    @Test
    @DisplayName("creerProduit() met actif=true quand le DTO ne le précise pas (null)")
    void creerProduit_actifNull_devientTrue() {
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));

        ProduitResponseDTO resultat = produitService.creerProduit(request(null));

        ArgumentCaptor<Produit> captor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(captor.capture());
        assertThat(captor.getValue().getActif()).isTrue();
        assertThat(resultat.getActif()).isTrue();
    }

    // =====================================================
    // lecture
    // =====================================================

    @Test
    @DisplayName("getProduitById() doit retourner le produit s'il existe")
    void getProduitById_produitExistant_doitRetournerLeProduit() {
        Produit produit = Produit.builder().id(1L).nom("Laptop Pro").build();
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        ProduitResponseDTO resultat = produitService.getProduitById(1L);

        assertThat(resultat.getNom()).isEqualTo("Laptop Pro");
    }

    @Test
    @DisplayName("getProduitById() mappe tous les champs de l'entité vers le DTO")
    void getProduitById_mappeTousLesChamps() {
        LocalDateTime date = LocalDateTime.of(2026, 1, 15, 10, 30);
        Produit produit = Produit.builder()
                .id(7L).nom("Miel").description("Montagne").prix(12.5).stock(3)
                .categorie("Alimentaire").actif(true).dateCreation(date)
                .build();
        when(produitRepository.findById(7L)).thenReturn(Optional.of(produit));

        ProduitResponseDTO resultat = produitService.getProduitById(7L);

        assertThat(resultat.getId()).isEqualTo(7L);
        assertThat(resultat.getNom()).isEqualTo("Miel");
        assertThat(resultat.getDescription()).isEqualTo("Montagne");
        assertThat(resultat.getPrix()).isEqualTo(12.5);
        assertThat(resultat.getStock()).isEqualTo(3);
        assertThat(resultat.getCategorie()).isEqualTo("Alimentaire");
        assertThat(resultat.getActif()).isTrue();
        assertThat(resultat.getDateCreation()).isEqualTo(date);
    }

    @Test
    @DisplayName("getProduitById() doit lever ProduitNotFoundException si absent")
    void getProduitById_produitAbsent_doitLeverException() {
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produitService.getProduitById(99L))
                .isInstanceOf(ProduitNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("getTousProduits() doit retourner la liste mappée en DTO")
    void getTousProduits_doitRetournerListeDeDTO() {
        when(produitRepository.findAll()).thenReturn(List.of(
                Produit.builder().id(1L).nom("A").build(),
                Produit.builder().id(2L).nom("B").build()
        ));

        List<ProduitResponseDTO> resultat = produitService.getTousProduits();

        assertThat(resultat).hasSize(2);
    }

    @Test
    @DisplayName("getTousProduits() retourne une liste vide s'il n'y a aucun produit")
    void getTousProduits_vide() {
        when(produitRepository.findAll()).thenReturn(List.of());

        assertThat(produitService.getTousProduits()).isEmpty();
    }

    // =====================================================
    // modifierProduit
    // =====================================================

    @Test
    @DisplayName("modifierProduit() met à jour tous les champs, y compris actif quand il est fourni")
    void modifierProduit_actifFourni_metAJourTout() {
        Produit existant = Produit.builder()
                .id(1L).nom("Ancien").description("old").prix(1.0).stock(1)
                .categorie("Old").actif(true).build();
        when(produitRepository.findById(1L)).thenReturn(Optional.of(existant));
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));

        ProduitResponseDTO resultat = produitService.modifierProduit(1L, request(false));

        assertThat(resultat.getId()).isEqualTo(1L);
        assertThat(resultat.getNom()).isEqualTo("Laptop Pro");
        assertThat(resultat.getDescription()).isEqualTo("Ultraportable");
        assertThat(resultat.getPrix()).isEqualTo(1299.99);
        assertThat(resultat.getStock()).isEqualTo(10);
        assertThat(resultat.getCategorie()).isEqualTo("Informatique");
        assertThat(resultat.getActif()).isFalse();
        verify(produitRepository).save(existant);
    }

    @Test
    @DisplayName("modifierProduit() ne touche pas à actif quand le DTO le laisse null")
    void modifierProduit_actifNull_conserveLaValeurExistante() {
        Produit existant = Produit.builder().id(1L).nom("Ancien").actif(false).build();
        when(produitRepository.findById(1L)).thenReturn(Optional.of(existant));
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));

        ProduitResponseDTO resultat = produitService.modifierProduit(1L, request(null));

        assertThat(resultat.getActif()).isFalse();
        assertThat(resultat.getNom()).isEqualTo("Laptop Pro");
    }

    @Test
    @DisplayName("modifierProduit() lève ProduitNotFoundException si l'ID est inconnu")
    void modifierProduit_produitAbsent_doitLeverException() {
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produitService.modifierProduit(99L, request(true)))
                .isInstanceOf(ProduitNotFoundException.class)
                .hasMessageContaining("99");

        verify(produitRepository, never()).save(any());
    }

    // =====================================================
    // supprimerProduit
    // =====================================================

    @Test
    @DisplayName("supprimerProduit() supprime quand le produit existe")
    void supprimerProduit_produitExistant_supprime() {
        when(produitRepository.existsById(5L)).thenReturn(true);

        produitService.supprimerProduit(5L);

        verify(produitRepository).deleteById(5L);
    }

    @Test
    @DisplayName("supprimerProduit() doit lever une exception si le produit n'existe pas")
    void supprimerProduit_produitAbsent_doitLeverException() {
        when(produitRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> produitService.supprimerProduit(42L))
                .isInstanceOf(ProduitNotFoundException.class)
                .hasMessageContaining("42");

        verify(produitRepository, never()).deleteById(any());
    }

    // =====================================================
    // recherches
    // =====================================================

    @Test
    @DisplayName("rechercherParCategorie() délègue au repository et mappe en DTO")
    void rechercherParCategorie_retourneLesProduitsDeLaCategorie() {
        when(produitRepository.findByCategorieIgnoreCase("bio")).thenReturn(List.of(
                Produit.builder().id(1L).nom("Miel").categorie("Bio").build(),
                Produit.builder().id(2L).nom("Huile").categorie("Bio").build()
        ));

        List<ProduitResponseDTO> resultat = produitService.rechercherParCategorie("bio");

        assertThat(resultat).hasSize(2);
        assertThat(resultat).extracting(ProduitResponseDTO::getNom).containsExactly("Miel", "Huile");
    }

    @Test
    @DisplayName("rechercherParNom() délègue au repository et mappe en DTO")
    void rechercherParNom_retourneLesProduitsCorrespondants() {
        when(produitRepository.findByNomContainingIgnoreCase("mi")).thenReturn(List.of(
                Produit.builder().id(1L).nom("Miel").build()
        ));

        List<ProduitResponseDTO> resultat = produitService.rechercherParNom("mi");

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getNom()).isEqualTo("Miel");
    }

    @Test
    @DisplayName("rechercherParNom() retourne une liste vide si rien ne correspond")
    void rechercherParNom_aucunResultat() {
        when(produitRepository.findByNomContainingIgnoreCase("zzz")).thenReturn(List.of());

        assertThat(produitService.rechercherParNom("zzz")).isEmpty();
    }

    @Test
    @DisplayName("getProduitsEnRuptureDeStock() ne doit retourner que les produits à stock 0")
    void getProduitsEnRuptureDeStock_doitFiltrerParStock() {
        when(produitRepository.findByStock(0)).thenReturn(List.of(
                Produit.builder().id(1L).nom("Souris").stock(0).build()
        ));

        List<ProduitResponseDTO> resultat = produitService.getProduitsEnRuptureDeStock();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStock()).isZero();
    }
}