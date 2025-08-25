package com.example.book_store_backend.service;

import com.example.book_store_backend.entity.*;
import com.example.book_store_backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final BookService bookService;

    /**
     * Créer une nouvelle commande
     */
    public Order createOrder(Long customerId, List<OrderItemRequest> items, String shippingAddress, String notes) {
        log.info("Création d'une nouvelle commande pour le client ID: {}", customerId);

        // Récupérer le client
        Customer customer = customerService.getCustomerById(customerId);

        // Créer la commande
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderNumber(generateOrderNumber());
        order.setShippingAddress(shippingAddress != null ? shippingAddress : customer.getAddress());
        order.setNotes(notes);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // Traiter les articles de la commande
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : items) {
            Book book = bookService.getBookById(itemRequest.getBookId());

            // Vérifier le stock
            if (book.getStockQuantity() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Stock insuffisant pour le livre: " + book.getTitle());
            }

            // Créer l'article de commande
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(book.getPrice());
            orderItem.setBookTitle(book.getTitle());
            orderItem.setBookAuthor(book.getAuthor());

            order.addOrderItem(orderItem);

            // Calculer le sous-total
            BigDecimal subTotal = book.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(subTotal);

            // Réduire le stock
            bookService.reduceStock(book.getId(), itemRequest.getQuantity());
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Commande créée avec succès. Numéro: {}, Total: {}", savedOrder.getOrderNumber(), savedOrder.getTotalAmount());
        return savedOrder;
    }

    /**
     * Mettre à jour le statut d'une commande
     */
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Mise à jour du statut de la commande ID: {} vers {}", orderId, newStatus);

        Order order = getOrderById(orderId);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        // Mettre à jour les dates selon le statut
        switch (newStatus) {
            case SHIPPED:
                if (order.getShippedAt() == null) {
                    order.setShippedAt(LocalDateTime.now());
                }
                break;
            case DELIVERED:
                if (order.getDeliveredAt() == null) {
                    order.setDeliveredAt(LocalDateTime.now());
                }
                break;
            case CANCELLED:
                // Restaurer le stock si la commande est annulée
                if (oldStatus != OrderStatus.CANCELLED) {
                    restoreStockFromCancelledOrder(order);
                }
                break;
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Statut de la commande mis à jour avec succès");
        return updatedOrder;
    }

    /**
     * Mettre à jour le statut de paiement
     */
    public Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        log.info("Mise à jour du statut de paiement de la commande ID: {} vers {}", orderId, paymentStatus);

        Order order = getOrderById(orderId);
        order.setPaymentStatus(paymentStatus);

        // Si le paiement est confirmé et que la commande est en attente, la passer en confirmée
        if (paymentStatus == PaymentStatus.PAID && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CONFIRMED);
        }

        return orderRepository.save(order);
    }

    /**
     * Récupérer une commande par ID
     */
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));
    }

    /**
     * Récupérer une commande par numéro
     */
    @Transactional(readOnly = true)
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec le numéro: " + orderNumber));
    }

    /**
     * Récupérer toutes les commandes avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * Récupérer les commandes d'un client
     */
    @Transactional(readOnly = true)
    public List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    /**
     * Récupérer les commandes d'un client avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Order> getCustomerOrdersWithPagination(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable);
    }

    /**
     * Rechercher des commandes
     */
    @Transactional(readOnly = true)
    public List<Order> searchOrders(String keyword) {
        return orderRepository.searchOrders(keyword);
    }

    /**
     * Récupérer les commandes par statut
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Récupérer les commandes en attente de traitement
     */
    @Transactional(readOnly = true)
    public List<Order> getPendingOrders() {
        return orderRepository.findPendingOrders();
    }

    /**
     * Annuler une commande
     */
    public Order cancelOrder(Long orderId, String reason) {
        log.info("Annulation de la commande ID: {}", orderId);

        Order order = getOrderById(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Impossible d'annuler une commande déjà livrée");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cette commande est déjà annulée");
        }

        // Restaurer le stock
        restoreStockFromCancelledOrder(order);

        // Mettre à jour le statut
        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.REFUNDED);
        if (reason != null) {
            order.setNotes(order.getNotes() != null ? order.getNotes() + "\nAnnulation: " + reason : "Annulation: " + reason);
        }

        Order cancelledOrder = orderRepository.save(order);
        log.info("Commande annulée avec succès");
        return cancelledOrder;
    }

    /**
     * Calculer les statistiques de vente
     */
    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findOrdersByDateRange(startDate, endDate);

        BigDecimal totalRevenue = orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = orders.size();
        long completedOrders = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .count();

        BigDecimal averageOrderValue = totalOrders > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;

        return OrderStatistics.builder()
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .build();
    }

    /**
     * Générer un numéro de commande unique
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.format("%04d", (int)(Math.random() * 10000));
        return "ORD-" + timestamp + "-" + randomSuffix;
    }

    /**
     * Restaurer le stock des articles d'une commande annulée
     */
    private void restoreStockFromCancelledOrder(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Book book = item.getBook();
            book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
            bookService.updateBook(book.getId(), book);
        }
        log.info("Stock restauré pour la commande annulée: {}", order.getOrderNumber());
    }

    // Classes d'aide
    public static class OrderItemRequest {
        private Long bookId;
        private Integer quantity;

        // Constructeurs, getters et setters
        public OrderItemRequest() {}

        public OrderItemRequest(Long bookId, Integer quantity) {
            this.bookId = bookId;
            this.quantity = quantity;
        }

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    @lombok.Builder
    @lombok.Data
    public static class OrderStatistics {
        private long totalOrders;
        private long completedOrders;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderValue;
    }
}