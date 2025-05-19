package com.movie.model;

import java.util.Date;

public class BookingHistory {
    private int historyID;
    private int customerID;
    private int ticketID;
    private Date bookingDate;
    private String movieTitle;
    private String roomName;
    private String seatNumber;
    private double price;

    // Getters and Setters
    public int getHistoryID() { return historyID; }
    public void setHistoryID(int historyID) { this.historyID = historyID; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public int getTicketID() { return ticketID; }
    public void setTicketID(int ticketID) { this.ticketID = ticketID; }

    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}