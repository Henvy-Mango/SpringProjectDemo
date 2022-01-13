package com.example.springsecurity.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 开启PreAuthorize,postAuthorize注解
public class SpringCloudSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudSecurityApplication.class, args);
    }

}
