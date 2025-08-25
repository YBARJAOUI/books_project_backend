package com.example.book_store_backend.repository;

import com.example.book_store_backend.entity.Order;
import com.example.book_store_backend.entity.OrderStatus;
import com.example.book_store_backend.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Trouver une commande par numéro
    Optional<Order> findByOrderNumber(String orderNumber);

    // Trouver les commandes d'un client
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    // Trouver les commandes par statut
    List<Order> findByStatus(OrderStatus status);

    // Trouver les commandes par statut de paiement
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    // Trouver les commandes dans une plage de dates
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // Trouver les commandes avec pagination
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Trouver les commandes d'un client avec pagination
    Page<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    // Calculer le total des ventes pour une période
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
    BigDecimal calculateTotalSalesForPeriod(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Compter les commandes par statut
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    // Trouver les commandes avec un montant supérieur à un seuil
    List<Order> findByTotalAmountGreaterThanOrderByTotalAmountDesc(BigDecimal amount);

    // Trouver les dernières commandes
    List<Order> findTop10ByOrderByCreatedAtDesc();

    // Recherche par numéro de commande ou nom de client
    @Query("SELECT o FROM Order o WHERE " +
            "o.orderNumber LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(CONCAT(o.customer.firstName, ' ', o.customer.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Order> searchOrders(@Param("keyword") String keyword);

    // Statistiques de ventes par mois
    @Query("SELECT YEAR(o.createdAt), MONTH(o.createdAt), COUNT(o), SUM(o.totalAmount) " +
            "FROM Order o WHERE o.status != 'CANCELLED' " +
            "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " +
            "ORDER BY YEAR(o.createdAt) DESC, MONTH(o.createdAt) DESC")
    List<Object[]> getMonthlySalesStats();

    // Trouver les commandes en attente de traitement
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED') ORDER BY o.createdAt ASC")
    List<Order> findPendingOrders();
}