package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.model.PurchaseOrder;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.PurchaseOrderRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;  // CORRECT - lowercase 'l'
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    /**
     * Generate Inventory Report as PDF
     */
    public byte[] generateInventoryPdf() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Smart Inventory Management System", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY);
        Paragraph sub = new Paragraph("Inventory Report – Generated: " + java.time.LocalDate.now(), subFont);
        sub.setAlignment(Element.ALIGN_CENTER);
        document.add(sub);
        document.add(Chunk.NEWLINE);

        // Table
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 3f, 2f, 1.5f, 2f, 2f});

        // Header row
        String[] headers = {"ID", "Product Name", "SKU", "Quantity", "Price (₹)", "Status"};
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new BaseColor(30, 78, 121));
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Data rows
        List<Product> products = productRepository.findAll();
        Font rowFont = new Font(Font.FontFamily.HELVETICA, 10);
        for (Product p : products) {
            table.addCell(new PdfPCell(new Phrase(String.valueOf(p.getId()), rowFont)));
            table.addCell(new PdfPCell(new Phrase(p.getName(), rowFont)));
            table.addCell(new PdfPCell(new Phrase(p.getSku(), rowFont)));

            // Highlight low stock in red
            PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(p.getQuantity()), rowFont));
            if (p.isLowStock()) qtyCell.setBackgroundColor(new BaseColor(255, 200, 200));
            table.addCell(qtyCell);

            table.addCell(new PdfPCell(new Phrase("₹" + p.getPrice(), rowFont)));
            String status = p.isLowStock() ? "LOW STOCK" : "OK";
            PdfPCell statusCell = new PdfPCell(new Phrase(status, rowFont));
            if (p.isLowStock()) statusCell.setBackgroundColor(new BaseColor(255, 150, 150));
            table.addCell(statusCell);
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Summary
        Font summaryFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        long lowStockCount = products.stream().filter(Product::isLowStock).count();
        document.add(new Paragraph("Total Products: " + products.size(), summaryFont));
        document.add(new Paragraph("Low Stock Items: " + lowStockCount, summaryFont));

        document.close();
        return out.toByteArray();
    }

    /**
     * Generate Inventory Report as CSV
     */
    public byte[] generateInventoryCsv() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("ID", "Product Name", "SKU", "Category", "Quantity", "Price", "Low Stock Threshold", "Status"));

        List<Product> products = productRepository.findAll();
        for (Product p : products) {
            printer.printRecord(
                    p.getId(),
                    p.getName(),
                    p.getSku(),
                    p.getCategory(),
                    p.getQuantity(),
                    p.getPrice(),
                    p.getLowStockThreshold(),
                    p.isLowStock() ? "LOW STOCK" : "OK"
            );
        }
        printer.flush();
        return out.toByteArray();
    }

    /**
     * Generate Purchase Orders Report as CSV
     */
    public byte[] generatePurchaseOrderCsv() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Order No", "Product", "Supplier", "Quantity", "Unit Price", "Total Amount", "Status", "Order Date"));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
        for (PurchaseOrder o : orders) {
            printer.printRecord(
                    o.getOrderNumber(),
                    o.getProduct().getName(),
                    o.getSupplier().getName(),
                    o.getQuantity(),
                    o.getUnitPrice(),
                    o.getTotalAmount(),
                    o.getStatus(),
                    o.getOrderDate() != null ? o.getOrderDate().format(fmt) : ""
            );
        }
        printer.flush();
        return out.toByteArray();
    }
}
