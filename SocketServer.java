package com.movie.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketServer {
    private ServerSocket serverSocket;
    private int port;
    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public SocketServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server đang chạy trên cổng " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message) {
        clients.removeIf(client -> !client.isValid()); // Loại bỏ client không hợp lệ
        for (ClientHandler client : clients) {
            if (client.isValid()) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.stop();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private SocketServer server;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isValid;

    public ClientHandler(Socket socket, SocketServer server) {
        this.socket = socket;
        this.server = server;
        this.isValid = true;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            isValid = false;
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null && isValid) {
                System.out.println("Nhận từ client: " + message);
                server.broadcast(message);
            }
        } catch (IOException e) {
            isValid = false;
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    public void sendMessage(String message) {
        if (isValid && out != null) {
            out.println(message);
        }
    }

    public boolean isValid() {
        return isValid && socket != null && !socket.isClosed();
    }

    public void stop() {
        isValid = false;
        cleanup();
    }

    private void cleanup() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            server.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}