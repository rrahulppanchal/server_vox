package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query("SELECT c FROM ProductEntity c LEFT JOIN FETCH c.prices WHERE c.isDeleted = false")
    List<ProductEntity> findAllProductsWithPrices();

    @Query("SELECT c FROM ProductEntity c LEFT JOIN FETCH c.prices WHERE c.id = :id AND c.isDeleted = false")
    ProductEntity findByIdWithPrices(@Param("id") Long id);

}
