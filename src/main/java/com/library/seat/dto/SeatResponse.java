package com.library.seat.dto;

import java.time.LocalDateTime;

public class SeatResponse {

    private Long id;
    private String seatNo;
    private String location;
    private String status;
    private ReservationInfo currentReservation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ReservationInfo getCurrentReservation() {
        return currentReservation;
    }

    public void setCurrentReservation(ReservationInfo currentReservation) {
        this.currentReservation = currentReservation;
    }

    public static class ReservationInfo {

        private Long reservationId;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public Long getReservationId() {
            return reservationId;
        }

        public void setReservationId(Long reservationId) {
            this.reservationId = reservationId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }
    }
}
