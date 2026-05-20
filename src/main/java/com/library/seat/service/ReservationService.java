package com.library.seat.service;

import com.library.seat.common.BusinessException;
import com.library.seat.common.ErrorCode;
import com.library.seat.dto.CreateReservationRequest;
import com.library.seat.dto.ReservationResponse;
import com.library.seat.entity.Reservation;
import com.library.seat.entity.Seat;
import com.library.seat.mapper.ReservationMapper;
import com.library.seat.mapper.SeatMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {

    private static final LocalTime OPEN_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(21, 0);
    private static final long MIN_HOURS = 1;

    private final ReservationMapper reservationMapper;
    private final SeatMapper seatMapper;

    public ReservationService(ReservationMapper reservationMapper, SeatMapper seatMapper) {
        this.reservationMapper = reservationMapper;
        this.seatMapper = seatMapper;
    }

    public ReservationResponse createReservation(Long userId, CreateReservationRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        // 不能预约过去时间
        if (startTime.isBefore(now)) {
            throw new BusinessException(ErrorCode.START_TIME_IN_PAST);
        }

        // 单次预约不少于1小时
        if (Duration.between(startTime, endTime).toHours() < MIN_HOURS) {
            throw new BusinessException(ErrorCode.DURATION_TOO_SHORT);
        }

        // 开放时间校验：08:00-21:00
        if (!isOpenTime(startTime) || !isOpenTime(endTime)) {
            throw new BusinessException(ErrorCode.TIME_OUT_OF_RANGE);
        }

        // 预约范围：当天当前时间开始，至次日21:00结束
        LocalDateTime maxEnd = LocalDate.now().plusDays(1).atTime(CLOSE_TIME);
        if (endTime.isAfter(maxEnd)) {
            throw new BusinessException(ErrorCode.TIME_OUT_OF_RANGE);
        }

        // 座位是否存在
        Seat seat = seatMapper.findById(request.getSeatId());
        if (seat == null) {
            throw new BusinessException(ErrorCode.SEAT_NOT_FOUND);
        }

        // 同一用户只能有一个有效预约
        List<Reservation> activeList = reservationMapper.findActiveByUserId(userId, now);
        if (!activeList.isEmpty()) {
            throw new BusinessException(ErrorCode.RESERVATION_CONFLICT);
        }

        // 时间冲突判断
        List<Reservation> conflicts = reservationMapper.findConflicting(
                request.getSeatId(), startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new BusinessException(ErrorCode.RESERVATION_CONFLICT);
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSeatId(request.getSeatId());
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus("reserved");
        reservationMapper.insert(reservation);

        return toResponse(reservation, seat);
    }

    public List<ReservationResponse> getMyReservations(Long userId) {
        List<Reservation> reservations = reservationMapper.findByUserId(userId);
        List<ReservationResponse> result = new ArrayList<>();
        for (Reservation r : reservations) {
            Seat seat = seatMapper.findById(r.getSeatId());
            result.add(toResponse(r, seat));
        }
        return result;
    }

    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationMapper.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_OWN_RESERVATION);
        }
        if (!"reserved".equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        reservationMapper.updateStatus(reservationId, "cancelled");
    }

    public void checkIn(Long userId, Long reservationId) {
        Reservation reservation = reservationMapper.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_OWN_RESERVATION);
        }
        if (!"reserved".equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(reservation.getStartTime()) || now.isAfter(reservation.getEndTime())) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        reservationMapper.updateStatus(reservationId, "checked_in");
    }

    private boolean isOpenTime(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        return !time.isBefore(OPEN_TIME) && !time.isAfter(CLOSE_TIME);
    }

    private ReservationResponse toResponse(Reservation reservation, Seat seat) {
        ReservationResponse resp = new ReservationResponse();
        resp.setId(reservation.getId());
        resp.setSeatId(reservation.getSeatId());
        resp.setStartTime(reservation.getStartTime());
        resp.setEndTime(reservation.getEndTime());
        resp.setStatus(reservation.getStatus());
        resp.setCheckInTime(reservation.getCheckInTime());
        resp.setCancelTime(reservation.getCancelTime());
        resp.setCreateTime(reservation.getCreateTime());
        if (seat != null) {
            resp.setSeatNo(seat.getSeatNo());
            resp.setLocation(seat.getLocation());
        }
        return resp;
    }
}
