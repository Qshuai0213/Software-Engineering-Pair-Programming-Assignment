package com.library.seat.controller;

import com.library.seat.common.ApiResponse;
import com.library.seat.dto.CreateReservationRequest;
import com.library.seat.dto.ReservationResponse;
import com.library.seat.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ApiResponse<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ReservationResponse response = reservationService.createReservation(userId, request);
        return ApiResponse.success(response);
    }

    @GetMapping("/reservations/my")
    public ApiResponse<List<ReservationResponse>> getMyReservations(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        List<ReservationResponse> list = reservationService.getMyReservations(userId);
        return ApiResponse.success(list);
    }

    @DeleteMapping("/reservations/{id}")
    public ApiResponse<Void> cancelReservation(@PathVariable Long id,
                                               HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        reservationService.cancelReservation(userId, id);
        return ApiResponse.success();
    }

    @PostMapping("/reservations/{id}/check-in")
    public ApiResponse<Void> checkIn(@PathVariable Long id,
                                     HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        reservationService.checkIn(userId, id);
        return ApiResponse.success();
    }
}
