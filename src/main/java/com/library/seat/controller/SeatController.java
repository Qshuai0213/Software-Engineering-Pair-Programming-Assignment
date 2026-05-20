package com.library.seat.controller;

import com.library.seat.common.ApiResponse;
import com.library.seat.dto.SeatResponse;
import com.library.seat.dto.SeatScheduleResponse;
import com.library.seat.service.SeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/seats")
    public ApiResponse<List<SeatResponse>> getSeats(
            @RequestParam(required = false) String location) {
        List<SeatResponse> seats = seatService.getSeats(location);
        return ApiResponse.success(seats);
    }

    @GetMapping("/seats/{id}")
    public ApiResponse<SeatResponse> getSeatDetail(@PathVariable Long id) {
        SeatResponse seat = seatService.getSeatDetail(id);
        return ApiResponse.success(seat);
    }

    @GetMapping("/seats/{id}/schedule")
    public ApiResponse<List<SeatScheduleResponse>> getSeatSchedule(@PathVariable Long id) {
        List<SeatScheduleResponse> schedule = seatService.getSeatSchedule(id);
        return ApiResponse.success(schedule);
    }
}
