package com.movie.model;

public class Seat {
    private int seatID;
    private int roomID;
    private String seatNumber;
    private String status;

    // No-argument constructor
    public Seat() {
    }

    // Constructor that accepts seatNumber
    public Seat(String seatNumber) {
        this.seatID = 0; // Default value, adjust as needed
        this.roomID = 0; // Default value, adjust as needed
        this.seatNumber = seatNumber;
        this.status = "AVAILABLE"; // Default status, adjust as needed
    }

    // Getters and Setters
    public int getSeatID() { return seatID; }
    public void setSeatID(int seatID) { this.seatID = seatID; }

    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}