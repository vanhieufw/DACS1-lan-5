package com.movie.bus;

import com.movie.dao.CustomerDAO;
import com.movie.model.Customer;
import com.movie.util.DBConnection;
import com.movie.util.PasswordEncrypter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerBUS {
    private CustomerDAO customerDAO = new CustomerDAO();

    public void registerCustomer(Customer customer) throws SQLException {
        if (customer.getFullName() == null || customer.getPassword() == null || customer.getEmail() == null) {
            throw new IllegalArgumentException("Thông tin không được để trống");
        }
        customer.setUsername(customer.getFullName()); // Giả định username = fullName (có thể thay đổi logic)
        customerDAO.insertCustomer(customer); // Thêm hàm insertCustomer vào CustomerDAO
    }

    public boolean validateUserPlain(String username, String password) {
        try {
            Customer customer = customerDAO.getCustomerByUsernameAndPassword(username, password);
            return customer != null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customer";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerID(rs.getInt("CustomerID"));
                customer.setUsername(rs.getString("Username"));
                customer.setPassword(rs.getString("Password"));
                customer.setFullName(rs.getString("FullName"));
                customer.setEmail(rs.getString("Email"));
                customers.add(customer);
            }
        }
        return customers;
    }
}