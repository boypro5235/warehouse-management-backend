package com.example.WebQlyKho.config;

import com.example.WebQlyKho.entity.User;
import com.example.WebQlyKho.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class AppIniConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if(userRepository.findByUsername("admin") == null){
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .status(true)
                        .role("ADMIN")
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it!");
            }

        };
    }
}
