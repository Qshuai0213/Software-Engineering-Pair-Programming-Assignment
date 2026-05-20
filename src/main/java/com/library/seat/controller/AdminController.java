package com.library.seat.controller;

import com.library.seat.common.ApiResponse;
import com.library.seat.dto.AdminReservationResponse;
import com.library.seat.dto.AdminStatisticsResponse;
import com.library.seat.service.AdminService;
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
            @RequestParam Long userId) {
        List<AdminReservationResponse> reservations = adminService.getAllReservations(userId);
        return ApiResponse.success(reservations);
    }

    @GetMapping("/statistics")
    public ApiResponse<AdminStatisticsResponse> getStatistics(
            @RequestParam Long userId) {
        AdminStatisticsResponse statistics = adminService.getStatistics(userId);
        return ApiResponse.success(statistics);
    }
}
