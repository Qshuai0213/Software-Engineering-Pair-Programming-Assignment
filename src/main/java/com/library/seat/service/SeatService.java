package com.library.seat.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.seat.common.BusinessException;
import com.library.seat.common.ErrorCode;
import com.library.seat.dto.SeatResponse;
import com.library.seat.entity.Reservation;
import com.library.seat.entity.Seat;
import com.library.seat.mapper.ReservationMapper;
import com.library.seat.mapper.SeatMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final SeatMapper seatMapper;
    private final ReservationMapper reservationMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String SEAT_LIST_PREFIX = "seats:list:";
    private static final String SEAT_DETAIL_PREFIX = "seats:detail:";
    private static final Duration CACHE_TTL = Duration.ofSeconds(30);

    public SeatService(SeatMapper seatMapper,
                       ReservationMapper reservationMapper,
                       StringRedisTemplate redisTemplate,
                       ObjectMapper objectMapper) {
        this.seatMapper = seatMapper;
        this.reservationMapper = reservationMapper;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public List<SeatResponse> getSeats(String location) {
        String cacheKey = SEAT_LIST_PREFIX + (location != null && !location.isEmpty() ? location : "all");

        List<SeatResponse> cached = getCachedList(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Seat> seats;
        if (location != null && !location.isEmpty()) {
            seats = seatMapper.findByLocation(location);
        } else {
            seats = seatMapper.findAll();
        }

        LocalDateTime now = LocalDateTime.now();
        Map<Long, Reservation> currentMap = buildCurrentReservationMap(now);

        List<SeatResponse> result = new ArrayList<>();
        for (Seat seat : seats) {
            SeatResponse resp = toListResponse(seat, currentMap.get(seat.getId()));
            result.add(resp);
        }

        setCache(cacheKey, result);
        return result;
    }

    public SeatResponse getSeatDetail(Long seatId) {
        String cacheKey = SEAT_DETAIL_PREFIX + seatId;

        String cachedJson = redisTemplate.opsForValue().get(cacheKey);
        if (cachedJson != null) {
            try {
                return objectMapper.readValue(cachedJson, SeatResponse.class);
            } catch (Exception ignored) {
            }
        }

        Seat seat = seatMapper.findById(seatId);
        if (seat == null) {
            throw new BusinessException(ErrorCode.SEAT_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();
        Reservation current = reservationMapper.findCurrentBySeatId(seatId, now);

        SeatResponse resp = new SeatResponse();
        resp.setId(seat.getId());
        resp.setSeatNo(seat.getSeatNo());
        resp.setLocation(seat.getLocation());

        if (current == null) {
            resp.setStatus("available");
        } else {
            resp.setStatus("checked_in".equals(current.getStatus()) ? "using" : "reserved");
            SeatResponse.ReservationInfo info = new SeatResponse.ReservationInfo();
            info.setReservationId(current.getId());
            info.setStatus(current.getStatus());
            info.setStartTime(current.getStartTime());
            info.setEndTime(current.getEndTime());
            resp.setCurrentReservation(info);
        }

        try {
            String json = objectMapper.writeValueAsString(resp);
            redisTemplate.opsForValue().set(cacheKey, json, CACHE_TTL);
        } catch (Exception ignored) {
        }

        return resp;
    }

    private Map<Long, Reservation> buildCurrentReservationMap(LocalDateTime now) {
        List<Reservation> currentList = reservationMapper.findAllCurrent(now);
        return currentList.stream()
                .collect(Collectors.toMap(
                        Reservation::getSeatId,
                        r -> r,
                        (existing, incoming) ->
                                "checked_in".equals(incoming.getStatus()) ? incoming : existing
                ));
    }

    private SeatResponse toListResponse(Seat seat, Reservation current) {
        SeatResponse resp = new SeatResponse();
        resp.setId(seat.getId());
        resp.setSeatNo(seat.getSeatNo());
        resp.setLocation(seat.getLocation());

        if (current == null) {
            resp.setStatus("available");
        } else if ("checked_in".equals(current.getStatus())) {
            resp.setStatus("using");
        } else {
            resp.setStatus("reserved");
        }
        return resp;
    }

    private List<SeatResponse> getCachedList(String cacheKey) {
        String cachedJson = redisTemplate.opsForValue().get(cacheKey);
        if (cachedJson == null) {
            return null;
        }
        try {
            return objectMapper.readValue(cachedJson, new TypeReference<List<SeatResponse>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    private void setCache(String cacheKey, List<SeatResponse> data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(cacheKey, json, CACHE_TTL);
        } catch (Exception ignored) {
        }
    }
}
