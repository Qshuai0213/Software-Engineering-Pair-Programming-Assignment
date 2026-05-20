package com.library.seat.mapper;

import com.library.seat.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationMapper {

    int insert(Reservation reservation);

    Reservation findById(@Param("id") Long id);

    List<Reservation> findByUserId(@Param("userId") Long userId);

    List<Reservation> findConflicting(@Param("seatId") Long seatId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    Reservation findCurrentBySeatId(@Param("seatId") Long seatId,
                                    @Param("now") LocalDateTime now);

    List<Reservation> findActiveByUserId(@Param("userId") Long userId,
                                         @Param("now") LocalDateTime now);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    List<Reservation> findAll();

    List<Reservation> findAllCurrent(@Param("now") LocalDateTime now);

    List<Reservation> findBySeatIdAndTimeRange(@Param("seatId") Long seatId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);
}
