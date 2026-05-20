package com.library.seat.controller;

import com.library.seat.common.ApiResponse;
import com.library.seat.dto.AdminReservationResponse;
import com.library.seat.dto.AdminStatisticsResponse;
import com.library.seat.dto.UserResponse;
import com.library.seat.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/reservations")
    public ApiResponse<List<AdminReservationResponse>> getReservations(
            HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        List<AdminReservationResponse> reservations = adminService.getAllReservations(role);
        return ApiResponse.success(reservations);
    }

    @GetMapping("/statistics")
    public ApiResponse<AdminStatisticsResponse> getStatistics(
            HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        AdminStatisticsResponse statistics = adminService.getStatistics(role);
        return ApiResponse.success(statistics);
    }

    @PutMapping("/users/{id}/block")
    public ApiResponse<Void> blockUser(@PathVariable Long id,
                                       HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        adminService.blockUser(role, id);
        return ApiResponse.success();
    }

    @PutMapping("/users/{id}/unblock")
    public ApiResponse<Void> unblockUser(@PathVariable Long id,
                                         HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        adminService.unblockUser(role, id);
        return ApiResponse.success();
    }

    @GetMapping("/users/blocked")
    public ApiResponse<List<UserResponse>> getBlockedUsers(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        List<UserResponse> users = adminService.getBlockedUsers(role);
        return ApiResponse.success(users);
    }

    @GetMapping("/users")
    public ApiResponse<List<UserResponse>> getAllUsers(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        List<UserResponse> users = adminService.getAllUsers(role);
        return ApiResponse.success(users);
    }
}
