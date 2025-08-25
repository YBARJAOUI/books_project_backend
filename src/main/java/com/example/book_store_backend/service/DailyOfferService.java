package com.example.book_store_backend.service;

import com.example.book_store_backend.entity.DailyOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// ======== DAILY OFFER SERVICE ========
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DailyOfferService {

    private final DailyOfferRepository dailyOfferRepository;

    /**
     * Créer une nouvelle offre du jour
     */
    public DailyOffer createDailyOffer(DailyOffer dailyOffer) {
        log.info("Création d'une nouvelle offre du jour: {}", dailyOffer.getTitle());

        // Calculer automatiquement le pourcentage de remise
        dailyOffer.calculateDiscountPercentage();

        if (dailyOffer.getIsActive() == null) {
            dailyOffer.setIsActive(true);
        }
        if (dailyOffer.getSoldQuantity() == null) {
            dailyOffer.setSoldQuantity(0);
        }

        DailyOffer savedOffer = dailyOfferRepository.save(dailyOffer);
        log.info("Offre du jour créée avec succès. ID: {}", savedOffer.getId());
        return savedOffer;
    }

    /**
     * Mettre à jour une offre du jour
     */
    public DailyOffer updateDailyOffer(Long id, DailyOffer offerDetails) {
        log.info("Mise à jour de l'offre du jour avec ID: {}", id);

        DailyOffer existingOffer = getDailyOfferById(id);

        existingOffer.setTitle(offerDetails.getTitle());
        existingOffer.setDescription(offerDetails.getDescription());
        existingOffer.setOriginalPrice(offerDetails.getOriginalPrice());
        existingOffer.setOfferPrice(offerDetails.getOfferPrice());
        existingOffer.setImageUrl(offerDetails.getImageUrl());
        existingOffer.setStartDate(offerDetails.getStartDate());
        existingOffer.setEndDate(offerDetails.getEndDate());
        existingOffer.setIsActive(offerDetails.getIsActive());
        existingOffer.setBook(offerDetails.getBook());
        existingOffer.setPack(offerDetails.getPack());
        existingOffer.setLimitQuantity(offerDetails.getLimitQuantity());

        // Recalculer le pourcentage de remise
        existingOffer.calculateDiscountPercentage();

        DailyOffer updatedOffer = dailyOfferRepository.save(existingOffer);
        log.info("Offre du jour mise à jour avec succès. ID: {}", updatedOffer.getId());
        return updatedOffer;
    }

    /**
     * Récupérer une offre du jour par ID
     */
    @Transactional(readOnly = true)
    public DailyOffer getDailyOfferById(Long id) {
        return dailyOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre du jour non trouvée avec l'ID: " + id));
    }

    /**
     * Récupérer toutes les offres actives
     */
    @Transactional(readOnly = true)
    public List<DailyOffer> getAllActiveOffers() {
        return dailyOfferRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    /**
     * Récupérer les offres actuellement valides
     */
    @Transactional(readOnly = true)
    public List<DailyOffer> getCurrentValidOffers() {
        LocalDate today = LocalDate.now();
        return dailyOfferRepository.findCurrentActiveOffers(today);
    }

    /**
     * Récupérer toutes les offres
     */
    @Transactional(readOnly = true)
    public List<DailyOffer> getAllOffers() {
        return dailyOfferRepository.findAll();
    }

    /**
     * Récupérer les offres pour un livre spécifique
     */
    @Transactional(readOnly = true)
    public List<DailyOffer> getOffersByBook(Long bookId) {
        return dailyOfferRepository.findByBookIdAndIsActiveTrue(bookId);
    }

    /**
     * Récupérer les offres pour un pack spécifique
     */
    @Transactional(readOnly = true)
    public List<DailyOffer> getOffersByPack(Long packId) {
        return dailyOfferRepository.findByPackIdAndIsActiveTrue(packId);
    }

    /**
     * Supprimer une offre du jour (suppression logique)
     */
    public void deleteDailyOffer(Long id) {
        log.info("Suppression de l'offre du jour avec ID: {}", id);
        DailyOffer offer = getDailyOfferById(id);
        offer.setIsActive(false);
        dailyOfferRepository.save(offer);
        log.info("Offre du jour supprimée (désactivée) avec succès. ID: {}", id);
    }

    /**
     * Enregistrer une vente pour une offre (incrémenter la quantité vendue)
     */
    public DailyOffer recordSale(Long offerId, Integer quantity) {
        DailyOffer offer = getDailyOfferById(offerId);

        if (!offer.isValidOffer()) {
            throw new IllegalStateException("Cette offre n'est plus valide");
        }

        if (offer.getLimitQuantity() != null &&
                offer.getSoldQuantity() + quantity > offer.getLimitQuantity()) {
            throw new IllegalArgumentException("Quantité demandée dépasse la limite disponible");
        }

        offer.setSoldQuantity(offer.getSoldQuantity() + quantity);
        return dailyOfferRepository.save(offer);
    }

    /**
     * Vérifier si une offre est encore valide
     */
    @Transactional(readOnly = true)
    public boolean isOfferValid(Long offerId) {
        DailyOffer offer = getDailyOfferById(offerId);
        return offer.isValidOffer();
    }
}
