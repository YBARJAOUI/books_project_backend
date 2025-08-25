package com.example.book_store_backend.controller;

import com.example.book_store_backend.entity.DailyOffer;
import com.example.book_store_backend.service.DailyOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ======== DAILY OFFER CONTROLLER ========
@RestController
@RequestMapping("/api/daily-offers")
@RequiredArgsConstructor
@Tag(name = "Daily Offers", description = "API de gestion des offres du jour")
@CrossOrigin(origins = "*")
public class DailyOfferController {

    private final DailyOfferService dailyOfferService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle offre du jour")
    public ResponseEntity<DailyOffer> createDailyOffer(@Valid @RequestBody DailyOffer dailyOffer) {
        DailyOffer createdOffer = dailyOfferService.createDailyOffer(dailyOffer);
        return new ResponseEntity<>(createdOffer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une offre du jour")
    public ResponseEntity<DailyOffer> updateDailyOffer(
            @Parameter(description = "ID de l'offre") @PathVariable Long id,
            @Valid @RequestBody DailyOffer offerDetails) {
        DailyOffer updatedOffer = dailyOfferService.updateDailyOffer(id, offerDetails);
        return ResponseEntity.ok(updatedOffer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une offre du jour par ID")
    public ResponseEntity<DailyOffer> getDailyOfferById(
            @Parameter(description = "ID de l'offre") @PathVariable Long id) {
        DailyOffer offer = dailyOfferService.getDailyOfferById(id);
        return ResponseEntity.ok(offer);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les offres")
    public ResponseEntity<List<DailyOffer>> getAllOffers() {
        List<DailyOffer> offers = dailyOfferService.getAllOffers();
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer toutes les offres actives")
    public ResponseEntity<List<DailyOffer>> getAllActiveOffers() {
        List<DailyOffer> offers = dailyOfferService.getAllActiveOffers();
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/current")
    @Operation(summary = "Récupérer les offres actuellement valides")
    public ResponseEntity<List<DailyOffer>> getCurrentValidOffers() {
        List<DailyOffer> offers = dailyOfferService.getCurrentValidOffers();
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Récupérer les offres pour un livre spécifique")
    public ResponseEntity<List<DailyOffer>> getOffersByBook(
            @Parameter(description = "ID du livre") @PathVariable Long bookId) {
        List<DailyOffer> offers = dailyOfferService.getOffersByBook(bookId);
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/pack/{packId}")
    @Operation(summary = "Récupérer les offres pour un pack spécifique")
    public ResponseEntity<List<DailyOffer>> getOffersByPack(
            @Parameter(description = "ID du pack") @PathVariable Long packId) {
        List<DailyOffer> offers = dailyOfferService.getOffersByPack(packId);
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/{id}/is-valid")
    @Operation(summary = "Vérifier si une offre est encore valide")
    public ResponseEntity<Boolean> isOfferValid(
            @Parameter(description = "ID de l'offre") @PathVariable Long id) {
        boolean isValid = dailyOfferService.isOfferValid(id);
        return ResponseEntity.ok(isValid);
    }

    @PutMapping("/{id}/record-sale")
    @Operation(summary = "Enregistrer une vente pour une offre")
    public ResponseEntity<DailyOffer> recordSale(
            @Parameter(description = "ID de l'offre") @PathVariable Long id,
            @Parameter(description = "Quantité vendue") @RequestParam Integer quantity) {
        DailyOffer updatedOffer = dailyOfferService.recordSale(id, quantity);
        return ResponseEntity.ok(updatedOffer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une offre du jour (suppression logique)")
    public ResponseEntity<Void> deleteDailyOffer(
            @Parameter(description = "ID de l'offre") @PathVariable Long id) {
        dailyOfferService.deleteDailyOffer(id);
        return ResponseEntity.noContent().build();
    }

    // Gestion des erreurs
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}
