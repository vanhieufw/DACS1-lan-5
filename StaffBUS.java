package com.movie.bus;

import com.movie.dao.StaffDAO;
import com.movie.model.Staff;
import java.sql.SQLException;
import java.util.List;

public class StaffBUS {
    private StaffDAO staffDAO = new StaffDAO();

    public void addStaff(String fullName, String email) throws SQLException {
        if (fullName.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("Tên và email không được để trống");
        }
        Staff staff = new Staff();
        staff.setFullName(fullName);
        staff.setEmail(email);
        staffDAO.addStaff(staff);
    }

    public void updateStaff(Staff staff) throws SQLException {
        if (staff.getFullName().isEmpty() || staff.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Tên và email không được để trống");
        }
        staffDAO.updateStaff(staff);
    }

    public void deleteStaff(int staffID) throws SQLException {
        staffDAO.deleteStaff(staffID);
    }

    public List<Staff> getAllStaff() throws SQLException {
        return staffDAO.getAllStaff();
    }
}