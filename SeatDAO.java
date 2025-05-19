package com.movie.dao;

import com.movie.model.Seat;
import com.movie.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {
    private static final String SELECT_BY_ROOM = "SELECT SeatID, RoomID, SeatNumber, Status FROM Seat WHERE RoomID = ?";

    public List<Seat> getSeatsByRoomId(int roomId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ROOM)) {
            stmt.setInt(1, roomId);
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