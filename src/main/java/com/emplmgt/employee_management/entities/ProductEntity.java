package com.emplmgt.employee_management.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_products")
@Data
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product", nullable = false, unique = true)
    private String productName;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "productEntity", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ProductPricesEntity> prices;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
