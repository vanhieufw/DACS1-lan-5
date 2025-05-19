package com.movie.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.movie.bus.TicketBUS;
import com.movie.bus.MovieBUS;
import com.movie.bus.RoomBUS;
import com.movie.model.Seat;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PaymentFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(PaymentFrame.class.getName());
    private final List<String> seats;
    private final int totalCost;
    private final int scheduleId;
    private int roomId;
    private int customerId;
    private int movieId;
    private TicketBUS ticketBUS;
    private Map<String, Integer> seatNameToIdMap;
    private MovieBUS movieBUS;
    private RoomBUS roomBUS;

    public PaymentFrame(List<String> seats, int totalCost, int scheduleId, int roomId, int customerId, int movieId, int roomIdParam, Map<String, Integer> seatNameToIdMap) {
        this.seats = seats;
        this.totalCost = totalCost;
        this.scheduleId = scheduleId;
        this.roomId = roomId;
        this.customerId = customerId;
        this.movieId = movieId;
        this.ticketBUS = new TicketBUS();
        this.seatNameToIdMap = seatNameToIdMap;
        this.movieBUS = new MovieBUS();
        this.roomBUS = new RoomBUS();
        initUI();
    }

    private void initUI() {
        setTitle("Thanh toán");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.LIGHT_GRAY);

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(new JLabel("Ghế đã chọn: " + String.join(", ", seats)));
        infoPanel.add(new JLabel("Tổng chi phí: " + totalCost + " VND"));
        infoPanel.add(new JLabel("Lịch chiếu: " + scheduleId + " | Phòng: " + roomId));
        infoPanel.add(new JLabel("Phương thức thanh toán: Momo (giả định)"));
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        JButton confirmButton = new JButton("Xác nhận thanh toán");
        confirmButton.addActionListener(e -> confirmPayment());
        mainPanel.add(confirmButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void confirmPayment() {
        Thread paymentThread = new Thread(() -> {
            try {
                Thread.sleep(500);

                List<Seat> seatList = new ArrayList<>();
                for (String seatName : seats) {
                    int seatId = seatNameToIdMap.getOrDefault(seatName, -1);
                    if (seatId == -1) {
                        throw new IllegalArgumentException("Ghế " + seatName + " không hợp lệ!");
                    }
                    Seat seat = new Seat();
                    seat.setSeatID(seatId);
                    seat.setSeatNumber(seatName); // Đảm bảo SeatNumber được gán
                    seatList.add(seat);
                }

                // Fetch movie title and room name
                String movieTitle = movieBUS.getMovieById(movieId).getTitle();
                String roomName = roomBUS.getRoomById(roomId).getRoomName();

                // Call processPayment with all required parameters
                ticketBUS.processPayment(customerId, scheduleId, seatList, (double) totalCost, movieTitle, roomName);

                exportInvoiceToXML();

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công! Hóa đơn đã được xuất.");
                    dispose();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        paymentThread.start();
    }

    private void exportInvoiceToXML() throws Exception {
        File invoiceDir = new File("invoices");
        if (!invoiceDir.exists()) {
            invoiceDir.mkdirs();
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "invoices/invoice_" + timestamp + ".xml";

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("invoice");
        doc.appendChild(rootElement);

        Element invoiceId = doc.createElement("invoiceId");
        invoiceId.appendChild(doc.createTextNode("INV-" + timestamp));
        rootElement.appendChild(invoiceId);

        Element dateTime = doc.createElement("dateTime");
        dateTime.appendChild(doc.createTextNode(LocalDateTime.now().toString()));
        rootElement.appendChild(dateTime);

        Element schedule = doc.createElement("scheduleId");
        schedule.appendChild(doc.createTextNode(String.valueOf(scheduleId)));
        rootElement.appendChild(schedule);

        Element room = doc.createElement("roomId");
        room.appendChild(doc.createTextNode(String.valueOf(roomId)));
        rootElement.appendChild(room);

        Element seatsElement = doc.createElement("seats");
        for (String seat : seats) {
            Element seatElement = doc.createElement("seat");
            seatElement.appendChild(doc.createTextNode(seat));
            seatsElement.appendChild(seatElement);
        }
        rootElement.appendChild(seatsElement);

        Element total = doc.createElement("totalCost");
        total.appendChild(doc.createTextNode(String.valueOf(totalCost)));
        rootElement.appendChild(total);

        Element paymentMethod = doc.createElement("paymentMethod");
        paymentMethod.appendChild(doc.createTextNode("Momo"));
        rootElement.appendChild(paymentMethod);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fileName));
        transformer.transform(source, result);

        LOGGER.info("Hóa đơn đã được xuất: " + fileName);
    }
}