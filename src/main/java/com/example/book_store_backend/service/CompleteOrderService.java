package com.example.book_store_backend.service;

import com.example.book_store_backend.dto.CreateOrderDTO;
import com.example.book_store_backend.dto.CustomerDTO;
import com.example.book_store_backend.dto.OrderItemDTO;
import com.example.book_store_backend.entity.Customer;
import com.example.book_store_backend.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompleteOrderService {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;

    /**
     * Créer une commande complète avec création automatique du client si nécessaire
     */
    public Order createCompleteOrder(CreateOrderDTO orderDTO) {
        log.info("Création d'une commande complète pour le client: {} {}",
                orderDTO.getCustomer().getFirstName(), orderDTO.getCustomer().getLastName());

        // Convertir CustomerDTO en Customer entity
        Customer customerEntity = modelMapper.map(orderDTO.getCustomer(), Customer.class);

        // Créer ou récupérer le client
        Customer customer = customerService.createOrGetCustomer(customerEntity);

        // Convertir les items de commande
        List<OrderService.OrderItemRequest> orderItems = orderDTO.getItems().stream()
                .map(item -> new OrderService.OrderItemRequest(item.getBookId(), item.getQuantity()))
                .collect(Collectors.toList());

        // Créer la commande
        Order order = orderService.createOrder(
                customer.getId(),
                orderItems,
                orderDTO.getShippingAddress(),
                orderDTO.getNotes()
        );

        log.info("Commande complète créée avec succès. Numéro: {}", order.getOrderNumber());
        return order;
    }

    /**
     * Créer une commande simplifiée (avec juste les infos minimales du client)
     */
    public Order createSimpleOrder(String firstName, String lastName, String email,
                                   String phoneNumber, String address,
                                   List<OrderItemDTO> items, String notes) {

        // Créer le DTO client
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName(firstName);
        customerDTO.setLastName(lastName);
        customerDTO.setEmail(email);
        customerDTO.setPhoneNumber(phoneNumber);
        customerDTO.setAddress(address);

        // Créer le DTO de commande
        CreateOrderDTO orderDTO = new CreateOrderDTO();
        orderDTO.setCustomer(customerDTO);
        orderDTO.setItems(items);
        orderDTO.setNotes(notes);

        return createCompleteOrder(orderDTO);
    }
}