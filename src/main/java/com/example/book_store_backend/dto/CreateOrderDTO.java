
package com.example.book_store_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderDTO {
    @NotNull(message = "Les informations du client sont obligatoires")
    private CustomerDTO customer;

    @NotEmpty(message = "La liste des articles ne peut pas être vide")
    private List<OrderItemDTO> items;

    private String shippingAddress;
    private String notes;
}

