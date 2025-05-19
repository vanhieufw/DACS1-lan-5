package com.movie.bus;

import com.movie.dao.BookingHistoryDAO;
import com.movie.dao.TicketDAO;
import com.movie.model.BookingHistory;
import com.movie.model.Seat;
import com.movie.model.Ticket;
import com.movie.network.SocketClient;
import com.movie.network.ThreadManager;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Business logic class for handling ticket booking and payment operations.
 */
public class TicketBUS {
    private final TicketDAO ticketDAO = new TicketDAO();
    private final BookingHistoryDAO bookingHistoryDAO = new BookingHistoryDAO();

    /**
     * Processes a payment and books tickets for the specified seats.
     * @param customerID The ID of the customer.
     * @param showtimeID The ID of the showtime.
     * @param seats The list of seats to book.
     * @param totalPrice The total price for the booking.
     * @param movieTitle The title of the movie (for booking history).
     * @param roomName The name of the room (for booking history).
     * @return A message indicating the result of the operation.
     * @throws SQLException If a database error occurs.
     */
    public String processPayment(int customerID, int showtimeID, List<Seat> seats, double totalPrice, String movieTitle, String roomName) throws SQLException {
        if (customerID <= 0 || showtimeID <= 0 || seats == null || seats.isEmpty() || totalPrice < 0) {
            JOptionPane.showMessageDialog(null,
                    "Thông tin đặt vé không hợp lệ",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return "Thông tin đặt vé không hợp lệ";
        }

        // Check if any seat is already booked
        for (Seat seat : seats) {
            if (ticketDAO.isSeatBooked(seat.getSeatID(), showtimeID)) {
                String message = "Ghế " + seat.getSeatNumber() + " đã được đặt!";
                JOptionPane.showMessageDialog(null,
                        message,
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return message;
            }
        }

        Connection conn = null;
        try {
            conn = com.movie.util.DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            for (Seat seat : seats) {
                // Book ticket
                Ticket ticket = new Ticket();
                ticket.setCustomerID(customerID);
                ticket.setShowtimeID(showtimeID);
                ticket.setSeatID(seat.getSeatID());
                ticket.setPrice((int) (totalPrice / seats.size())); // Cast to int to fix type mismatch
                ticket.setSeatNumber(seat.getSeatNumber()); // Required by TicketDAO
                int ticketID = ticketDAO.bookTicket(ticket); // Assigns the returned ticketID

                // Add booking history
                BookingHistory history = new BookingHistory();
                history.setCustomerID(customerID);
                history.setTicketID(ticketID); // Use the generated ticketID
                history.setBookingDate(new Date());
                history.setMovieTitle(movieTitle); // Use provided movieTitle
                history.setRoomName(roomName);     // Use provided roomName
                history.setSeatNumber(seat.getSeatNumber());
                history.setPrice((int) (totalPrice / seats.size())); // Cast to int for consistency
                bookingHistoryDAO.addBooking(history);
            }

            conn.commit(); // Commit transaction

            // Notify via socket in a separate thread
            ThreadManager.execute(() -> {
                try {
                    SocketClient client = new SocketClient("localhost", 5000); // Align with BookingFrame port
                    client.sendMessage("SEAT_UPDATE:" + showtimeID + ":" + seats.get(0).getRoomID() + ":" + getSeatNumbers(seats));
                } catch (Exception e) {
                    System.err.println("Error sending socket message: " + e.getMessage());
                }
            });

            JOptionPane.showMessageDialog(null,
                    "Thanh toán thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            return "Thanh toán thành công!";
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error processing payment for customer " + customerID + ": " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Không thể xử lý thanh toán: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Retrieves the booking history for a specific customer.
     * @param customerID The ID of the customer.
     * @return A list of booking history records for the customer.
     * @throws SQLException If a database error occurs.
     */
    public List<BookingHistory> getBookingHistory(int customerID) throws SQLException {
        if (customerID <= 0) {
            JOptionPane.showMessageDialog(null,
                    "ID khách hàng không hợp lệ",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("ID khách hàng không hợp lệ");
        }

        try {
            return bookingHistoryDAO.getBookingsByCustomer(customerID);
        } catch (SQLException e) {
            System.err.println("Error retrieving booking history for customer " + customerID + ": " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Không thể tải lịch sử đặt vé: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }

    /**
     * Checks if a seat is booked for a specific showtime.
     * @param seatID The ID of the seat.
     * @param showtimeID The ID of the showtime.
     * @return True if the seat is booked, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    public boolean isSeatBooked(int seatID, int showtimeID) throws SQLException {
        return ticketDAO.isSeatBooked(seatID, showtimeID);
    }

    /**
     * Helper method to get a comma-separated string of seat numbers.
     * @param seats The list of seats.
     * @return A string of seat numbers.
     */
    private String getSeatNumbers(List<Seat> seats) {
        StringBuilder seatNumbers = new StringBuilder();
        for (int i = 0; i < seats.size(); i++) {
            seatNumbers.append(seats.get(i).getSeatNumber());
            if (i < seats.size() - 1) {
                seatNumbers.append(",");
            }
        }
        return seatNumbers.toString();
    }
}