package com.emplmgt.employee_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String productName;
    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    @NotEmpty(message = "Prices list cannot be empty")
    private List<pricesDTO> prices;

    @Data
    public static class pricesDTO {
        private Long id;
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be a positive number")
        private double amount;
        @NotBlank(message = "Price description is required")
        @Size(max = 255, message = "Price description must not exceed 255 characters")
        private String description;
    }
}
