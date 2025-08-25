package com.example.book_store_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "packs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Le nom du pack est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du pack doit contenir entre 2 et 100 caractères")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Digits(integer = 8, fraction = 2, message = "Format de prix invalide")
    private BigDecimal price;

    @Column
    private String imageUrl;

    @Column
    private Boolean isActive = true;

    @Column
    private Boolean isFeatured = false;

    @Column
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer stockQuantity = 0;

    @Column
    private String category;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}