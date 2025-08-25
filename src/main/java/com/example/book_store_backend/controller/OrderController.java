package com.example.book_store_backend.controller;

import com.example.book_store_backend.entity.Order;
import com.example.book_store_backend.entity.OrderStatus;
import com.example.book_store_backend.entity.PaymentStatus;
import com.example.book_store_backend.service.OrderService;
import com.example.book_store_backend.service.OrderService.OrderItemRequest;
import com.example.book_store_backend.service.OrderService.OrderStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API de gestion des commandes")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle commande")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(
                request.getCustomerId(),
                request.getItems(),
                request.getShippingAddress(),
                request.getNotes()
        );
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une commande par ID")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "ID de la commande") @PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Récupérer une commande par numéro")
    public ResponseEntity<Order> getOrderByNumber(
            @Parameter(description = "Numéro de la commande") @PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les commandes avec pagination")
    public ResponseEntity<Page<Order>> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Récupérer les commandes d'un client")
    public ResponseEntity<List<Order>> getCustomerOrders(
            @Parameter(description = "ID du client") @PathVariable Long customerId) {
        List<Order> orders = orderService.getCustomerOrders(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}/paginated")
    @Operation(summary = "Récupérer les commandes d'un client avec pagination")
    public ResponseEntity<Page<Order>> getCustomerOrdersWithPagination(
            @Parameter(description = "ID du client") @PathVariable Long customerId,
            Pageable pageable) {
        Page<Order> orders = orderService.getCustomerOrdersWithPagination(customerId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des commandes")
    public ResponseEntity<List<Order>> searchOrders(
            @Parameter(description = "Mot-clé de recherche") @RequestParam String keyword) {
        List<Order> orders = orderService.searchOrders(keyword);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Récupérer les commandes par statut")
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @Parameter(description = "Statut de la commande") @PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/pending")
    @Operation(summary = "Récupérer les commandes en attente de traitement")
    public ResponseEntity<List<Order>> getPendingOrders() {
        List<Order> orders = orderService.getPendingOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une commande")
    public ResponseEntity<Order> updateOrderStatus(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Parameter(description = "Nouveau statut") @RequestParam OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/payment-status")
    @Operation(summary = "Mettre à jour le statut de paiement")
    public ResponseEntity<Order> updatePaymentStatus(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Parameter(description = "Statut de paiement") @RequestParam PaymentStatus paymentStatus) {
        Order updatedOrder = orderService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Annuler une commande")
    public ResponseEntity<Order> cancelOrder(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Parameter(description = "Raison de l'annulation") @RequestParam(required = false) String reason) {
        Order cancelledOrder = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(cancelledOrder);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Récupérer les statistiques de commandes")
    public ResponseEntity<OrderStatistics> getOrderStatistics(
            @Parameter(description = "Date de début")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        OrderStatistics statistics = orderService.getOrderStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    // DTOs pour les requêtes
    public static class CreateOrderRequest {
        private Long customerId;
        private List<OrderItemRequest> items;
        private String shippingAddress;
        private String notes;

        // Constructeurs
        public CreateOrderRequest() {}

        public CreateOrderRequest(Long customerId, List<OrderItemRequest> items, String shippingAddress, String notes) {
            this.customerId = customerId;
            this.items = items;
            this.shippingAddress = shippingAddress;
            this.notes = notes;
        }

        // Getters et setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }

        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }

        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    // Gestion des erreurs
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}