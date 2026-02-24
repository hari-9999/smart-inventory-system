package com.inventory.controller;

import com.inventory.model.PurchaseOrder;
import com.inventory.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    /** GET /api/purchase-orders */
    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllOrders());
    }

    /** GET /api/purchase-orders/status/{status} */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrder>> getOrdersByStatus(@PathVariable String status) {
        PurchaseOrder.OrderStatus orderStatus = PurchaseOrder.OrderStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(purchaseOrderService.getOrdersByStatus(orderStatus));
    }

    /** POST /api/purchase-orders - Create order (Admin only) */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> req) {
        try {
            PurchaseOrder order = purchaseOrderService.createOrder(
                    Long.valueOf(req.get("productId").toString()),
                    Long.valueOf(req.get("supplierId").toString()),
                    Integer.valueOf(req.get("quantity").toString()),
                    Double.valueOf(req.get("unitPrice").toString()),
                    (String) req.get("notes")
            );
            return ResponseEntity.ok(Map.of("message", "Purchase order created", "order", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PATCH /api/purchase-orders/{id}/status - Update order status */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            PurchaseOrder.OrderStatus newStatus = PurchaseOrder.OrderStatus.valueOf(status.toUpperCase());
            PurchaseOrder updated = purchaseOrderService.updateOrderStatus(id, newStatus);
            return ResponseEntity.ok(Map.of("message", "Order status updated to " + status, "order", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
