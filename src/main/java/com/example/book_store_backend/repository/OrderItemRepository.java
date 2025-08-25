package com.example.book_store_backend.repository;

import com.example.book_store_backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Trouver les items par commande
    List<OrderItem> findByOrderId(Long orderId);

    // Trouver les items par livre
    List<OrderItem> findByBookId(Long bookId);

    // Statistiques des ventes par livre
    @Query("SELECT oi.book.id, oi.book.title, SUM(oi.quantity), SUM(oi.price * oi.quantity) " +
            "FROM OrderItem oi WHERE oi.order.status != 'CANCELLED' " +
            "GROUP BY oi.book.id, oi.book.title " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getBookSalesStatistics();

    // Trouver les livres les plus vendus
    @Query("SELECT oi.book FROM OrderItem oi WHERE oi.order.status != 'CANCELLED' " +
            "GROUP BY oi.book ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findBestSellingBooks();

    // Calculer le total des ventes pour un livre
    @Query("SELECT SUM(oi.quantity * oi.price) FROM OrderItem oi WHERE oi.book.id = :bookId AND oi.order.status != 'CANCELLED'")
    java.math.BigDecimal getTotalSalesForBook(@Param("bookId") Long bookId);
}