package com.example.book_store_backend.repository;

import com.example.book_store_backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Trouver un client par email
    Optional<Customer> findByEmail(String email);

    // Trouver un client par numéro de téléphone
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // Trouver les clients actifs
    List<Customer> findByIsActiveTrue();

    // Recherche par nom et prénom
    @Query("SELECT c FROM Customer c WHERE c.isActive = true AND " +
            "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Customer> searchCustomersByName(@Param("keyword") String keyword);

    // Recherche globale (nom, prénom, email, téléphone)
    @Query("SELECT c FROM Customer c WHERE c.isActive = true AND " +
            "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "c.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    List<Customer> searchCustomers(@Param("keyword") String keyword);

    // Trouver les clients par ville
    List<Customer> findByCityAndIsActiveTrue(String city);

    // Trouver les clients par pays
    List<Customer> findByCountryAndIsActiveTrue(String country);

    // Vérifier l'existence par email (excluant un ID spécifique pour les mises à jour)
    boolean existsByEmailAndIdNot(String email, Long id);

    // Vérifier l'existence par numéro de téléphone (excluant un ID spécifique)
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    // Compter les clients par ville
    @Query("SELECT c.city, COUNT(c) FROM Customer c WHERE c.isActive = true AND c.city IS NOT NULL GROUP BY c.city")
    List<Object[]> countCustomersByCity();

    // Trouver les clients les plus actifs (avec le plus de commandes)
    @Query("SELECT c FROM Customer c WHERE c.isActive = true ORDER BY SIZE(c.orders) DESC")
    List<Customer> findMostActiveCustomers();
}