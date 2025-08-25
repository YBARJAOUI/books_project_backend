package com.example.book_store_backend.service;

import com.example.book_store_backend.entity.Pack;
import com.example.book_store_backend.entity.DailyOffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// ======== PACK REPOSITORY ========
@org.springframework.stereotype.Repository
interface PackRepository extends org.springframework.data.jpa.repository.JpaRepository<Pack, Long> {
    List<Pack> findByIsActiveTrueOrderByCreatedAtDesc();
    List<Pack> findByIsFeaturedTrue();
    List<Pack> findByCategoryAndIsActiveTrue(String category);
    List<Pack> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT p.category FROM Pack p WHERE p.category IS NOT NULL AND p.isActive = true")
    List<String> findDistinctCategories();
}

// ======== DAILY OFFER REPOSITORY ========
@org.springframework.stereotype.Repository
interface DailyOfferRepository extends org.springframework.data.jpa.repository.JpaRepository<DailyOffer, Long> {
    List<DailyOffer> findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByCreatedAtDesc(LocalDate date1, LocalDate date2);
    List<DailyOffer> findByIsActiveTrueOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT d FROM DailyOffer d WHERE d.isActive = true AND :currentDate BETWEEN d.startDate AND d.endDate")
    List<DailyOffer> findCurrentActiveOffers(@org.springframework.data.repository.query.Param("currentDate") LocalDate currentDate);

    List<DailyOffer> findByBookIdAndIsActiveTrue(Long bookId);
    List<DailyOffer> findByPackIdAndIsActiveTrue(Long packId);
}

// ======== PACK SERVICE ========
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PackService {

    private final PackRepository packRepository;

    /**
     * Créer un nouveau pack
     */
    public Pack createPack(Pack pack) {
        log.info("Création d'un nouveau pack: {}", pack.getName());

        if (pack.getIsActive() == null) {
            pack.setIsActive(true);
        }
        if (pack.getIsFeatured() == null) {
            pack.setIsFeatured(false);
        }

        Pack savedPack = packRepository.save(pack);
        log.info("Pack créé avec succès. ID: {}", savedPack.getId());
        return savedPack;
    }

    /**
     * Mettre à jour un pack existant
     */
    public Pack updatePack(Long id, Pack packDetails) {
        log.info("Mise à jour du pack avec ID: {}", id);

        Pack existingPack = getPackById(id);

        existingPack.setName(packDetails.getName());
        existingPack.setDescription(packDetails.getDescription());
        existingPack.setPrice(packDetails.getPrice());
        existingPack.setImageUrl(packDetails.getImageUrl());
        existingPack.setIsActive(packDetails.getIsActive());
        existingPack.setIsFeatured(packDetails.getIsFeatured());
        existingPack.setStockQuantity(packDetails.getStockQuantity());
        existingPack.setCategory(packDetails.getCategory());

        Pack updatedPack = packRepository.save(existingPack);
        log.info("Pack mis à jour avec succès. ID: {}", updatedPack.getId());
        return updatedPack;
    }

    /**
     * Récupérer un pack par ID
     */
    @Transactional(readOnly = true)
    public Pack getPackById(Long id) {
        return packRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pack non trouvé avec l'ID: " + id));
    }

    /**
     * Récupérer tous les packs actifs
     */
    @Transactional(readOnly = true)
    public List<Pack> getAllActivePacks() {
        return packRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    /**
     * Récupérer tous les packs
     */
    @Transactional(readOnly = true)
    public List<Pack> getAllPacks() {
        return packRepository.findAll();
    }

    /**
     * Récupérer les packs en vedette
     */
    @Transactional(readOnly = true)
    public List<Pack> getFeaturedPacks() {
        return packRepository.findByIsFeaturedTrue();
    }

    /**
     * Récupérer les packs par catégorie
     */
    @Transactional(readOnly = true)
    public List<Pack> getPacksByCategory(String category) {
        return packRepository.findByCategoryAndIsActiveTrue(category);
    }

    /**
     * Rechercher des packs par nom
     */
    @Transactional(readOnly = true)
    public List<Pack> searchPacks(String keyword) {
        return packRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword);
    }

    /**
     * Supprimer un pack (suppression logique)
     */
    public void deletePack(Long id) {
        log.info("Suppression du pack avec ID: {}", id);
        Pack pack = getPackById(id);
        pack.setIsActive(false);
        packRepository.save(pack);
        log.info("Pack supprimé (désactivé) avec succès. ID: {}", id);
    }

    /**
     * Marquer/Démarquer un pack comme vedette
     */
    public Pack toggleFeaturedStatus(Long id) {
        Pack pack = getPackById(id);
        pack.setIsFeatured(!pack.getIsFeatured());
        return packRepository.save(pack);
    }

    /**
     * Récupérer toutes les catégories de packs
     */
    @Transactional(readOnly = true)
    public List<String> getAllPackCategories() {
        return packRepository.findDistinctCategories();
    }
}

