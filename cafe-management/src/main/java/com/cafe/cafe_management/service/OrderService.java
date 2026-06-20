package com.cafe.cafe_management.service;

import com.cafe.cafe_management.entity.CafeOrder;
import com.cafe.cafe_management.repository.CafeOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final CafeOrderRepository cafeOrderRepository;

    public List<CafeOrder> getOrdersByCustomer(String username) {
        return cafeOrderRepository.findByCustomerUsername(username);
    }

    public double calculateTotalRevenue() {
        return cafeOrderRepository.findAll().stream()
                .filter(order -> "COMPLETED".equalsIgnoreCase(order.getStatus()))
                .mapToDouble(CafeOrder::getTotalAmount)
                .sum();
    }
    public CafeOrder placeOrder(String username, String itemsSummary, Double totalAmount) {
        return cafeOrderRepository.save(CafeOrder.builder()
                .customerUsername(username)
                .itemsSummary(itemsSummary)
                .totalAmount(totalAmount)
                .status("PENDING")
                .build());
    }

    public List<CafeOrder> getPendingOrders() {
        return cafeOrderRepository.findByStatusOrderByIdDesc("PENDING");
    }

    public List<CafeOrder> getCustomerOrderHistory(String username) {
        return cafeOrderRepository.findByCustomerUsernameOrderByIdDesc(username);
    }

    public void completeOrder(Long orderId) {
        cafeOrderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus("COMPLETED");
            cafeOrderRepository.save(order);
        });
    }

    // NEW: Admin master control to pull every order regardless of status
    public List<CafeOrder> getAllOrdersGlobal() {
        return cafeOrderRepository.findAll();
    }

    // NEW: Administrative cancellation override tool
    public void cancelOrder(Long orderId) {
        log.warn("Admin override executed: Cancelling Order ID {}", orderId);
        cafeOrderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus("CANCELLED");
            cafeOrderRepository.save(order);
        });
    }
}