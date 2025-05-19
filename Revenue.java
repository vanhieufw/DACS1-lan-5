package com.movie.model;

public class Revenue {
    private int revenueID;
    private int showtimeID;
    private double totalRevenue;
    private String revenueDate;

    public int getRevenueID() { return revenueID; }
    public void setRevenueID(int revenueID) { this.revenueID = revenueID; }
    public int getShowtimeID() { return showtimeID; }
    public void setShowtimeID(int showtimeID) { this.showtimeID = showtimeID; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public String getRevenueDate() { return revenueDate; }
    public void setRevenueDate(String revenueDate) { this.revenueDate = revenueDate; }
}