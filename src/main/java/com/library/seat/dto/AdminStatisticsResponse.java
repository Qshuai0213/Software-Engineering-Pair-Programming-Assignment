package com.library.seat.dto;

import java.util.List;

public class AdminStatisticsResponse {

    private int totalSeats;
    private CurrentStatus currentStatus;
    private int totalReservations;
    private List<SeatUsage> seatUsage;
    private List<UserUsage> userUsage;

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    public List<SeatUsage> getSeatUsage() {
        return seatUsage;
    }

    public void setSeatUsage(List<SeatUsage> seatUsage) {
        this.seatUsage = seatUsage;
    }

    public List<UserUsage> getUserUsage() {
        return userUsage;
    }

    public void setUserUsage(List<UserUsage> userUsage) {
        this.userUsage = userUsage;
    }

    public static class CurrentStatus {

        private int available;
        private int reserved;
        private int using;

        public int getAvailable() {
            return available;
        }

        public void setAvailable(int available) {
            this.available = available;
        }

        public int getReserved() {
            return reserved;
        }

        public void setReserved(int reserved) {
            this.reserved = reserved;
        }

        public int getUsing() {
            return using;
        }

        public void setUsing(int using) {
            this.using = using;
        }
    }

    public static class SeatUsage {

        private Long seatId;
        private String seatNo;
        private String location;
        private int count;

        public Long getSeatId() {
            return seatId;
        }

        public void setSeatId(Long seatId) {
            this.seatId = seatId;
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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class UserUsage {

        private Long userId;
        private String username;
        private int count;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
