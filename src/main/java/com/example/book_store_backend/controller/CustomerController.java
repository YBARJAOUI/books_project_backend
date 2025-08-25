package com.example.book_store_backend.controller;

import com.example.book_store_backend.entity.Customer;
import com.example.book_store_backend.service.CustomerService;
import com.example.book_store_backend.service.CustomerService.CustomerStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "API de gestion des clients")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Créer un nouveau client")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PostMapping("/create-or-get")
    @Operation(summary = "Créer un client ou récupérer s'il existe déjà")
    public ResponseEntity<Customer> createOrGetCustomer(@Valid @RequestBody Customer customer) {
        Customer resultCustomer = customerService.createOrGetCustomer(customer);
        return ResponseEntity.ok(resultCustomer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un client existant")
    public ResponseEntity<Customer> updateCustomer(
            @Parameter(description = "ID du client") @PathVariable Long id,
            @Valid @RequestBody Customer customerDetails) {
        Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client par ID")
    public ResponseEntity<Customer> getCustomerById(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Récupérer un client par email")
    public ResponseEntity<Customer> getCustomerByEmail(
            @Parameter(description = "Email du client") @PathVariable String email) {
        Optional<Customer> customer = customerService.getCustomerByEmail(email);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phoneNumber}")
    @Operation(summary = "Récupérer un client par numéro de téléphone")
    public ResponseEntity<Customer> getCustomerByPhoneNumber(
            @Parameter(description = "Numéro de téléphone") @PathVariable String phoneNumber) {
        Optional<Customer> customer = customerService.getCustomerByPhoneNumber(phoneNumber);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les clients")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer tous les clients actifs")
    public ResponseEntity<List<Customer>> getAllActiveCustomers() {
        List<Customer> customers = customerService.getAllActiveCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des clients")
    public ResponseEntity<List<Customer>> searchCustomers(
            @Parameter(description = "Mot-clé de recherche") @RequestParam String keyword) {
        List<Customer> customers = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search/name")
    @Operation(summary = "Rechercher des clients par nom")
    public ResponseEntity<List<Customer>> searchCustomersByName(
            @Parameter(description = "Mot-clé de recherche") @RequestParam String keyword) {
        List<Customer> customers = customerService.searchCustomersByName(keyword);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Récupérer les clients par ville")
    public ResponseEntity<List<Customer>> getCustomersByCity(
            @Parameter(description = "Ville") @PathVariable String city) {
        List<Customer> customers = customerService.getCustomersByCity(city);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "Récupérer les clients par pays")
    public ResponseEntity<List<Customer>> getCustomersByCountry(
            @Parameter(description = "Pays") @PathVariable String country) {
        List<Customer> customers = customerService.getCustomersByCountry(country);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/most-active")
    @Operation(summary = "Récupérer les clients les plus actifs")
    public ResponseEntity<List<Customer>> getMostActiveCustomers() {
        List<Customer> customers = customerService.getMostActiveCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Vérifier si un client existe par email")
    public ResponseEntity<Boolean> customerExistsByEmail(
            @Parameter(description = "Email") @PathVariable String email) {
        boolean exists = customerService.customerExistsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/phone/{phoneNumber}")
    @Operation(summary = "Vérifier si un client existe par numéro de téléphone")
    public ResponseEntity<Boolean> customerExistsByPhoneNumber(
            @Parameter(description = "Numéro de téléphone") @PathVariable String phoneNumber) {
        boolean exists = customerService.customerExistsByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Récupérer les statistiques des clients")
    public ResponseEntity<CustomerStatistics> getCustomerStatistics() {
        CustomerStatistics statistics = customerService.getCustomerStatistics();
        return ResponseEntity.ok(statistics);
    }

    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "Activer/Désactiver un client")
    public ResponseEntity<Customer> toggleCustomerStatus(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        Customer updatedCustomer = customerService.toggleCustomerStatus(id);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client (suppression logique)")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Supprimer définitivement un client")
    public ResponseEntity<Void> permanentlyDeleteCustomer(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        customerService.permanentlyDeleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // Gestion des erreurs
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}