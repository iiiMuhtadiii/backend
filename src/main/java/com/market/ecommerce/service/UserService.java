package com.market.ecommerce.service;

import com.market.ecommerce.entity.User;
import com.market.ecommerce.exception.ResourceNotFoundException;
import com.market.ecommerce.repository.UserRepository;
import com.market.ecommerce.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // جلب معلومات حساب المستخدم الحالي المسجل بالنظام
    public User getCurrentUserProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("لم يتم العثور على حساب مستخدم بهذا البريد"));
    }

    // استدعاء جميع المستخدمين (لصلاحية لوحة الإدارة فقط)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}