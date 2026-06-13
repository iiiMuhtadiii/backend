package com.market.ecommerce.service;

import com.market.ecommerce.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    // يمكنك لاحقاً دمج spring-boot-starter-mail هنا لإرسال بريد حقيقي
    public void sendOrderConfirmationEmail(Order order) {
        String email = order.getUser().getEmail();
        log.info("تم تأكيد الطلب رقم: {} - جاري إرسال إشعار إلى البريد: {}", order.getId(), email);

        // TODO: تنفيذ كود إرسال البريد الإلكتروني الفعلي هنا
    }
}