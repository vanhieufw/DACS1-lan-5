package com.movie.bus;

import com.movie.dao.RoomDAO;
import com.movie.model.Room;
import java.sql.SQLException;
import java.util.List;

public class RoomBUS {
    private RoomDAO roomDAO = new RoomDAO();

    public void addRoom(String roomName, int capacity, double price) throws SQLException {
        if (roomName.isEmpty()) {
            throw new IllegalArgumentException("Tên phòng không được để trống");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Sức chứa phải lớn hơn 0");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Giá vé không được âm");
        }
        Room room = new Room();
        room.setRoomName(roomName);
        room.setCapacity(capacity);
        room.setPrice(price);
        roomDAO.addRoom(room);
    }

    public void updateRoom(Room room) throws SQLException {
        if (room.getRoomName().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng không được để trống");
        }
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Sức chứa phải lớn hơn 0");
        }
        if (room.getPrice() < 0) {
            throw new IllegalArgumentException("Giá vé không được âm");
        }
        roomDAO.updateRoom(room);
    }

    public void deleteRoom(int roomID) throws SQLException {
        roomDAO.deleteRoom(roomID);
    }

    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.getAllRooms();
    }

    public Room getRoomById(int roomID) throws SQLException {
        List<Room> rooms = roomDAO.getAllRooms();
        return rooms.stream()
                .filter(room -> room.getRoomID() == roomID)
                .findFirst()
                .orElse(null);
    }
}