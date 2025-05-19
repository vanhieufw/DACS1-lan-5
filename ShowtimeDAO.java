package com.movie.dao;

import com.movie.model.Showtime;
import com.movie.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeDAO {

    public void addShowtime(Showtime showtime) throws SQLException {
        String query = "INSERT INTO Showtime (MovieID, RoomID, ShowDate, StaffID, Status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (showtime.getMovieID() == 0) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, showtime.getMovieID());
            }
            stmt.setInt(2, showtime.getRoomID());
            stmt.setTimestamp(3, new Timestamp(showtime.getShowDate().getTime()));
            if (showtime.getStaffID() == 0) {
                stmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(4, showtime.getStaffID());
            }
            stmt.setString(5, showtime.getStatus());
            stmt.executeUpdate();
        }
    }

    public void updateShowtime(Showtime showtime) throws SQLException {
        String query = "UPDATE Showtime SET MovieID = ?, ShowDate = ?, StaffID = ?, Status = ? WHERE ShowtimeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (showtime.getMovieID() == 0) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, showtime.getMovieID());
            }
            stmt.setTimestamp(2, new Timestamp(showtime.getShowDate().getTime()));
            if (showtime.getStaffID() == 0) {
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, showtime.getStaffID());
            }
            stmt.setString(4, showtime.getStatus());
            stmt.setInt(5, showtime.getShowtimeID());
            stmt.executeUpdate();
        }
    }

    public void updateShowtimeStatus(int showtimeID, String status) throws SQLException {
        String query = "UPDATE Showtime SET Status = ? WHERE ShowtimeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, showtimeID);
            stmt.executeUpdate();
        }
    }

    public List<Showtime> getAllShowtimes() throws SQLException {
        List<Showtime> showtimes = new ArrayList<>();
        String query = "SELECT s.ShowtimeID, s.MovieID, s.RoomID, s.ShowDate, s.StaffID, s.Status, " +
                "m.Title AS MovieTitle, r.RoomName, st.FullName AS StaffName " +
                "FROM Showtime s " +
                "LEFT JOIN Movie m ON s.MovieID = m.MovieID " +
                "LEFT JOIN Room r ON s.RoomID = r.RoomID " +
                "LEFT JOIN Staff st ON s.StaffID = st.StaffID";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Showtime showtime = new Showtime();
                showtime.setShowtimeID(rs.getInt("ShowtimeID"));
                showtime.setMovieID(rs.getInt("MovieID"));
                showtime.setRoomID(rs.getInt("RoomID"));
                showtime.setShowDate(rs.getTimestamp("ShowDate"));
                showtime.setStaffID(rs.getInt("StaffID"));
                showtime.setStatus(rs.getString("Status"));
                showtime.setMovieTitle(rs.getString("MovieTitle"));
                showtime.setRoomName(rs.getString("RoomName"));
                showtime.setStaffName(rs.getString("StaffName"));
                showtimes.add(showtime);
            }
        }
        return showtimes;
    }

    public void deleteShowtime(int showtimeID) throws SQLException {
        String query = "DELETE FROM Showtime WHERE ShowtimeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, showtimeID);
            stmt.executeUpdate();
        }
    }
}