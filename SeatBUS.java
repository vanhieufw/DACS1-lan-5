package com.movie.bus;

import com.movie.dao.SeatDAO;
import com.movie.model.Seat;
import java.sql.SQLException;
import java.util.List;

public class SeatBUS {
    private SeatDAO seatDAO = new SeatDAO();

    public List<Seat> getSeatsByRoomId(int roomId) throws SQLException {
        return seatDAO.getSeatsByRoomId(roomId);
    }
}
