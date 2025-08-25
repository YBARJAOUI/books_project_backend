package com.example.book_store_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSummaryDTO {
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private String createdAt;
}
