package com.cafe.cafe_management.service;

import com.cafe.cafe_management.entity.MenuItem;
import com.cafe.cafe_management.repository.MenuItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    @PostConstruct
    public void seedMenu() {
        if (menuItemRepository.count() == 0) {
            log.info("Menu table is empty. Seeding artisan coffee items...");
            menuItemRepository.save(MenuItem.builder().name("Artisan Caramel Latte").description("Espresso with organic steamed milk and house caramel.").price(4.75).category("BEVERAGE").build());
            menuItemRepository.save(MenuItem.builder().name("Butter Flaky Croissant").description("Classic French pastry baked meticulously fresh daily.").price(3.50).category("FOOD").build());
        }
    }

    // Define a localized upload directory on your machine
    private final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images/uploads/";

    public void addMenuItemWithImage(MenuItem menuItem, MultipartFile imageFile) throws IOException {

        // BRING IT HOME: Creates a 'cafe-uploads' folder right inside your main project root directory
        Path uploadDirectoryPath = Paths.get(".", "cafe-uploads").toAbsolutePath().normalize();

        if (imageFile != null && !imageFile.isEmpty()) {
            log.info("🔥 FILE DETECTED! Size: {} bytes", imageFile.getSize());

            if (!Files.exists(uploadDirectoryPath)) {
                Files.createDirectories(uploadDirectoryPath);
                log.info("📁 Created storage folder at project root: {}", uploadDirectoryPath);
            }

            String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path targetPath = uploadDirectoryPath.resolve(uniqueFileName);

            java.nio.file.Files.copy(imageFile.getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            log.info("✅ SUCCESS! Written to project root directory.");

            menuItem.setImageName(uniqueFileName);
        } else {
            log.warn("⚠️ NO FILE DETECTED! Falling back to default placeholder.");
            menuItem.setImageName("default-coffee.jpg");
        }

        menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public MenuItem addMenuItem(MenuItem item) {
        log.info("Adding new item to menu: {}", item.getName());
        return menuItemRepository.save(item);
    }

    public void deleteMenuItem(Long id) {
        log.info("Removing item ID {} from menu", id);
        menuItemRepository.deleteById(id);
    }
}
