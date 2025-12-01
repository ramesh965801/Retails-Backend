package com.retail.RM_RETAIL.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.retail.RM_RETAIL.entity.Order;
import com.retail.RM_RETAIL.entity.OrderItem;
import com.retail.RM_RETAIL.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ FIXED: Place Order (NO 404)
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestBody Order order){
        if(order.getOrderDate() == null)
            order.setOrderDate(java.time.LocalDate.now());

        if(order.getOrderTime() == null)
            order.setOrderTime(java.time.LocalTime.now());

        Order saved = orderService.saveOrder(order);
        return ResponseEntity.ok(saved);
    }

    // ✅ Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ✅ PDF Bill
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> downloadBill(@PathVariable Long id) {

        Order order = orderService.getOrderById(id);
        if (order == null) return ResponseEntity.notFound().build();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("RM-BANK RETAIL STORE"));
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("Customer: " + order.getCustomerName()));
            document.add(new Paragraph("Mobile: " + order.getMobileNumber()));
            document.add(new Paragraph("Cashier: " + order.getCashierName()));
            document.add(new Paragraph("Payment Mode: " + order.getPaymentMode()));
            document.add(new Paragraph("Date: " + order.getOrderDate()));
            document.add(new Paragraph("Time: " + order.getOrderTime()));
            document.add(new Paragraph("\nItems:"));

            for (OrderItem item : order.getOrderItems()) {
                document.add(new Paragraph(
                        item.getProductName() + " x " + item.getQuantity() +
                                " = ₹" + (item.getQuantity() * item.getPrice())
                ));
            }

            document.add(new Paragraph("\nSubtotal: ₹" + order.getSubtotal()));
            document.add(new Paragraph("Tax: ₹" + order.getTax()));
            document.add(new Paragraph("Total: ₹" + order.getTotalAmount()));

            document.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> map = new HashMap<>();
        LocalDate today = LocalDate.now();

        map.put("todaySales", orderService.getTodaySales(today));
        map.put("todayOrdersCount", orderService.getTodayOrdersCount(today));
        map.put("recentOrders", orderService.getRecentOrders());

        return ResponseEntity.ok(map);
    }

    // ✅ Test
    @GetMapping("/test")
    public String test() {
        return "Orders API Working ✅";
    }
}
