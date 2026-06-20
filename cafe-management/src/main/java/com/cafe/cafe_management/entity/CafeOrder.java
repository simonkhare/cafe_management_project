package com.cafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cafe_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CafeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerUsername;

    @Column(nullable = false, length = 1000)
    private String itemsSummary; // Stores item breakdown (e.g., "2 x Espresso, 1 x Croissant")

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String status; // "PENDING" or "COMPLETED"

}