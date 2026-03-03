package com.shortlink;

import com.shortlink.entity.UserEntity;
import com.shortlink.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShortLinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkApplication.class, args);
    }

    @Bean
    ApplicationRunner init(UserRepository userRepository) {
        return args -> {
            // 检查是否已有用户
            if (userRepository.findByUsername("admin") == null) {
                // 添加默认用户
                UserEntity user = new UserEntity();
                user.setUsername("admin");
                user.setPassword("admin");
                userRepository.save(user);
                System.out.println("默认用户添加成功: admin/admin");
            }
        };
    }
}