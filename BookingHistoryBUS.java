package com.movie.bus;

import com.movie.dao.BookingHistoryDAO;
import com.movie.model.BookingHistory;
import java.sql.SQLException;
import java.util.List;

public class BookingHistoryBUS {
    private final BookingHistoryDAO bookingHistoryDAO = new BookingHistoryDAO();

    public void addBooking(BookingHistory booking) throws SQLException {
        bookingHistoryDAO.addBooking(booking);
    }

    public List<BookingHistory> getAllBookings() throws SQLException {
        return bookingHistoryDAO.getAllBookings();
    }

    public List<BookingHistory> getBookingsByCustomer(int customerID) throws SQLException {
        return bookingHistoryDAO.getBookingsByCustomer(customerID);
    }
}