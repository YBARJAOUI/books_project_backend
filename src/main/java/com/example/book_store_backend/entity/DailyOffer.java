package com.example.book_store_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Le titre de l'offre est obligatoire")
    @Size(min = 2, max = 150, message = "Le titre doit contenir entre 2 et 150 caractères")
    private String title;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Le prix original est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix original doit être supérieur à 0")
    @Digits(integer = 8, fraction = 2, message = "Format de prix invalide")
    private BigDecimal originalPrice;

    @Column(nullable = false)
    @NotNull(message = "Le prix de l'offre est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix de l'offre doit être supérieur à 0")
    @Digits(integer = 8, fraction = 2, message = "Format de prix invalide")
    private BigDecimal offerPrice;

    @Column
    @Min(value = 0, message = "Le pourcentage de remise ne peut pas être négatif")
    @Max(value = 100, message = "Le pourcentage de remise ne peut pas dépasser 100%")
    private Integer discountPercentage;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    @Column(nullable = false)
    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate endDate;

    @Column
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book; // Livre en promotion (optionnel)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id")
    private Pack pack; // Pack en promotion (optionnel)

    @Column
    @Min(value = 0, message = "La quantité limite ne peut pas être négative")
    private Integer limitQuantity; // Quantité limitée pour l'offre

    @Column
    @Min(value = 0, message = "La quantité vendue ne peut pas être négative")
    private Integer soldQuantity = 0; // Quantité déjà vendue

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;

    // Méthode pour vérifier si l'offre est toujours valide
    public boolean isValidOffer() {
        LocalDate now = LocalDate.now();
        return isActive &&
                !now.isBefore(startDate) &&
                !now.isAfter(endDate) &&
                (limitQuantity == null || soldQuantity < limitQuantity);
    }

    // Méthode pour calculer le pourcentage de remise
    public void calculateDiscountPercentage() {
        if (originalPrice != null && offerPrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = originalPrice.subtract(offerPrice);
            BigDecimal percentage = discount.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            this.discountPercentage = percentage.intValue();
        }
    }
}