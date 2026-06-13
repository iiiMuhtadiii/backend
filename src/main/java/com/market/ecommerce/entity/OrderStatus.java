package com.market.ecommerce.entity;

public enum OrderStatus {
    NEW,
    PROCESSING, // جاري المعالجة
    SHIPPED,    // تم الشحن
    COMPLETED,  // مكتمل
    CANCELLED   // ملغى
}