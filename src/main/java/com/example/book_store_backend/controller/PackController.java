package com.example.book_store_backend.controller;

import com.example.book_store_backend.entity.Pack;
import com.example.book_store_backend.entity.DailyOffer;
import com.example.book_store_backend.service.PackService;
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

// ======== PACK CONTROLLER ========
@RestController
@RequestMapping("/api/packs")
@RequiredArgsConstructor
@Tag(name = "Packs", description = "API de gestion des packs de livres")
@CrossOrigin(origins = "*")
public class PackController {

    private final PackService packService;

    @PostMapping
    @Operation(summary = "Créer un nouveau pack")
    public ResponseEntity<Pack> createPack(@Valid @RequestBody Pack pack) {
        Pack createdPack = packService.createPack(pack);
        return new ResponseEntity<>(createdPack, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un pack existant")
    public ResponseEntity<Pack> updatePack(
            @Parameter(description = "ID du pack") @PathVariable Long id,
            @Valid @RequestBody Pack packDetails) {
        Pack updatedPack = packService.updatePack(id, packDetails);
        return ResponseEntity.ok(updatedPack);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un pack par ID")
    public ResponseEntity<Pack> getPackById(
            @Parameter(description = "ID du pack") @PathVariable Long id) {
        Pack pack = packService.getPackById(id);
        return ResponseEntity.ok(pack);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les packs")
    public ResponseEntity<List<Pack>> getAllPacks() {
        List<Pack> packs = packService.getAllPacks();
        return ResponseEntity.ok(packs);
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer tous les packs actifs")
    public ResponseEntity<List<Pack>> getAllActivePacks() {
        List<Pack> packs = packService.getAllActivePacks();
        return ResponseEntity.ok(packs);
    }

    @GetMapping("/featured")
    @Operation(summary = "Récupérer les packs en vedette")
    public ResponseEntity<List<Pack>> getFeaturedPacks() {
        List<Pack> packs = packService.getFeaturedPacks();
        return ResponseEntity.ok(packs);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Récupérer les packs par catégorie")
    public ResponseEntity<List<Pack>> getPacksByCategory(
            @Parameter(description = "Catégorie") @PathVariable String category) {
        List<Pack> packs = packService.getPacksByCategory(category);
        return ResponseEntity.ok(packs);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des packs par nom")
    public ResponseEntity<List<Pack>> searchPacks(
            @Parameter(description = "Mot-clé de recherche") @RequestParam String keyword) {
        List<Pack> packs = packService.searchPacks(keyword);
        return ResponseEntity.ok(packs);
    }

    @GetMapping("/categories")
    @Operation(summary = "Récupérer toutes les catégories de packs")
    public ResponseEntity<List<String>> getAllPackCategories() {
        List<String> categories = packService.getAllPackCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}/toggle-featured")
    @Operation(summary = "Marquer/Démarquer un pack comme vedette")
    public ResponseEntity<Pack> toggleFeaturedStatus(
            @Parameter(description = "ID du pack") @PathVariable Long id) {
        Pack updatedPack = packService.toggleFeaturedStatus(id);
        return ResponseEntity.ok(updatedPack);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un pack (suppression logique)")
    public ResponseEntity<Void> deletePack(
            @Parameter(description = "ID du pack") @PathVariable Long id) {
        packService.deletePack(id);
        return ResponseEntity.noContent().build();
    }

    // Gestion des erreurs
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}

