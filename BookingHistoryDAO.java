package com.movie.dao;

import com.movie.model.BookingHistory;
import com.movie.util.DBConnection;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing booking history data in the database.
 */
public class BookingHistoryDAO {
    private static final String INSERT_BOOKING =
            "INSERT INTO BookingHistory (CustomerID, TicketID, BookingDate, MovieTitle, RoomName, SeatNumber, Price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL =
            "SELECT * FROM BookingHistory";
    private static final String SELECT_BY_CUSTOMER =
            "SELECT * FROM BookingHistory WHERE CustomerID = ?";

    /**
     * Adds a new booking history record to the database.
     * @param booking The booking history object to add.
     * @throws SQLException If a database error occurs.
     */
    public void addBooking(BookingHistory booking) throws SQLException {
        if (booking == null || booking.getMovieTitle() == null || booking.getRoomName() == null) {
            throw new IllegalArgumentException("Thông tin đặt vé không hợp lệ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_BOOKING)) {
            stmt.setInt(1, booking.getCustomerID());
            stmt.setInt(2, booking.getTicketID());
            stmt.setDate(3, new java.sql.Date(booking.getBookingDate().getTime()));
            stmt.setString(4, booking.getMovieTitle());
            stmt.setString(5, booking.getRoomName());
            stmt.setString(6, booking.getSeatNumber());
            stmt.setDouble(7, booking.getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding booking for customer " + booking.getCustomerID() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves all booking history records from the database.
     * @return A list of all booking history records.
     * @throws SQLException If a database error occurs.
     */
    public List<BookingHistory> getAllBookings() throws SQLException {
        List<BookingHistory> bookings = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapResultSetToBookingHistory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all bookings: " + e.getMessage());
            throw e;
        }
        return bookings;
    }

    /**
     * Retrieves booking history records for a specific customer.
     * @param customerID The ID of the customer.
     * @return A list of booking history records for the customer.
     * @throws SQLException If a database error occurs.
     */
    public List<BookingHistory> getBookingsByCustomer(int customerID) throws SQLException {
        if (customerID <= 0) {
            throw new IllegalArgumentException("ID khách hàng không hợp lệ");
        }

        List<BookingHistory> bookings = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CUSTOMER)) {
            stmt.setInt(1, customerID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBookingHistory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving bookings for customer " + customerID + ": " + e.getMessage());
            throw e;
        }
        return bookings;
    }

    /**
     * Maps a ResultSet row to a BookingHistory object.
     * @param rs The ResultSet containing booking data.
     * @return A BookingHistory object.
     * @throws SQLException If a database error occurs.
     */
    private BookingHistory mapResultSetToBookingHistory(ResultSet rs) throws SQLException {
        BookingHistory booking = new BookingHistory();
        booking.setHistoryID(rs.getInt("HistoryID"));
        booking.setCustomerID(rs.getInt("CustomerID"));
        booking.setTicketID(rs.getInt("TicketID"));
        booking.setBookingDate(rs.getDate("BookingDate"));
        booking.setMovieTitle(rs.getString("MovieTitle"));
        booking.setRoomName(rs.getString("RoomName"));
        booking.setSeatNumber(rs.getString("SeatNumber"));
        booking.setPrice(rs.getDouble("Price"));
        return booking;
    }
}