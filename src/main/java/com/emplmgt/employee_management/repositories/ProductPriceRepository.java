package com.emplmgt.employee_management.repositories;

import com.emplmgt.employee_management.entities.ProductEntity;
import com.emplmgt.employee_management.entities.ProductPricesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPriceRepository extends JpaRepository<ProductPricesEntity, Long> {
    void deleteAllByProductEntity(ProductEntity productEntity);
}
