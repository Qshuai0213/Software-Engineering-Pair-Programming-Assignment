package com.library.seat.service;

import com.library.seat.common.BusinessException;
import com.library.seat.common.ErrorCode;
import com.library.seat.dto.AdminReservationResponse;
import com.library.seat.dto.AdminStatisticsResponse;
import com.library.seat.dto.UserResponse;
import com.library.seat.entity.Reservation;
import com.library.seat.entity.Seat;
import com.library.seat.entity.User;
import com.library.seat.mapper.ReservationMapper;
import com.library.seat.mapper.SeatMapper;
import com.library.seat.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserMapper userMapper;
    private final SeatMapper seatMapper;
    private final ReservationMapper reservationMapper;

    public AdminService(UserMapper userMapper,
                        SeatMapper seatMapper,
                        ReservationMapper reservationMapper) {
        this.userMapper = userMapper;
        this.seatMapper = seatMapper;
        this.reservationMapper = reservationMapper;
    }

    public List<AdminReservationResponse> getAllReservations(String role) {
        checkAdmin(role);

        List<Reservation> reservations = reservationMapper.findAll();
        List<User> users = userMapper.findAll();
        List<Seat> seats = seatMapper.findAll();

        Map<Long, String> usernameMap = users.stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
        Map<Long, Seat> seatMap = seats.stream()
                .collect(Collectors.toMap(Seat::getId, s -> s));

        List<AdminReservationResponse> result = new ArrayList<>();
        for (Reservation r : reservations) {
            AdminReservationResponse resp = new AdminReservationResponse();
            resp.setId(r.getId());
            resp.setUsername(usernameMap.getOrDefault(r.getUserId(), "unknown"));
            resp.setStatus(r.getStatus());
            resp.setStartTime(r.getStartTime());
            resp.setEndTime(r.getEndTime());

            Seat seat = seatMap.get(r.getSeatId());
            if (seat != null) {
                resp.setSeatNo(seat.getSeatNo());
                resp.setLocation(seat.getLocation());
            }
            result.add(resp);
        }
        return result;
    }

    public AdminStatisticsResponse getStatistics(String role) {
        checkAdmin(role);

        List<Seat> seats = seatMapper.findAll();
        List<Reservation> allReservations = reservationMapper.findAll();
        List<Reservation> currentReservations = reservationMapper.findAllCurrent(LocalDateTime.now());

        AdminStatisticsResponse stats = new AdminStatisticsResponse();

        stats.setTotalSeats(seats.size());

        AdminStatisticsResponse.CurrentStatus currentStatus = new AdminStatisticsResponse.CurrentStatus();
        int usingCount = 0;
        int reservedCount = 0;
        for (Reservation r : currentReservations) {
            if ("checked_in".equals(r.getStatus())) {
                usingCount++;
            } else if ("reserved".equals(r.getStatus())) {
                reservedCount++;
            }
        }
        currentStatus.setUsing(usingCount);
        currentStatus.setReserved(reservedCount);
        currentStatus.setAvailable(seats.size() - usingCount - reservedCount);
        stats.setCurrentStatus(currentStatus);

        stats.setTotalReservations(allReservations.size());

        Map<Long, Long> seatCountMap = allReservations.stream()
                .collect(Collectors.groupingBy(Reservation::getSeatId, Collectors.counting()));
        Map<Long, Seat> seatMap = seats.stream()
                .collect(Collectors.toMap(Seat::getId, s -> s));

        List<AdminStatisticsResponse.SeatUsage> seatUsages = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : seatCountMap.entrySet()) {
            AdminStatisticsResponse.SeatUsage usage = new AdminStatisticsResponse.SeatUsage();
            usage.setSeatId(entry.getKey());
            Seat seat = seatMap.get(entry.getKey());
            if (seat != null) {
                usage.setSeatNo(seat.getSeatNo());
                usage.setLocation(seat.getLocation());
            }
            usage.setCount(entry.getValue().intValue());
            seatUsages.add(usage);
        }
        seatUsages.sort(Comparator.comparingInt(AdminStatisticsResponse.SeatUsage::getCount).reversed());
        stats.setSeatUsage(seatUsages);

        Map<Long, Long> userCountMap = allReservations.stream()
                .filter(r -> !"cancelled".equals(r.getStatus()))
                .collect(Collectors.groupingBy(Reservation::getUserId, Collectors.counting()));
        Map<Long, String> usernameMap = userMapper.findAll().stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        List<AdminStatisticsResponse.UserUsage> userUsages = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : userCountMap.entrySet()) {
            AdminStatisticsResponse.UserUsage usage = new AdminStatisticsResponse.UserUsage();
            usage.setUserId(entry.getKey());
            usage.setUsername(usernameMap.getOrDefault(entry.getKey(), "unknown"));
            usage.setCount(entry.getValue().intValue());
            userUsages.add(usage);
        }
        userUsages.sort(Comparator.comparingInt(AdminStatisticsResponse.UserUsage::getCount).reversed());
        stats.setUserUsage(userUsages);

        return stats;
    }

    private void checkAdmin(String role) {
        if (!"admin".equals(role)) {
            throw new BusinessException(ErrorCode.NO_ADMIN_PERMISSION);
        }
    }

    public void blockUser(String role, Long userId) {
        checkAdmin(role);

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userMapper.updateStatus(userId, "blocked");
    }

    public void unblockUser(String role, Long userId) {
        checkAdmin(role);

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userMapper.updateStatus(userId, "normal");
    }

    public List<UserResponse> getBlockedUsers(String role) {
        checkAdmin(role);

        List<User> users = userMapper.findBlockedUsers();
        List<UserResponse> result = new ArrayList<>();
        for (User u : users) {
            UserResponse resp = new UserResponse();
            resp.setId(u.getId());
            resp.setUsername(u.getUsername());
            resp.setRole(u.getRole());
            resp.setStatus(u.getStatus());
            result.add(resp);
        }
        return result;
    }

    public List<UserResponse> getAllUsers(String role) {
        checkAdmin(role);

        List<User> users = userMapper.findAll();
        List<UserResponse> result = new ArrayList<>();
        for (User u : users) {
            UserResponse resp = new UserResponse();
            resp.setId(u.getId());
            resp.setUsername(u.getUsername());
            resp.setRole(u.getRole());
            resp.setStatus(u.getStatus());
            result.add(resp);
        }
        return result;
    }
}
