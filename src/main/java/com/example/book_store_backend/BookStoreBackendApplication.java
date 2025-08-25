package com.example.book_store_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class BookStoreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreBackendApplication.class, args);
        System.out.println("=".repeat(60));
        System.out.println("🚀 Book Store Backend démarré avec succès!");
        System.out.println("📖 API Documentation: http://localhost:8080/swagger-ui.html");
        System.out.println("🔗 API Base URL: http://localhost:8080/api");
        System.out.println("📊 Endpoints disponibles:");
        System.out.println("   • Books: /api/books");
        System.out.println("   • Customers: /api/customers");
        System.out.println("   • Orders: /api/orders");
        System.out.println("   • Packs: /api/packs");
        System.out.println("   • Daily Offers: /api/daily-offers");
        System.out.println("=".repeat(60));
    }
}