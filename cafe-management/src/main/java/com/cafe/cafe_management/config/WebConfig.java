package com.cafe.cafe_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Point directly to the new root directory location
        Path uploadDir = Paths.get(".", "cafe-uploads").toAbsolutePath().normalize();
        String uploadPath = uploadDir.toUri().toString();

        System.out.println("🚀 FORWARDING TRAFFIC TO: " + uploadPath);

        registry.addResourceHandler("/images/uploads/**")
                .addResourceLocations(uploadPath);
    }
}