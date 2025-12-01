package com.retail.RM_RETAIL.utils;

import java.util.Random;

public class OrderIdGenerator {

    public static String generate() {
        Random random = new Random();
        int num = 1000 + random.nextInt(9000); // 1000 - 9999
        return "ORD" + num;
    }
}
