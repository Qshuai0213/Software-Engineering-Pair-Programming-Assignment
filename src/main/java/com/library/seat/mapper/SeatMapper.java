package com.library.seat.mapper;

import com.library.seat.entity.Seat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeatMapper {

    List<Seat> findAll();

    List<Seat> findByLocation(@Param("location") String location);

    Seat findById(@Param("id") Long id);
}
