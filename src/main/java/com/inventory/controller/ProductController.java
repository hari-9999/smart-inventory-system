package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    /** GET /api/products - Get all products */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /** GET /api/products/{id} - Get product by ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/products/search?name=xyz - Search products by name */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    /** GET /api/products/category/{category} - Filter by category */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    /** GET /api/products/low-stock - Get all low stock products */
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockProducts() {
        List<Product> lowStock = productService.getLowStockProducts();
        return ResponseEntity.ok(Map.of(
                "count", lowStock.size(),
                "products", lowStock,
                "alert", lowStock.isEmpty() ? "All stock levels are healthy" : "Action required!"
        ));
    }

    /** POST /api/products - Create new product (Admin only) */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> request) {
        try {
            Product product = new Product();
            product.setName((String) request.get("name"));
            product.setSku((String) request.get("sku"));
            product.setDescription((String) request.get("description"));
            product.setCategory((String) request.get("category"));
            product.setPrice(Double.valueOf(request.get("price").toString()));
            product.setQuantity(Integer.valueOf(request.get("quantity").toString()));
            product.setLowStockThreshold(
                    request.get("lowStockThreshold") != null
                    ? Integer.valueOf(request.get("lowStockThreshold").toString()) : 10);

            Long supplierId = request.get("supplierId") != null
                    ? Long.valueOf(request.get("supplierId").toString()) : null;

            Product saved = productService.createProduct(product, supplierId);
            return ResponseEntity.ok(Map.of("message", "Product created successfully", "product", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/products/{id} - Update product (Admin only) */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updated = productService.updateProduct(id, product);
            return ResponseEntity.ok(Map.of("message", "Product updated successfully", "product", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/products/{id}/stock?change=50 - Update stock quantity */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestParam int change) {
        try {
            productService.updateStock(id, change);
            return ResponseEntity.ok(Map.of("message", "Stock updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/products/{id} - Delete product (Admin only) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
