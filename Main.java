package com.movie;

import com.movie.network.SocketServer;
import com.movie.network.DataUpdater;
import com.movie.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Khởi động server socket
        SocketServer socketServer = new SocketServer(5000);
        new Thread(socketServer::start).start();

        // Khởi động DataUpdater
        DataUpdater dataUpdater = new DataUpdater(socketServer);
        dataUpdater.start();

        // Khởi động giao diện đăng nhập
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}