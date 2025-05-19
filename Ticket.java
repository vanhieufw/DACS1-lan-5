package com.movie.model;

public class Ticket {
    private int ticketID;
    private int customerID;
    private int showtimeID;
    private int seatID;
    private int price;
    private String seatNumber; // Thêm thuộc tính SeatNumber

    // Constructor không tham số
    public Ticket() {
    }

    // Getters và Setters
    public int getTicketID() { return ticketID; }
    public void setTicketID(int ticketID) { this.ticketID = ticketID; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public int getShowtimeID() { return showtimeID; }
    public void setShowtimeID(int showtimeID) { this.showtimeID = showtimeID; }

    public int getSeatID() { return seatID; }
    public void setSeatID(int seatID) { this.seatID = seatID; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
}