package com.movie.dao;

import com.movie.model.Staff;
import com.movie.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    public void addStaff(Staff staff) throws SQLException {
        String query = "INSERT INTO Staff (FullName, Email) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, staff.getFullName());
            stmt.setString(2, staff.getEmail());
            stmt.executeUpdate();
        }
    }

    public void updateStaff(Staff staff) throws SQLException {
        String query = "UPDATE Staff SET FullName = ?, Email = ? WHERE StaffID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, staff.getFullName());
            stmt.setString(2, staff.getEmail());
            stmt.setInt(3, staff.getStaffID());
            stmt.executeUpdate();
        }
    }

    public void deleteStaff(int staffID) throws SQLException {
        String query = "DELETE FROM Staff WHERE StaffID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, staffID);
            stmt.executeUpdate();
        }
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String query = "SELECT * FROM Staff";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Staff staff = new Staff();
                staff.setStaffID(rs.getInt("StaffID"));
                staff.setFullName(rs.getString("FullName"));
                staff.setEmail(rs.getString("Email"));
                staffList.add(staff);
            }
        }
        return staffList;
    }
}