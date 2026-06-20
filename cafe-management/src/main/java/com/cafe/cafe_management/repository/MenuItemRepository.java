package com.cafe.cafe_management.repository;

import com.cafe.cafe_management.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
