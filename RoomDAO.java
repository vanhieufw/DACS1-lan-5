package com.movie.dao;

import com.movie.model.Room;
import com.movie.model.Seat;
import com.movie.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomDAO {
    public void addRoom(Room room) throws SQLException {
        String query = "INSERT INTO Room (RoomName, Capacity, Price) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getRoomName());
            stmt.setInt(2, room.getCapacity());
            stmt.setDouble(3, room.getPrice());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    room.setRoomID(rs.getInt(1));
                    createSeatsForRoom(room);
                }
            }
        }
    }

    public void updateRoom(Room room) throws SQLException {
        String query = "UPDATE Room SET RoomName = ?, Capacity = ?, Price = ? WHERE RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, room.getRoomName());
            stmt.setInt(2, room.getCapacity());
            stmt.setDouble(3, room.getPrice());
            stmt.setInt(4, room.getRoomID());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No room found with ID " + room.getRoomID());
            }
            updateSeatsForRoom(room);
        }
    }

    public void deleteRoom(int roomID) throws SQLException {
        String query = "DELETE FROM Room WHERE RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No room found with ID " + roomID);
            }
        }
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT r.*, m.Title, s.ShowDate, m.StartDate, m.EndDate, m.Duration " +
                "FROM Room r " +
                "LEFT JOIN Showtime s ON r.RoomID = s.RoomID " +
                "LEFT JOIN Movie m ON s.MovieID = m.MovieID";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Room room = new Room();
                room.setRoomID(rs.getInt("RoomID"));
                room.setRoomName(rs.getString("RoomName"));
                room.setCapacity(rs.getInt("Capacity"));
                room.setPrice(rs.getDouble("Price"));
                room.setMovieTitle(rs.getString("Title"));
                Date showDate = rs.getTimestamp("ShowDate");
                Date startDate = rs.getDate("StartDate");
                Date endDate = rs.getDate("EndDate");
                Date currentDate = new Date();
                if (showDate == null || startDate == null || endDate == null) {
                    room.setStatus("Không chiếu");
                } else {
                    int duration = rs.getInt("Duration"); // Lấy Duration từ bảng Movie
                    if (rs.wasNull()) duration = 0; // Nếu Duration là null, gán 0
                    if (currentDate.after(startDate) && currentDate.before(endDate) &&
                            currentDate.after(showDate) &&
                            currentDate.before(new Date(showDate.getTime() + duration * 60 * 1000))) {
                        room.setStatus("Đang chiếu");
                    } else if (currentDate.before(showDate)) {
                        room.setStatus("Chuẩn bị chiếu");
                    } else {
                        room.setStatus("Không chiếu");
                    }
                }
                room.setSeats(getSeatsByRoomId(room.getRoomID()));
                rooms.add(room);
            }
        }
        return rooms;
    }
    public Room getRoomById(int roomID) throws SQLException {
        String query = "SELECT r.*, m.Title, s.ShowDate, m.StartDate, m.EndDate, m.Duration " +
                "FROM Room r " +
                "LEFT JOIN Showtime s ON r.RoomID = s.RoomID " +
                "LEFT JOIN Movie m ON s.MovieID = m.MovieID " +
                "WHERE r.RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setRoomID(rs.getInt("RoomID"));
                    room.setRoomName(rs.getString("RoomName"));
                    room.setCapacity(rs.getInt("Capacity"));
                    room.setPrice(rs.getDouble("Price"));
                    room.setMovieTitle(rs.getString("Title"));
                    Date showDate = rs.getTimestamp("ShowDate");
                    Date startDate = rs.getDate("StartDate");
                    Date endDate = rs.getDate("EndDate");
                    Date currentDate = new Date();
                    if (showDate == null || startDate == null || endDate == null) {
                        room.setStatus("Không chiếu");
                    } else {
                        int duration = rs.getInt("Duration"); // Lấy Duration từ bảng Movie
                        if (rs.wasNull()) duration = 0; // Nếu Duration là null, gán 0
                        if (currentDate.after(startDate) && currentDate.before(endDate) &&
                                currentDate.after(showDate) &&
                                currentDate.before(new Date(showDate.getTime() + duration * 60 * 1000))) {
                            room.setStatus("Đang chiếu");
                        } else if (currentDate.before(showDate)) {
                            room.setStatus("Chuẩn bị chiếu");
                        } else {
                            room.setStatus("Không chiếu");
                        }
                    }
                    room.setSeats(getSeatsByRoomId(roomID));
                    return room;
                }
            }
        }
        return null;
    }

    private void createSeatsForRoom(Room room) throws SQLException {
        String query = "INSERT INTO Seat (RoomID, SeatNumber, Status) VALUES (?, ?, 'Trống')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 1; i <= room.getCapacity(); i++) {
                String seatNumber = (i <= 5 ? "A" : "B") + (i % 5 == 0 ? 5 : i % 5);
                stmt.setInt(1, room.getRoomID());
                stmt.setString(2, seatNumber);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void updateSeatsForRoom(Room room) throws SQLException {
        String deleteQuery = "DELETE FROM Seat WHERE RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setInt(1, room.getRoomID());
            stmt.executeUpdate();
        }
        createSeatsForRoom(room);
    }

    public List<Seat> getSeatsByRoomId(int roomID) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT s.*, CASE WHEN t.TicketID IS NOT NULL THEN 'Đã đặt' ELSE 'Trống' END AS Status " +
                "FROM Seat s " +
                "LEFT JOIN Ticket t ON s.SeatID = t.SeatID " +
                "WHERE s.RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = new Seat();
                    seat.setSeatID(rs.getInt("SeatID"));
                    seat.setRoomID(rs.getInt("RoomID"));
                    seat.setSeatNumber(rs.getString("SeatNumber"));
                    seat.setStatus(rs.getString("Status"));
                    seats.add(seat);
                }
            }
        }
        return seats;
    }
}