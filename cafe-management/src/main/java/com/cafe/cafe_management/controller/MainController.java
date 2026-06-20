package com.cafe.cafe_management.controller;

import com.cafe.cafe_management.entity.MenuItem;
import com.cafe.cafe_management.entity.User;
import com.cafe.cafe_management.service.MenuService;
import com.cafe.cafe_management.service.OrderService;
import com.cafe.cafe_management.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    private final MenuService menuService;
    private final OrderService orderService;

    @GetMapping("/")
    public String index() { return "redirect:/login/customer"; }

    @GetMapping("/login/customer")
    public String customerLoginView() { return "login-customer"; }

    @GetMapping("/login/employee")
    public String employeeLoginView() { return "login-employee"; }

    // NEW: Custom Admin Access Portal Mapping
    @GetMapping("/login/admin")
    public String adminLoginView() { return "login-admin"; }

    @GetMapping("/register")
    public String registerView() { return "register"; }

    @PostMapping("/register")
    public String handleRegistration(@RequestParam String username, @RequestParam String password, Model model) {
        if (userService.registerCustomer(username, password)) {
            model.addAttribute("success", "Account created successfully! Please log in.");
            return "login-customer";
        }
        model.addAttribute("error", "Username is already taken.");
        return "register";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password, @RequestParam String expectedRole, HttpSession session, Model model) {
        return userService.authenticate(username, password, expectedRole)
                .map(user -> {
                    session.setAttribute("user", user);
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid credentials or unauthorized role.");
                    if ("ADMIN".equalsIgnoreCase(expectedRole)) return "login-admin";
                    return expectedRole.equalsIgnoreCase("EMPLOYEE") ? "login-employee" : "login-customer";
                });
    }

    // 1. Locate your main dashboard rendering method
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // ... (Your existing code to fetch the logged-in user from session)
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        // Pass the user context to the frontend navbar
        model.addAttribute("user", user);

        // 2. TARGET ZONE: Find your role-checking logic split matrix
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            model.addAttribute("allUsers", userService.getAllUsers());
            model.addAttribute("allOrders", orderService.getAllOrdersGlobal());
            model.addAttribute("revenue", orderService.calculateTotalRevenue());
            model.addAttribute("menuItems", menuService.getAllMenuItems());
        }
        else if ("EMPLOYEE".equalsIgnoreCase(user.getRole()) || "employee".equalsIgnoreCase(user.getRole())) {
            // Your existing catalog and pending queue lines
            model.addAttribute("menuItems", menuService.getAllMenuItems());
            model.addAttribute("pendingOrders", orderService.getPendingOrders());

            // 📍 PLACE THE EXACT NEW CHANGE HERE:
            // This exposes the global orders database pool to the employee Thymeleaf context
            model.addAttribute("allOrders", orderService.getAllOrdersGlobal());
        }
        else if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            model.addAttribute("menuItems", menuService.getAllMenuItems());
            model.addAttribute("myOrders", orderService.getOrdersByCustomer(user.getUsername()));
        }

        return "dashboard"; // Renders dashboard.html template view
    }
    @PostMapping("/customer/order/place")
    public String handlePlaceOrder(@RequestParam String itemsSummary,
                                   @RequestParam Double totalAmount,
                                   HttpSession session) {

        // 1. Pull the authenticated user context directly from the session
        User user = (User) session.getAttribute("user");

        // 2. Structural Guard: Ensure only registered CUSTOMERS can dispatch tickets
        if (user != null && "CUSTOMER".equalsIgnoreCase(user.getRole())) {

            // 3. Delegate the database save operations entirely to the service layer
            orderService.placeOrder(user.getUsername(), itemsSummary, totalAmount);
        }

        // 4. Force a clean page reload to refresh the customer's historical receipt cards
        return "redirect:/dashboard";
    }
    // ================= NEW ADMIN EXCLUSIVE POST CONTROLS =================

    @PostMapping("/admin/employee/add")
    public String adminAddEmployee(@RequestParam String username, @RequestParam String password, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            userService.registerEmployee(username, password);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/user/delete")
    public String adminDeleteUser(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            userService.removeUser(id);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/order/cancel")
    public String adminCancelOrder(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            orderService.cancelOrder(id);
        }
        return "redirect:/dashboard";
    }

    // ================= CORE MENU MANAGEMENT ENDPOINTS =================

    @PostMapping("/employee/menu/add")
    public String addMenuItem(@ModelAttribute MenuItem menuItem,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user != null && ("EMPLOYEE".equalsIgnoreCase(user.getRole()) || "ADMIN".equalsIgnoreCase(user.getRole()))) {
            try {
                // Forward execution downstream into the updated service layer pipeline
                menuService.addMenuItemWithImage(menuItem, imageFile);
            } catch (IOException e) {
                // Catch disk write faults gracefully using our logger
                System.err.println("Fatal: Disk write operation failed: " + e.getMessage());
            }
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/employee/menu/delete")
    public String deleteMenuItem(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && ("EMPLOYEE".equalsIgnoreCase(user.getRole()) || "ADMIN".equalsIgnoreCase(user.getRole()))) {
            menuService.deleteMenuItem(id);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/employee/order/complete")
    public String handleCompleteOrder(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && "EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            orderService.completeOrder(id);
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login/customer";
    }
}