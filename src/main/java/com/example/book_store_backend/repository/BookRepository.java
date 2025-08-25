package com.example.book_store_backend.repository;

import com.example.book_store_backend.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Trouver un livre par ISBN
    Optional<Book> findByIsbn(String isbn);

    // Trouver les livres actifs
    List<Book> findByIsActiveTrue();

    // Trouver les livres en vedette
    List<Book> findByIsFeaturedTrue();

    // Trouver les livres par catégorie
    List<Book> findByCategoryAndIsActiveTrue(String category);

    // Trouver les livres par auteur
    List<Book> findByAuthorContainingIgnoreCaseAndIsActiveTrue(String author);

    // Recherche par titre
    List<Book> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);

    // Recherche globale (titre, auteur, description)
    @Query("SELECT b FROM Book b WHERE b.isActive = true AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Book> searchBooks(@Param("keyword") String keyword);

    // Recherche avec pagination
    @Query("SELECT b FROM Book b WHERE b.isActive = true AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> searchBooksWithPagination(@Param("keyword") String keyword, Pageable pageable);

    // Trouver les livres avec stock faible
    @Query("SELECT b FROM Book b WHERE b.isActive = true AND b.stockQuantity <= :threshold")
    List<Book> findBooksWithLowStock(@Param("threshold") Integer threshold);

    // Trouver les livres en rupture de stock
    List<Book> findByStockQuantityAndIsActiveTrue(Integer stockQuantity);

    // Trouver les catégories distinctes
    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.category IS NOT NULL AND b.isActive = true")
    List<String> findDistinctCategories();

    // Trouver les auteurs distincts
    @Query("SELECT DISTINCT b.author FROM Book b WHERE b.isActive = true")
    List<String> findDistinctAuthors();

    // Compter les livres par catégorie
    @Query("SELECT b.category, COUNT(b) FROM Book b WHERE b.isActive = true GROUP BY b.category")
    List<Object[]> countBooksByCategory();

    // Trouver les livres les plus vendus (basé sur OrderItems)
    @Query("SELECT b FROM Book b JOIN b.orderItems oi WHERE b.isActive = true GROUP BY b ORDER BY SUM(oi.quantity) DESC")
    List<Book> findBestSellingBooks(Pageable pageable);

    // Vérifier l'existence par ISBN (excluant un ID spécifique pour les mises à jour)
    boolean existsByIsbnAndIdNot(String isbn, Long id);

    // Trouver les livres par plage de prix
    @Query("SELECT b FROM Book b WHERE b.isActive = true AND b.price BETWEEN :minPrice AND :maxPrice")
    List<Book> findBooksByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice,
                                     @Param("maxPrice") java.math.BigDecimal maxPrice);
}