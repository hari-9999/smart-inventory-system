package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

class LoginRequest {
    public String username;
    public String password;
}

class LoginResponse {
    public String token;
    public String username;
    public String role;

    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
}

class RegisterRequest {
    public String username;
    public String password;
    public String email;
    public String role;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ProductRequest {
    private String name;
    private String sku;
    private String description;
    private String category;
    private Double price;
    private Integer quantity;
    private Integer lowStockThreshold;
    private Long supplierId;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class PurchaseOrderRequest {
    private Long productId;
    private Long supplierId;
    private Integer quantity;
    private Double unitPrice;
    private String notes;
}

@Data
@AllArgsConstructor
class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}