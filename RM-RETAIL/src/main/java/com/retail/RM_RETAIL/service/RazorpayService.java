package com.retail.RM_RETAIL.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    private RazorpayClient client;

    public RazorpayService() throws Exception {
        this.client = new RazorpayClient("YOUR_KEY_ID", "YOUR_KEY_SECRET");
    }

    public Order createOrder(double amount) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("amount", (int)(amount * 100)); // convert to paisa
        obj.put("currency", "INR");
        obj.put("payment_capture", 1);
        return client.orders.create(obj);
    }
}
