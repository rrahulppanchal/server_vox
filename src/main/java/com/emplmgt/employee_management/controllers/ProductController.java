package com.emplmgt.employee_management.controllers;

import com.emplmgt.employee_management.dto.ProductDTO;
import com.emplmgt.employee_management.serivices.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/v3/product")
@Validated //
public class ProductController {

    private final ProductService productService;

    @GetMapping(path = "/get-all")
    public ResponseEntity<?> getAllProducts() {
        return handleRequest(productService::getAllProducts);
    }

    @GetMapping(path = "/get/{id}")
    public ResponseEntity<?> getProduct(@PathVariable("id") Long id) {
        return handleRequest(() -> productService.getProduct(id));
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        return handleRequest(() -> productService.deleteProduct(id));
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        return handleRequest(() -> productService.addProduct(productDTO));
    }

    @PutMapping(path = "/update")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductDTO productDTO) {
        return handleRequest(() -> productService.updateProduct(productDTO));
    }

    private ResponseEntity<?> handleRequest(Supplier<ResponseEntity<?>> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

}
