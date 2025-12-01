package com.retail.RM_RETAIL.dto;


import lombok.*;

@Data
public class VerifyPaymentRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}

