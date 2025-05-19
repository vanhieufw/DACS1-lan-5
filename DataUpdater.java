package com.movie.network;

import com.movie.bus.RoomBUS;

import java.sql.SQLException;

public class DataUpdater {
    private RoomBUS roomBUS = new RoomBUS();
    private SocketServer socketServer;

    public DataUpdater(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    public void start() {
        ThreadManager.execute(() -> {
            while (true) {
                try {
                    // Kiểm tra trạng thái phòng và ghế
                    roomBUS.getAllRooms().forEach(room -> {
                        try {
                            if (room.getStatus().equals("Đang chiếu") || room.getStatus().equals("Chuẩn bị chiếu")) {
                                socketServer.broadcast("SEAT_UPDATE:" + room.getRoomID());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    Thread.sleep(30000); // Cập nhật mỗi 30 giây
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}