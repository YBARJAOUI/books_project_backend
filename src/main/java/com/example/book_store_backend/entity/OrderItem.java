package com.example.book_store_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "Le livre est obligatoire")
    private Book book;

    @Column(nullable = false)
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être supérieure à 0")
    private Integer quantity;

    @Column(nullable = false)
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Digits(integer = 8, fraction = 2, message = "Format de prix invalide")
    private BigDecimal price;

    // Prix au moment de l'achat (peut différer du prix actuel du livre)
    @Column
    private String bookTitle; // Sauvegarde du titre au moment de l'achat

    @Column
    private String bookAuthor; // Sauvegarde de l'auteur au moment de l'achat

    // Constructeur utilitaire
    public OrderItem(Book book, Integer quantity) {
        this.book = book;
        this.quantity = quantity;
        this.price = book.getPrice();
        this.bookTitle = book.getTitle();
        this.bookAuthor = book.getAuthor();
    }

    // Méthode pour calculer le sous-total
    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}