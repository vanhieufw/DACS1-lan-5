package com.movie.dao;

import com.movie.model.Ticket;
import com.movie.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketDAO {
    private static final String INSERT_TICKET = "INSERT INTO Ticket (CustomerID, ShowtimeID, SeatID, Price, SeatNumber) VALUES (?, ?, ?, ?, ?)";
    private static final String CHECK_SEAT_BOOKED = "SELECT COUNT(*) FROM Ticket WHERE SeatID = ? AND ShowtimeID = ?";

    public int bookTicket(Ticket ticket) throws SQLException {
        if (ticket.getSeatNumber() == null) {
            throw new SQLException("SeatNumber không được để trống!");
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_TICKET, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ticket.getCustomerID());
            stmt.setInt(2, ticket.getShowtimeID());
            stmt.setInt(3, ticket.getSeatID());
            stmt.setInt(4, ticket.getPrice()); // Assuming price is now int
            stmt.setString(5, ticket.getSeatNumber());
            stmt.executeUpdate();

            // Retrieve the generated ticketID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated ticketID
                } else {
                    throw new SQLException("Không thể lấy ticketID sau khi chèn!");
                }
            }
        }
    }

    public boolean isSeatBooked(int seatID, int showtimeID) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_SEAT_BOOKED)) {
            stmt.setInt(1, seatID);
            stmt.setInt(2, showtimeID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}