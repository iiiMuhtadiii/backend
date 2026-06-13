package com.market.ecommerce.config;

import com.market.ecommerce.entity.User;
import com.market.ecommerce.entity.UserRole;
import com.market.ecommerce.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // Create a demo user if not exists
        String demoEmail = "demo@example.com";
        if (userRepository.existsByEmail(demoEmail)) return;

        User user = User.builder()
                .name("Demo User")
                .email(demoEmail)
                .password(passwordEncoder.encode("password"))
                .role(UserRole.CUSTOMER)
                .build();

        userRepository.save(user);
    }
}
