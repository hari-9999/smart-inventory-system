package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.model.PurchaseOrder;
import com.inventory.model.Supplier;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.PurchaseOrderRepository;
import com.inventory.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }

    public List<PurchaseOrder> getOrdersByStatus(PurchaseOrder.OrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    @Transactional
    public PurchaseOrder createOrder(Long productId, Long supplierId, Integer quantity, Double unitPrice, String notes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNumber("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setProduct(product);
        order.setSupplier(supplier);
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(unitPrice * quantity);
        order.setStatus(PurchaseOrder.OrderStatus.PENDING);
        order.setNotes(notes);

        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder updateOrderStatus(Long orderId, PurchaseOrder.OrderStatus newStatus) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        // If delivered, update stock
        if (newStatus == PurchaseOrder.OrderStatus.DELIVERED) {
            Product product = order.getProduct();
            product.setQuantity(product.getQuantity() + order.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(newStatus);
        return purchaseOrderRepository.save(order);
    }

    /**
     * Auto-generate purchase orders for low-stock products every 6 hours
     */
    @Scheduled(fixedRate = 21600000)
    @Transactional
    public void autoGeneratePurchaseOrdersForLowStock() {
        List<Product> lowStockProducts = productRepository.findLowStockProducts();

        for (Product product : lowStockProducts) {
            if (product.getSupplier() == null) continue;

            // Check if a pending order already exists for this product
            boolean pendingExists = purchaseOrderRepository
                    .findByProductId(product.getId())
                    .stream()
                    .anyMatch(o -> o.getStatus() == PurchaseOrder.OrderStatus.PENDING);

            if (!pendingExists) {
                int reorderQty = product.getLowStockThreshold() * 3; // Order 3x threshold
                PurchaseOrder auto = new PurchaseOrder();
                auto.setOrderNumber("AUTO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                auto.setProduct(product);
                auto.setSupplier(product.getSupplier());
                auto.setQuantity(reorderQty);
                auto.setUnitPrice(product.getPrice());
                auto.setTotalAmount(product.getPrice() * reorderQty);
                auto.setStatus(PurchaseOrder.OrderStatus.PENDING);
                auto.setNotes("Auto-generated: low stock alert for " + product.getName());

                purchaseOrderRepository.save(auto);
                System.out.println("[LOW STOCK ALERT] Auto PO created for: " + product.getName()
                        + " | Current Stock: " + product.getQuantity()
                        + " | Ordered: " + reorderQty + " units");
            }
        }
    }
}
