package com.inventory.controller;  // This is correct - matches your folder structure
import com.inventory.model.Product;
import com.inventory.model.PurchaseOrder;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.PurchaseOrderRepository;
import com.inventory.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;  // FIXED: Changed from ReducedArgsConstructor
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor  // This will now work
@CrossOrigin(origins = "*")
public class DashboardController {

    // Add your fields here that need to be injected
    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;

    // @RequiredArgsConstructor will automatically create a constructor
    // with all final fields (like the repositories above)

    // Add your API methods here
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Dashboard is working!");
    }
}