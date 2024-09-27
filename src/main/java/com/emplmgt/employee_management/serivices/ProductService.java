package com.emplmgt.employee_management.serivices;

import com.emplmgt.employee_management.dto.ProductDTO;
import com.emplmgt.employee_management.entities.ProductEntity;
import com.emplmgt.employee_management.entities.ProductPricesEntity;
import com.emplmgt.employee_management.repositories.ProductPriceRepository;
import com.emplmgt.employee_management.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;

    public ResponseEntity<?> addProduct(ProductDTO productDTO) {
        if (productRepository.existsById(productDTO.getId())) {
            return ResponseEntity.badRequest().body("Product already available");
        }

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName(productDTO.getProductName());
        productEntity.setDescription(productDTO.getDescription());

        ProductEntity savedProduct = productRepository.save(productEntity);

        List<ProductPricesEntity> priceEntities = productDTO.getPrices().stream()
                .map(pricesDTO -> {
                    ProductPricesEntity priceEntity = new ProductPricesEntity();
                    priceEntity.setAmount(pricesDTO.getAmount());
                    priceEntity.setDescription(pricesDTO.getDescription());
                    priceEntity.setProductEntity(savedProduct);
                    return priceEntity;
                })
                .collect(Collectors.toList());

        productPriceRepository.saveAll(priceEntities);

        return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully");
    }

    @Transactional
    public ResponseEntity<?> updateProduct(ProductDTO productDTO) {
        Optional<ProductEntity> optionalProduct = productRepository.findById(productDTO.getId());

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        ProductEntity productEntity = optionalProduct.get();
        productEntity.setProductName(productDTO.getProductName());
        productEntity.setDescription(productDTO.getDescription());

        productRepository.save(productEntity);

        productPriceRepository.deleteAllByProductEntity(productEntity);

        List<ProductPricesEntity> priceEntities = productDTO.getPrices().stream()
                .map(pricesDTO -> {
                    ProductPricesEntity priceEntity = new ProductPricesEntity();
                    priceEntity.setAmount(pricesDTO.getAmount());
                    priceEntity.setDescription(pricesDTO.getDescription());
                    priceEntity.setProductEntity(productEntity);
                    return priceEntity;
                })
                .collect(Collectors.toList());

        productPriceRepository.saveAll(priceEntities);

        return ResponseEntity.ok("Product updated successfully");
    }

    public ResponseEntity<?> deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setDeleted(true);
                    productRepository.save(product);
                    return ResponseEntity.ok("Product deleted successfully");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product not found"));
    }

    public ResponseEntity<?> getAllProducts() {
        List<ProductEntity> products = productRepository.findAllProductsWithPrices();
        return ResponseEntity.ok(products);
    }

    public ResponseEntity<?> getProduct(Long id) {
        ProductEntity product = productRepository.findByIdWithPrices(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No product found.");
        }
        return ResponseEntity.ok(product);
    }

}
