package com.retail.RM_RETAIL.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.retail.RM_RETAIL.repository.OrderRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment/razorpay")
@CrossOrigin(origins = "http://localhost:3000")
public class RazorpayController {

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    private final OrderRepository orderRepository;

    public RazorpayController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // ✅ CREATE ORDER
   @PostMapping("/create")
public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {

    try {
        System.out.println("Incoming data: " + data);

        double amount = Double.parseDouble(data.get("amount").toString());
        String cashierName = data.getOrDefault("cashierName", "Admin").toString();
        String customerName = data.getOrDefault("customerName", "Guest").toString();
        String mobile = data.getOrDefault("mobileNumber", "NA").toString();

        long amountInPaise = (long) (amount * 100);

        RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order razorpayOrder = client.orders.create(orderRequest);

        String systemOrderId = "SYS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        com.retail.RM_RETAIL.entity.Order dbOrder =
                new com.retail.RM_RETAIL.entity.Order();

        dbOrder.setSystemOrderId(systemOrderId);
        dbOrder.setCashierName(cashierName);
        dbOrder.setCustomerName(customerName);
        dbOrder.setMobileNumber(mobile);
        dbOrder.setPaymentMode("RAZORPAY");
        dbOrder.setSubtotal(amount);
        dbOrder.setTotalAmount(amount);
        dbOrder.setTax(0);
        dbOrder.setOrderDate(LocalDate.now());
        dbOrder.setOrderTime(LocalTime.now());
        dbOrder.setPaymentStatus("PENDING");

        com.retail.RM_RETAIL.entity.Order savedOrder =
                orderRepository.save(dbOrder);

        Map<String, Object> response = new HashMap<>();
        response.put("dbOrderId", savedOrder.getId());
        response.put("systemOrderId", systemOrderId);
        response.put("razorpayOrderId", razorpayOrder.get("id"));
        response.put("amount", razorpayOrder.get("amount"));
        response.put("currency", razorpayOrder.get("currency"));
        response.put("key", razorpayKey);
        response.put("cashierName", cashierName);

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(e.getMessage());
    }
}


    // ✅ VERIFY PAYMENT
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> data) {

        try {

            Long dbOrderId = Long.parseLong(data.get("dbOrderId").toString());
            String paymentStatus = data.get("status").toString();

            com.retail.RM_RETAIL.entity.Order order =
                    orderRepository.findById(dbOrderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if ("success".equalsIgnoreCase(paymentStatus)) {
                order.setPaymentStatus("SUCCESS");
            } else {
                order.setPaymentStatus("FAILED");
            }

            orderRepository.save(order);

            return ResponseEntity.ok(order);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Verification failed : " + e.getMessage());
        }
    }

    // ✅ RECEIPT API
    @GetMapping("/receipt/{id}")
    public ResponseEntity<?> getReceipt(@PathVariable Long id) {

        com.retail.RM_RETAIL.entity.Order order =
                orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Map<String, Object> receipt = new HashMap<>();

        receipt.put("dbId", order.getId());                       
        receipt.put("systemOrderId", order.getSystemOrderId());  
        receipt.put("cashierName", order.getCashierName());       
        receipt.put("customerName", order.getCustomerName());
        receipt.put("totalAmount", order.getTotalAmount());
        receipt.put("paymentStatus", order.getPaymentStatus());

        // ✅ SAFE COLOR LOGIC
        receipt.put("statusColor",
                "SUCCESS".equalsIgnoreCase(order.getPaymentStatus())
                        ? "green" : "red"
        );

        receipt.put("orderDate", order.getOrderDate());
        receipt.put("orderTime", order.getOrderTime());

        return ResponseEntity.ok(receipt);
    }

}
