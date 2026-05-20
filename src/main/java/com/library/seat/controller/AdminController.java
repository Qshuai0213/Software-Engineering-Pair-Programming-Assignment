package com.library.seat.controller;

import com.library.seat.common.ApiResponse;
import com.library.seat.dto.AdminReservationResponse;
import com.library.seat.dto.AdminStatisticsResponse;
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
}
