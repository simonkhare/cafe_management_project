package com.cafe.cafe_management.repository;

import com.cafe.cafe_management.entity.CafeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Long> {
    List<CafeOrder> findByCustomerUsernameOrderByIdDesc(String username);
    List<CafeOrder> findByStatusOrderByIdDesc(String status);
    List<CafeOrder> findByCustomerUsername(String customerUsername);
}