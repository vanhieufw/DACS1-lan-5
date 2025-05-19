package com.movie.bus;

import com.movie.dao.RevenueDAO;
import javax.swing.JOptionPane;
import java.sql.SQLException;

/**
 * Business logic class for handling revenue-related operations.
 */
public class RevenueBUS {
    private final RevenueDAO revenueDAO = new RevenueDAO();

    /**
     * Retrieves the total revenue from all bookings.
     * @return Formatted total revenue as a string (e.g., "100,000.00 VND").
     */
    public String getTotalRevenue() {
        try {
            double revenue = revenueDAO.getTotalRevenue();
            return String.format("%,.2f VND", revenue);
        } catch (SQLException e) {
            // Log the error for debugging
            System.err.println("Error retrieving total revenue: " + e.getMessage());
            // Show user-friendly error message
            JOptionPane.showMessageDialog(null,
                    "Không thể tải tổng doanh thu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return "0.00 VND";
        }
    }
}