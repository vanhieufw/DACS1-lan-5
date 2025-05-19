package com.movie.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.movie.bus.MovieBUS;
import com.movie.bus.RoomBUS;
import com.movie.bus.ShowtimeBUS;
import com.movie.bus.TicketBUS;
import com.movie.bus.SeatBUS;
import com.movie.model.Movie;
import com.movie.model.Room;
import com.movie.model.Seat;
import com.movie.model.Showtime;
import com.movie.network.SocketClient;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingFrame extends JFrame {
    private SocketClient client;
    private JPanel seatPanel;
    private JLabel totalCostLabel;
    private JLabel movieInfoLabel;
    private List<JButton> seatButtons;
    private List<String> selectedSeats;
    private static final int TICKET_PRICE = 120000;
    private int customerId;
    private int showtimeId;
    private int roomId;
    private int movieId;
    private MovieBUS movieBUS;
    private RoomBUS roomBUS;
    private ShowtimeBUS showtimeBUS;
    private TicketBUS ticketBUS;
    private SeatBUS seatBUS;
    private List<Seat> allSeats;
    private Map<String, Integer> seatNameToIdMap;

    public BookingFrame(int customerId, int roomId, int movieId) {
        this.customerId = customerId;
        this.roomId = roomId; // Đảm bảo roomId là 9 nếu bạn đang test với RoomID = 9
        this.movieId = movieId;
        seatButtons = new ArrayList<>();
        selectedSeats = new ArrayList<>();
        movieBUS = new MovieBUS();
        roomBUS = new RoomBUS();
        showtimeBUS = new ShowtimeBUS();
        ticketBUS = new TicketBUS();
        seatBUS = new SeatBUS();
        seatNameToIdMap = new HashMap<>();
        initUI();
        initSocket();
        loadShowtime();
        loadSeats();
    }

    private void initUI() {
        setTitle("Đặt vé xem phim");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);

        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.setBackground(Color.LIGHT_GRAY);
        movieInfoLabel = new JLabel("Đang tải thông tin...");
        infoPanel.add(movieInfoLabel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        seatPanel = new JPanel();
        seatPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(seatPanel);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel costPanel = new JPanel(new FlowLayout());
        costPanel.setBackground(Color.LIGHT_GRAY);
        totalCostLabel = new JLabel("Tổng chi phí: 0 VND");
        costPanel.add(totalCostLabel);
        JButton bookButton = new JButton("Đặt vé");
        bookButton.addActionListener(e -> bookTickets());
        costPanel.add(bookButton);
        mainPanel.add(costPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadShowtime() {
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws SQLException {
                List<Showtime> showtimes = showtimeBUS.getShowtimesByRoomAndMovie(roomId, movieId);
                if (!showtimes.isEmpty()) {
                    return showtimes.get(0).getShowtimeID();
                }
                return -1;
            }

            @Override
            protected void done() {
                try {
                    showtimeId = get();
                    if (showtimeId == -1) {
                        JOptionPane.showMessageDialog(BookingFrame.this,
                                "Không tìm thấy suất chiếu cho phòng " + roomId + "!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        dispose();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(BookingFrame.this,
                            "Lỗi tải suất chiếu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    dispose();
                }
            }
        }.execute();
    }

    private void loadSeats() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    Movie movie = movieBUS.getMovieById(movieId);
                    Room room = roomBUS.getRoomById(roomId);
                    List<Showtime> showtimes = showtimeBUS.getShowtimesByRoomAndMovie(roomId, movieId);
                    allSeats = seatBUS.getSeatsByRoomId(roomId);

                    if (movie != null && room != null && !showtimes.isEmpty() && !allSeats.isEmpty()) {
                        Showtime showtime = showtimes.get(0);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                        String showDateStr = dateFormat.format(showtime.getShowDate());

                        seatNameToIdMap.clear();
                        for (Seat seat : allSeats) {
                            seatNameToIdMap.put(seat.getSeatNumber(), seat.getSeatID());
                        }

                        SwingUtilities.invokeLater(() -> {
                            movieInfoLabel.setText("Phim: " + movie.getTitle() +
                                    " | Rạp: CGV Vincom | Phòng: " + room.getRoomName() +
                                    " | Suất: " + showDateStr);
                            seatPanel.removeAll();
                            seatPanel.setLayout(new GridLayout(0, 5, 10, 10));
                            seatButtons.clear();
                            for (Seat seat : allSeats) {
                                JButton seatButton = new JButton(seat.getSeatNumber());
                                seatButton.setBackground(Color.GREEN);
                                seatButton.addActionListener(e -> toggleSeat(seatButton));
                                seatButtons.add(seatButton);
                                seatPanel.add(seatButton);
                                checkSeatStatus(seatButton, seat.getSeatNumber());
                            }
                            seatPanel.revalidate();
                            seatPanel.repaint();
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(BookingFrame.this,
                                    "Không tải được thông tin phim, phòng hoặc ghế cho RoomID " + roomId + "!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            dispose();
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(BookingFrame.this,
                            "Lỗi tải ghế: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
                }
                return null;
            }
        }.execute();
    }

    private void checkSeatStatus(JButton seatButton, String seatName) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    Integer seatId = seatNameToIdMap.get(seatName);
                    if (seatId == null) return false;
                    return ticketBUS.isSeatBooked(seatId, showtimeId);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        seatButton.setBackground(Color.RED);
                        seatButton.setEnabled(false);
                        selectedSeats.remove(seatName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private int getSeatIdFromName(String seatName) {
        return seatNameToIdMap.getOrDefault(seatName, -1);
    }

    private void toggleSeat(JButton seatButton) {
        String seatName = seatButton.getText();
        if (!seatButton.isEnabled()) return;
        if (seatButton.getBackground().equals(Color.GREEN)) {
            seatButton.setBackground(Color.YELLOW);
            selectedSeats.add(seatName);
        } else if (seatButton.getBackground().equals(Color.YELLOW)) {
            seatButton.setBackground(Color.GREEN);
            selectedSeats.remove(seatName);
        }
        updateTotalCost();
    }

    private void updateTotalCost() {
        int totalCost = selectedSeats.size() * TICKET_PRICE;
        totalCostLabel.setText("Tổng chi phí: " + totalCost + " VND");
    }

    private void initSocket() {
        client = new SocketClient("localhost", 5000);
        client.start();
        int attempts = 30;
        while (attempts > 0 && !client.isConnected()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            attempts--;
        }
        if (!client.isConnected()) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        client.addMessageListener(new SocketClient.MessageListener() {
            @Override
            public void onMessage(String message) {
                SwingUtilities.invokeLater(() -> {
                    if (message.startsWith("SEAT_UPDATE")) {
                        updateSeats(message.replace("SEAT_UPDATE", "").trim());
                    }
                });
            }
        });
        client.sendMessage("GET_SEATS:" + showtimeId + ":" + roomId);
    }

    private void updateSeats(String seatData) {
        if (seatData.isEmpty()) return;
        String[] lockedSeats = seatData.split(",");
        for (JButton seatButton : seatButtons) {
            String seatName = seatButton.getText();
            boolean isLocked = false;
            for (String lockedSeat : lockedSeats) {
                if (seatName.equals(lockedSeat.trim())) {
                    isLocked = true;
                    break;
                }
            }
            if (isLocked && seatButton.isEnabled()) {
                seatButton.setBackground(Color.RED);
                seatButton.setEnabled(false);
                selectedSeats.remove(seatName);
            } else if (!selectedSeats.contains(seatName)) {
                seatButton.setBackground(Color.GREEN);
                seatButton.setEnabled(true);
            }
        }
        updateTotalCost();
    }

    private void bookTickets() {
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một ghế!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (String seat : selectedSeats) {
            try {
                int seatId = getSeatIdFromName(seat);
                if (seatId == -1) {
                    JOptionPane.showMessageDialog(this, "Ghế " + seat + " không hợp lệ trong RoomID " + roomId + "!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (ticketBUS.isSeatBooked(seatId, showtimeId)) {
                    JOptionPane.showMessageDialog(this, "Ghế " + seat + " đã được đặt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi kiểm tra ghế: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String message = "LOCK_SEATS:" + showtimeId + ":" + roomId + ":" + String.join(",", selectedSeats);
        if (client.isConnected()) {
            client.sendMessage(message);
            SwingUtilities.invokeLater(() -> {
                new PaymentFrame(selectedSeats, selectedSeats.size() * TICKET_PRICE, showtimeId, roomId, customerId, movieId, roomId, seatNameToIdMap).setVisible(true);
                dispose();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        if (client != null) {
            client.stop();
        }
        super.dispose();
    }
}