package com.example.book_store_backend.service;

import com.example.book_store_backend.entity.Book;
import com.example.book_store_backend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Créer un nouveau livre
     */
    public Book createBook(Book book) {
        log.info("Création d'un nouveau livre: {}", book.getTitle());

        // Vérifier si l'ISBN existe déjà
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Un livre avec cet ISBN existe déjà");
        }

        // Définir les valeurs par défaut
        if (book.getIsActive() == null) {
            book.setIsActive(true);
        }
        if (book.getIsFeatured() == null) {
            book.setIsFeatured(false);
        }

        Book savedBook = bookRepository.save(book);
        log.info("Livre créé avec succès. ID: {}", savedBook.getId());
        return savedBook;
    }

    /**
     * Mettre à jour un livre existant
     */
    public Book updateBook(Long id, Book bookDetails) {
        log.info("Mise à jour du livre avec ID: {}", id);

        Book existingBook = getBookById(id);

        // Vérifier l'unicité de l'ISBN lors de la mise à jour
        if (!existingBook.getIsbn().equals(bookDetails.getIsbn()) &&
                bookRepository.existsByIsbnAndIdNot(bookDetails.getIsbn(), id)) {
            throw new IllegalArgumentException("Un autre livre avec cet ISBN existe déjà");
        }

        // Mettre à jour les champs
        existingBook.setIsbn(bookDetails.getIsbn());
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setDescription(bookDetails.getDescription());
        existingBook.setPrice(bookDetails.getPrice());
        existingBook.setStockQuantity(bookDetails.getStockQuantity());
        existingBook.setCategory(bookDetails.getCategory());
        existingBook.setPublisher(bookDetails.getPublisher());
        existingBook.setPublicationYear(bookDetails.getPublicationYear());
        existingBook.setLanguage(bookDetails.getLanguage());
        existingBook.setPageCount(bookDetails.getPageCount());
        existingBook.setImageUrl(bookDetails.getImageUrl());
        existingBook.setIsActive(bookDetails.getIsActive());
        existingBook.setIsFeatured(bookDetails.getIsFeatured());

        Book updatedBook = bookRepository.save(existingBook);
        log.info("Livre mis à jour avec succès. ID: {}", updatedBook.getId());
        return updatedBook;
    }

    /**
     * Récupérer un livre par ID
     */
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé avec l'ID: " + id));
    }

    /**
     * Récupérer un livre par ISBN
     */
    @Transactional(readOnly = true)
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * Récupérer tous les livres actifs
     */
    @Transactional(readOnly = true)
    public List<Book> getAllActiveBooks() {
        return bookRepository.findByIsActiveTrue();
    }

    /**
     * Récupérer tous les livres avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    /**
     * Rechercher des livres
     */
    @Transactional(readOnly = true)
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    /**
     * Rechercher des livres avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Book> searchBooksWithPagination(String keyword, Pageable pageable) {
        return bookRepository.searchBooksWithPagination(keyword, pageable);
    }

    /**
     * Récupérer les livres par catégorie
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategoryAndIsActiveTrue(category);
    }

    /**
     * Récupérer les livres en vedette
     */
    @Transactional(readOnly = true)
    public List<Book> getFeaturedBooks() {
        return bookRepository.findByIsFeaturedTrue();
    }

    /**
     * Supprimer un livre (suppression logique)
     */
    public void deleteBook(Long id) {
        log.info("Suppression du livre avec ID: {}", id);
        Book book = getBookById(id);
        book.setIsActive(false);
        bookRepository.save(book);
        log.info("Livre supprimé (désactivé) avec succès. ID: {}", id);
    }

    /**
     * Supprimer définitivement un livre
     */
    public void permanentlyDeleteBook(Long id) {
        log.info("Suppression définitive du livre avec ID: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Livre non trouvé avec l'ID: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Livre supprimé définitivement avec succès. ID: {}", id);
    }

    /**
     * Mettre à jour le stock d'un livre
     */
    public Book updateBookStock(Long id, Integer newStock) {
        log.info("Mise à jour du stock du livre ID: {} vers {}", id, newStock);
        Book book = getBookById(id);
        book.setStockQuantity(newStock);
        return bookRepository.save(book);
    }

    /**
     * Réduire le stock lors d'une vente
     */
    public void reduceStock(Long bookId, Integer quantity) {
        Book book = getBookById(bookId);
        if (book.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Stock insuffisant pour le livre: " + book.getTitle());
        }
        book.setStockQuantity(book.getStockQuantity() - quantity);
        bookRepository.save(book);
        log.info("Stock réduit de {} pour le livre ID: {}", quantity, bookId);
    }

    /**
     * Récupérer les livres avec stock faible
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksWithLowStock(Integer threshold) {
        return bookRepository.findBooksWithLowStock(threshold != null ? threshold : 10);
    }

    /**
     * Récupérer toutes les catégories
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return bookRepository.findDistinctCategories();
    }

    /**
     * Récupérer les livres par plage de prix
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return bookRepository.findBooksByPriceRange(minPrice, maxPrice);
    }

    /**
     * Marquer/Démarquer un livre comme vedette
     */
    public Book toggleFeaturedStatus(Long id) {
        Book book = getBookById(id);
        book.setIsFeatured(!book.getIsFeatured());
        return bookRepository.save(book);
    }
}