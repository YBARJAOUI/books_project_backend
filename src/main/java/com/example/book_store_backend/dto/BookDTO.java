package com.example.book_store_backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDTO {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String publisher;
    private Integer publicationYear;
    private String language;
    private Integer pageCount;
    private String imageUrl;
    private Boolean isActive;
    private Boolean isFeatured;
}
