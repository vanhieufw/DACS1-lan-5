package com.movie.model;

import java.util.List;

public class Room {
    private int roomID;
    private String roomName;
    private int capacity;
    private double price;
    private String movieTitle; // Tên phim đang chiếu
    private String status; // Trạng thái: Đang chiếu/Chuẩn bị chiếu/Không chiếu
    private String showtime; // Suất chiếu
    private List<Seat> seats; // Danh sách ghế

    // Getters and Setters
    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShowtime() { return showtime; }
    public void setShowtime(String showtime) { this.showtime = showtime; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
}