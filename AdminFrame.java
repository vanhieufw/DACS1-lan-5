package com.movie.ui;

import com.movie.bus.MovieBUS;
import com.movie.bus.RoomBUS;
import com.movie.bus.ShowtimeBUS;
import com.movie.bus.StaffBUS;
import com.movie.bus.CustomerBUS;
import com.movie.bus.TicketBUS;
import com.movie.model.Movie;
import com.movie.model.Room;
import com.movie.model.Showtime;
import com.movie.model.Staff;
import com.movie.model.Customer;
import com.movie.dao.MovieDAO;
import com.movie.dao.RevenueDAO;
import com.movie.dao.ShowtimeDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private final MovieBUS movieBUS = new MovieBUS();
    private final RoomBUS roomBUS = new RoomBUS();
    private final ShowtimeBUS showtimeBUS = new ShowtimeBUS();
    private final StaffBUS staffBUS = new StaffBUS();
    private final CustomerBUS customerBUS = new CustomerBUS();
    private final TicketBUS ticketBUS = new TicketBUS();
    private final MovieDAO movieDAO = new MovieDAO();
    private final RevenueDAO revenueDAO = new RevenueDAO();
    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private JPanel movieListPanel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField durationField;
    private JTextField directorField;
    private JTextField genreField;
    private JTextField posterField;
    private JLabel posterPreview;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField productionYearField;
    private JTextField countryField;
    private JTextField ageRestrictionField;
    private JButton updateButton;
    private JButton deleteButton;
    private JPanel formPanel;
    private JLabel timeLabel;
    private Movie selectedMovie;
    private JButton choosePosterButton;
    private JButton selectGenreButton;
    private JButton selectCountryButton;
    private JButton selectStartDateButton;
    private JButton selectEndDateButton;
    private volatile boolean running = true; // Flag to control threads
    private JPanel showtimeListPanel;
    public AdminFrame() {
        initUI();
        startClock();
        startShowtimeStatusUpdater();
    }

    private void initUI() {
        setTitle("Quản lý bán vé xem phim - Hiếu");
        setSize(1200, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Stop threads when the frame is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false; // Stop threads
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel mainView = createMainView();
        mainPanel.add(mainView, "MainView");

        add(mainPanel);
        setVisible(true);

        // ❗❗ Gọi load suất chiếu và hẹn giờ cập nhật mỗi 30 giây
        loadShowtimes(showtimeListPanel); // showtimeListPanel phải là biến thành viên, hoặc được truy cập ở đây
        new javax.swing.Timer(30000, e -> loadShowtimes(showtimeListPanel)).start();
    }

    private void startClock() {
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(Color.BLACK);
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePanel.add(timeLabel);
        add(timePanel, BorderLayout.NORTH);

        new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            while (running) {
                timeLabel.setText(sdf.format(new java.util.Date()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("Clock thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    private void startShowtimeStatusUpdater() {
        new Thread(() -> {
            while (running) {
                try {
                    showtimeBUS.getAllShowtimes();
                    Thread.sleep(60000);
                } catch (SQLException e) {
                    System.err.println("Error updating showtime status: " + e.getMessage());
                } catch (InterruptedException e) {
                    System.err.println("Showtime updater thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    private JPanel createMainView() {
        JPanel mainView = new JPanel(new BorderLayout());
        mainView.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 40));

        JButton homeButton = new JButton("Trang chủ");
        JButton infoButton = new JButton("Thông tin phim");
        JButton roomButton = new JButton("Phòng chiếu");
        JButton showtimeButton = new JButton("Suất chiếu");
        JButton staffButton = new JButton("Nhân viên");
        JButton customerButton = new JButton("Khách hàng");
        JButton statsButton = new JButton("Thống kê");
        JButton logoutButton = new JButton("Đăng xuất");

        styleButton(homeButton);
        styleButton(infoButton);
        styleButton(roomButton);
        styleButton(showtimeButton);
        styleButton(staffButton);
        styleButton(customerButton);
        styleButton(statsButton);
        styleButton(logoutButton);

        homeButton.addActionListener(e -> showPanel("Trang chủ"));
        infoButton.addActionListener(e -> showPanel("Thông tin phim"));
        roomButton.addActionListener(e -> showPanel("Phòng chiếu"));
        showtimeButton.addActionListener(e -> showPanel("Suất chiếu"));
        staffButton.addActionListener(e -> showPanel("Nhân viên"));
        customerButton.addActionListener(e -> showPanel("Khách hàng"));
        statsButton.addActionListener(e -> showPanel("Thống kê"));
        logoutButton.addActionListener(e -> {
            running = false; // Stop threads before logout
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(homeButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(infoButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(roomButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(showtimeButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(staffButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(customerButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(statsButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(logoutButton);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(createHomePanel(), "Trang chủ");
        contentPanel.add(createInfoPanel(), "Thông tin phim");
        contentPanel.add(createRoomPanel(), "Phòng chiếu");
        contentPanel.add(createShowtimePanel(), "Suất chiếu");
        contentPanel.add(createStaffPanel(), "Nhân viên");
        contentPanel.add(createCustomerPanel(), "Khách hàng");
        contentPanel.add(createStatsPanel(), "Thống kê");

        mainView.add(sidebar, BorderLayout.WEST);
        mainView.add(contentPanel, BorderLayout.CENTER);

        return mainView;
    }

    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60));
            }
        });
    }

    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Chào mừng đến với hệ thống quản lý bán vé xem phim", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        infoArea.setText("Hệ thống này cho phép quản lý:\n" +
                "- Danh sách phim và thông tin chi tiết\n" +
                "- Phòng chiếu và suất chiếu\n" +
                "- Nhân viên phụ trách suất chiếu\n" +
                "- Thông tin khách hàng và lịch sử đặt vé\n" +
                "- Thống kê doanh thu theo ngày\n\n" +
                "Vui lòng chọn chức năng từ menu bên trái để bắt đầu.");
        infoArea.setBackground(new Color(245, 245, 245));
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Thông tin phim", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        movieListPanel = new JPanel();
        movieListPanel.setLayout(new BoxLayout(movieListPanel, BoxLayout.Y_AXIS));
        JScrollPane movieScrollPane = new JScrollPane(movieListPanel);
        mainContent.add(movieScrollPane, BorderLayout.CENTER);

        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên phim:"), gbc);
        titleField = new JTextField(20);
        titleField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Thời lượng (phút):"), gbc);
        durationField = new JTextField(20);
        durationField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(durationField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Đạo diễn:"), gbc);
        directorField = new JTextField(20);
        directorField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(directorField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Thể loại:"), gbc);
        genreField = new JTextField(20);
        genreField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(genreField, gbc);
        selectGenreButton = new JButton("Chọn thể loại");
        selectGenreButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 4;
        formPanel.add(selectGenreButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Áp phích:"), gbc);
        posterField = new JTextField(20);
        posterField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(posterField, gbc);
        choosePosterButton = new JButton("Chọn hình ảnh");
        choosePosterButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 5;
        formPanel.add(choosePosterButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Ngày bắt đầu (yyyy-MM-dd):"), gbc);
        startDateField = new JTextField(20);
        startDateField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(startDateField, gbc);
        selectStartDateButton = new JButton("Chọn ngày");
        selectStartDateButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 6;
        formPanel.add(selectStartDateButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Ngày kết thúc (yyyy-MM-dd):"), gbc);
        endDateField = new JTextField(20);
        endDateField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 7;
        formPanel.add(endDateField, gbc);
        selectEndDateButton = new JButton("Chọn ngày");
        selectEndDateButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 7;
        formPanel.add(selectEndDateButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Năm sản xuất:"), gbc);
        productionYearField = new JTextField(20);
        productionYearField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(productionYearField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Quốc gia:"), gbc);
        countryField = new JTextField(20);
        countryField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 9;
        formPanel.add(countryField, gbc);
        selectCountryButton = new JButton("Chọn quốc gia");
        selectCountryButton.setEnabled(false);
        gbc.gridx = 2; gbc.gridy = 9;
        formPanel.add(selectCountryButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 10;
        formPanel.add(new JLabel("Giới hạn tuổi:"), gbc);
        ageRestrictionField = new JTextField(20);
        ageRestrictionField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 10; gbc.gridwidth = 2;
        formPanel.add(ageRestrictionField, gbc);

        posterPreview = new JLabel();
        posterPreview.setPreferredSize(new Dimension(150, 200));
        posterPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 11;
        formPanel.add(posterPreview, gbc);

        formPanel.setVisible(false);
        mainContent.add(formPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm phim");
        updateButton = new JButton("Cập nhật");
        updateButton.setEnabled(false);
        deleteButton = new JButton("Xóa");
        deleteButton.setEnabled(false);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        mainContent.add(buttonPanel, BorderLayout.SOUTH);

        loadMovies();

        addButton.addActionListener(e -> showAddMovieDialog());
        choosePosterButton.addActionListener(e -> choosePoster());
        selectGenreButton.addActionListener(e -> selectGenre());
        selectCountryButton.addActionListener(e -> selectCountry());
        selectStartDateButton.addActionListener(e -> selectDate(startDateField));
        selectEndDateButton.addActionListener(e -> selectDate(endDateField));
        updateButton.addActionListener(e -> updateMovie());
        deleteButton.addActionListener(e -> deleteMovie());

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }

    private void choosePoster() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String posterPath = selectedFile.getAbsolutePath();
            posterField.setText(posterPath);
            posterPreview.setIcon(new ImageIcon(new ImageIcon(posterPath).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
        }
    }

    private void selectGenre() {
        try {
            List<String> genres = movieBUS.getAllGenres();
            String selectedGenre = (String) JOptionPane.showInputDialog(
                    this, "Chọn thể loại:", "Lựa chọn thể loại",
                    JOptionPane.PLAIN_MESSAGE, null, genres.toArray(), genres.get(0));
            if (selectedGenre != null) {
                genreField.setText(selectedGenre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách thể loại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectCountry() {
        try {
            List<String> countries = movieBUS.getAllCountries();
            String selectedCountry = (String) JOptionPane.showInputDialog(
                    this, "Chọn quốc gia:", "Lựa chọn quốc gia",
                    JOptionPane.PLAIN_MESSAGE, null, countries.toArray(), countries.get(0));
            if (selectedCountry != null) {
                countryField.setText(selectedCountry);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách quốc gia: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectDate(JTextField dateField) {
        JDialog dateDialog = new JDialog(this, "Chọn ngày", true);
        dateDialog.setSize(300, 150);
        dateDialog.setLocationRelativeTo(this);
        dateDialog.setLayout(new BorderLayout());

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateDialog.add(dateSpinner, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dateDialog.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false); // Strict parsing
                String dateStr = sdf.format(dateSpinner.getValue());
                sdf.parse(dateStr); // Validate format
                dateField.setText(dateStr);
                dateDialog.dispose();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Vui lòng chọn lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dateDialog.dispose());

        dateDialog.setVisible(true);
    }

    private void showAddMovieDialog() {
        JDialog dialog = new JDialog(this, "Thêm phim", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Tên phim:"), gbc);
        JTextField tempTitleField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        inputPanel.add(tempTitleField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Mô tả:"), gbc);
        JTextArea tempDescriptionArea = new JTextArea(3, 20);
        tempDescriptionArea.setLineWrap(true);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        inputPanel.add(new JScrollPane(tempDescriptionArea), gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Thời lượng (phút):"), gbc);
        JTextField tempDurationField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        inputPanel.add(tempDurationField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Đạo diễn:"), gbc);
        JTextField tempDirectorField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        inputPanel.add(tempDirectorField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Thể loại:"), gbc);
        JTextField tempGenreField = new JTextField(20);
        tempGenreField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 4;
        inputPanel.add(tempGenreField, gbc);
        JButton tempSelectGenreButton = new JButton("Chọn thể loại");
        gbc.gridx = 2; gbc.gridy = 4;
        inputPanel.add(tempSelectGenreButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 5;
        inputPanel.add(new JLabel("Áp phích:"), gbc);
        JTextField tempPosterField = new JTextField(20);
        tempPosterField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 5;
        inputPanel.add(tempPosterField, gbc);
        JButton tempChoosePosterButton = new JButton("Chọn hình ảnh");
        gbc.gridx = 2; gbc.gridy = 5;
        inputPanel.add(tempChoosePosterButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 6;
        inputPanel.add(new JLabel("Ngày bắt đầu (yyyy-MM-dd):"), gbc);
        JTextField tempStartDateField = new JTextField(20);
        tempStartDateField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 6;
        inputPanel.add(tempStartDateField, gbc);
        JButton tempSelectStartDateButton = new JButton("Chọn ngày");
        gbc.gridx = 2; gbc.gridy = 6;
        inputPanel.add(tempSelectStartDateButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 7;
        inputPanel.add(new JLabel("Ngày kết thúc (yyyy-MM-dd):"), gbc);
        JTextField tempEndDateField = new JTextField(20);
        tempEndDateField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 7;
        inputPanel.add(tempEndDateField, gbc);
        JButton tempSelectEndDateButton = new JButton("Chọn ngày");
        gbc.gridx = 2; gbc.gridy = 7;
        inputPanel.add(tempSelectEndDateButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 8;
        inputPanel.add(new JLabel("Năm sản xuất:"), gbc);
        JTextField tempProductionYearField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 8; gbc.gridwidth = 2;
        inputPanel.add(tempProductionYearField, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 9;
        inputPanel.add(new JLabel("Quốc gia:"), gbc);
        JTextField tempCountryField = new JTextField(20);
        tempCountryField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 9;
        inputPanel.add(tempCountryField, gbc);
        JButton tempSelectCountryButton = new JButton("Chọn quốc gia");
        gbc.gridx = 2; gbc.gridy = 9;
        inputPanel.add(tempSelectCountryButton, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 10;
        inputPanel.add(new JLabel("Giới hạn tuổi:"), gbc);
        JTextField tempAgeRestrictionField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 10; gbc.gridwidth = 2;
        inputPanel.add(tempAgeRestrictionField, gbc);

        JLabel tempPosterPreview = new JLabel();
        tempPosterPreview.setPreferredSize(new Dimension(150, 200));
        tempPosterPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 11;
        inputPanel.add(tempPosterPreview, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        tempChoosePosterButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String posterPath = selectedFile.getAbsolutePath();
                tempPosterField.setText(posterPath);
                tempPosterPreview.setIcon(new ImageIcon(new ImageIcon(posterPath).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
            }
        });

        tempSelectGenreButton.addActionListener(e -> {
            try {
                List<String> genres = movieBUS.getAllGenres();
                String selectedGenre = (String) JOptionPane.showInputDialog(
                        dialog, "Chọn thể loại:", "Lựa chọn thể loại",
                        JOptionPane.PLAIN_MESSAGE, null, genres.toArray(), genres.get(0));
                if (selectedGenre != null) {
                    tempGenreField.setText(selectedGenre);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Không thể tải danh sách thể loại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        tempSelectCountryButton.addActionListener(e -> {
            try {
                List<String> countries = movieBUS.getAllCountries();
                String selectedCountry = (String) JOptionPane.showInputDialog(
                        dialog, "Chọn quốc gia:", "Lựa chọn quốc gia",
                        JOptionPane.PLAIN_MESSAGE, null, countries.toArray(), countries.get(0));
                if (selectedCountry != null) {
                    tempCountryField.setText(selectedCountry);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Không thể tải danh sách quốc gia: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        tempSelectStartDateButton.addActionListener(e -> selectDate(tempStartDateField));
        tempSelectEndDateButton.addActionListener(e -> selectDate(tempEndDateField));

        saveButton.addActionListener(e -> {
            try {
                Movie movie = new Movie();
                movie.setTitle(tempTitleField.getText().trim());
                movie.setDescription(tempDescriptionArea.getText().trim());
                movie.setDuration(Integer.parseInt(tempDurationField.getText().trim()));
                movie.setDirector(tempDirectorField.getText().trim());
                movie.setGenreName(tempGenreField.getText().trim());
                movie.setGenreID(movieDAO.getGenreIdByName(tempGenreField.getText().trim()));
                movie.setPoster(tempPosterField.getText().trim());
                movie.setStartDate(Date.valueOf(tempStartDateField.getText().trim()));
                movie.setEndDate(Date.valueOf(tempEndDateField.getText().trim()));
                movie.setProductionYear(Integer.parseInt(tempProductionYearField.getText().trim()));
                movie.setCountryName(tempCountryField.getText().trim());
                movie.setCountryID(movieDAO.getCountryIdByName(tempCountryField.getText().trim()));
                movie.setAgeRestriction(Integer.parseInt(tempAgeRestrictionField.getText().trim()));

                if (movie.getTitle().isEmpty() || movie.getGenreName().isEmpty()) {
                    throw new IllegalArgumentException("Tên phim và thể loại không được để trống");
                }

                movieBUS.addMovie(movie);
                loadMovies();
                JOptionPane.showMessageDialog(dialog, "Thêm phim thành công");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ cho thời lượng, năm sản xuất và giới hạn tuổi", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Không thể thêm phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void loadMovies() {
        try {
            movieListPanel.removeAll();
            List<Movie> movies = movieBUS.getAllMovies();
            for (Movie movie : movies) {
                JPanel moviePanel = new JPanel(new BorderLayout());
                moviePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                moviePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                JLabel movieLabel = new JLabel(String.format("%s - %s - %d phút",
                        movie.getTitle(), movie.getGenreName(), movie.getDuration()));
                movieLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                moviePanel.add(movieLabel, BorderLayout.CENTER);

                JButton selectButton = new JButton("Chọn");
                moviePanel.add(selectButton, BorderLayout.EAST);

                selectButton.addActionListener(e -> {
                    selectedMovie = movie;
                    titleField.setText(movie.getTitle());
                    descriptionArea.setText(movie.getDescription());
                    durationField.setText(String.valueOf(movie.getDuration()));
                    directorField.setText(movie.getDirector());
                    genreField.setText(movie.getGenreName());
                    posterField.setText(movie.getPoster());
                    startDateField.setText(movie.getStartDate().toString());
                    endDateField.setText(movie.getEndDate().toString());
                    productionYearField.setText(String.valueOf(movie.getProductionYear()));
                    countryField.setText(movie.getCountryName());
                    ageRestrictionField.setText(String.valueOf(movie.getAgeRestriction()));
                    if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
                        posterPreview.setIcon(new ImageIcon(new ImageIcon(movie.getPoster()).getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
                    } else {
                        posterPreview.setIcon(null);
                    }
                    formPanel.setVisible(true);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    titleField.setEditable(true);
                    descriptionArea.setEditable(true);
                    durationField.setEditable(true);
                    directorField.setEditable(true);
                    genreField.setEditable(false);
                    posterField.setEditable(false);
                    startDateField.setEditable(false);
                    endDateField.setEditable(false);
                    productionYearField.setEditable(true);
                    countryField.setEditable(false);
                    ageRestrictionField.setEditable(true);
                    choosePosterButton.setEnabled(true);
                    selectGenreButton.setEnabled(true);
                    selectCountryButton.setEnabled(true);
                    selectStartDateButton.setEnabled(true);
                    selectEndDateButton.setEnabled(true);
                });

                movieListPanel.add(moviePanel);
            }
            movieListPanel.revalidate();
            movieListPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMovie() {
        try {
            selectedMovie.setTitle(titleField.getText().trim());
            selectedMovie.setDescription(descriptionArea.getText().trim());
            selectedMovie.setDuration(Integer.parseInt(durationField.getText().trim()));
            selectedMovie.setDirector(directorField.getText().trim());
            selectedMovie.setGenreName(genreField.getText().trim());
            selectedMovie.setGenreID(movieDAO.getGenreIdByName(genreField.getText().trim()));
            selectedMovie.setPoster(posterField.getText().trim());
            selectedMovie.setStartDate(Date.valueOf(startDateField.getText().trim()));
            selectedMovie.setEndDate(Date.valueOf(endDateField.getText().trim()));
            selectedMovie.setProductionYear(Integer.parseInt(productionYearField.getText().trim()));
            selectedMovie.setCountryName(countryField.getText().trim());
            selectedMovie.setCountryID(movieDAO.getCountryIdByName(countryField.getText().trim()));
            selectedMovie.setAgeRestriction(Integer.parseInt(ageRestrictionField.getText().trim()));

            if (selectedMovie.getTitle().isEmpty() || selectedMovie.getGenreName().isEmpty()) {
                throw new IllegalArgumentException("Tên phim và thể loại không được để trống");
            }

            movieBUS.updateMovie(selectedMovie);
            loadMovies();
            formPanel.setVisible(false);
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Cập nhật phim thành công");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho thời lượng, năm sản xuất và giới hạn tuổi", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Không thể cập nhật phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deleteMovie() {
        try {
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phim này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                movieBUS.deleteMovie(selectedMovie.getMovieID());
                loadMovies();
                formPanel.setVisible(false);
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Xóa phim thành công");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể xóa phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Phòng chiếu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));
        JScrollPane roomScrollPane = new JScrollPane(roomListPanel);
        mainContent.add(roomScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm phòng");
        buttonPanel.add(addButton);
        mainContent.add(buttonPanel, BorderLayout.SOUTH);

        loadRooms(roomListPanel);

        addButton.addActionListener(e -> showAddRoomDialog(roomListPanel));

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }

    private void loadRooms(JPanel roomListPanel) {
        try {
            roomListPanel.removeAll();
            List<Room> rooms = roomBUS.getAllRooms();
            for (Room room : rooms) {
                JPanel roomPanel = new JPanel(new BorderLayout());
                roomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                roomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                String priceStr = room.getPrice() > 0 ? String.format("%,.0f VND", room.getPrice()) : "Không có";
                JLabel roomLabel = new JLabel(String.format("%02d - %s - Sức chứa: %d - Giá vé: %s",
                        room.getRoomID(), room.getRoomName(), room.getCapacity(), priceStr));
                roomLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                roomPanel.add(roomLabel, BorderLayout.CENTER);

                JPanel actionPanel = new JPanel(new FlowLayout());
                JButton editButton = new JButton("Sửa");
                JButton deleteButton = new JButton("Xóa");
                actionPanel.add(editButton);
                actionPanel.add(deleteButton);
                roomPanel.add(actionPanel, BorderLayout.EAST);

                editButton.addActionListener(e -> showEditRoomDialog(room, roomListPanel));
                deleteButton.addActionListener(e -> {
                    try {
                        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phòng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            roomBUS.deleteRoom(room.getRoomID());
                            loadRooms(roomListPanel);
                            JOptionPane.showMessageDialog(this, "Xóa phòng thành công");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Không thể xóa phòng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });

                roomListPanel.add(roomPanel);
            }
            roomListPanel.revalidate();
            roomListPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách phòng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddRoomDialog(JPanel roomListPanel) {
        JDialog dialog = new JDialog(this, "Thêm phòng", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Tên phòng:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Số ghế:"), gbc);
        JTextField capacityField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(capacityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Giá vé (VND):"), gbc);
        JTextField priceField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(priceField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String roomName = nameField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                if (roomName.isEmpty()) throw new IllegalArgumentException("Tên phòng không được để trống");

                roomBUS.addRoom(roomName, capacity, price);
                loadRooms(roomListPanel);
                JOptionPane.showMessageDialog(this, "Thêm phòng thành công");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho số ghế và giá vé", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Không thể thêm phòng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
;
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditRoomDialog(Room room, JPanel roomListPanel) {
        JDialog dialog = new JDialog(this, "Sửa phòng", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Tên phòng:"), gbc);
        JTextField nameField = new JTextField(room.getRoomName(), 20);
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Số ghế:"), gbc);
        JTextField capacityField = new JTextField(String.valueOf(room.getCapacity()), 20);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(capacityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Giá vé (VND):"), gbc);
        JTextField priceField = new JTextField(String.valueOf(room.getPrice()), 20);
        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(priceField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String capacityText = capacityField.getText().trim();
                String priceText = priceField.getText().trim();

                int capacity = Integer.parseInt(capacityText);
                double price = Double.parseDouble(priceText);

                room.setRoomName(name);
                room.setCapacity(capacity);
                room.setPrice(price);

                roomBUS.updateRoom(room);
                loadRooms(roomListPanel);

                JOptionPane.showMessageDialog(this, "Cập nhật phòng thành công");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập số hợp lệ cho số ghế và giá vé",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        "Không thể cập nhật phòng: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
;
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private JPanel createShowtimePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Suất chiếu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ✅ KHÔNG khai báo lại JPanel, chỉ gán giá trị
        showtimeListPanel = new JPanel();
        showtimeListPanel.setLayout(new BoxLayout(showtimeListPanel, BoxLayout.Y_AXIS));
        JScrollPane showtimeScrollPane = new JScrollPane(showtimeListPanel);
        mainContent.add(showtimeScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm suất chiếu");
        buttonPanel.add(addButton);
        mainContent.add(buttonPanel, BorderLayout.SOUTH);

        loadShowtimes(showtimeListPanel); // ✅ dùng biến toàn cục

        addButton.addActionListener(e -> showAddShowtimeDialog(showtimeListPanel));

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }

    private void loadShowtimes(JPanel showtimeListPanel) {
        new SwingWorker<List<Showtime>, Void>() {
            @Override
            protected List<Showtime> doInBackground() throws SQLException {
                return showtimeBUS.getAllShowtimes();
            }

            @Override
            protected void done() {
                try {
                    List<Showtime> showtimes = get();
                    showtimeListPanel.removeAll();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                    for (Showtime showtime : showtimes) {
                        JPanel showtimePanel = createShowtimePanel(showtime, showtimeListPanel, sdf);
                        showtimeListPanel.add(showtimePanel);
                    }

                    showtimeListPanel.revalidate();
                    showtimeListPanel.repaint();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(showtimeListPanel, "Không thể tải danh sách suất chiếu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }


    private JPanel createShowtimePanel(Showtime showtime, JPanel showtimeListPanel, SimpleDateFormat sdf) {
        JPanel showtimePanel = new JPanel(new BorderLayout());
        showtimePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        showtimePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        String movieTitle = (showtime.getMovieTitle() != null && !showtime.getMovieTitle().isEmpty())
                ? showtime.getMovieTitle()
                : "Chưa có phim";
        String status = (showtime.getStatus() != null && !showtime.getStatus().isEmpty())
                ? showtime.getStatus()
                : "Không chiếu";
        String roomName = (showtime.getRoomName() != null) ? showtime.getRoomName() : "Phòng không xác định";
        String showDateStr = (showtime.getShowDate() != null)
                ? sdf.format(showtime.getShowDate())
                : "Chưa có thời gian";

        JLabel showtimeLabel = new JLabel(String.format("%02d - %s - Phim: %s - Trạng thái: %s - Thời gian: %s",
                showtime.getShowtimeID(), roomName, movieTitle, status, showDateStr));
        showtimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        showtimePanel.add(showtimeLabel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton addMovieButton = new JButton("Thêm phim");
        JButton addStaffButton = new JButton("Thêm nhân viên");
        JButton hideButton = new JButton("Ẩn");

        actionPanel.add(addMovieButton);
        actionPanel.add(addStaffButton);
        if (status.equals("Đã chiếu xong")) {
            actionPanel.add(hideButton);
        }
        showtimePanel.add(actionPanel, BorderLayout.EAST);

        addMovieButton.addActionListener(e -> showAddMovieToShowtimeDialog(showtime, showtimeListPanel));
        addStaffButton.addActionListener(e -> showAddStaffToShowtimeDialog(showtime, showtimeListPanel));
        hideButton.addActionListener(e -> {
            try {
                showtimeBUS.updateShowtimeStatus(showtime.getShowtimeID(), "Ẩn");
                loadShowtimes(showtimeListPanel);
                JOptionPane.showMessageDialog(showtimeListPanel, "Ẩn suất chiếu thành công");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(showtimeListPanel, "Không thể ẩn suất chiếu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        return showtimePanel;
    }

    private void showAddShowtimeDialog(JPanel showtimeListPanel) {
        JDialog dialog = new JDialog(this, "Thêm suất chiếu", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Phòng:"), gbc);
        JComboBox<String> roomCombo = new JComboBox<>();
        try {
            List<Room> rooms = roomBUS.getAllRooms();
            for (Room room : rooms) {
                roomCombo.addItem(room.getRoomName());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách phòng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(roomCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Ngày giờ chiếu (yyyy-MM-dd HH:mm):"), gbc);
        JTextField showDateField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(showDateField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String roomName = (String) roomCombo.getSelectedItem();
                String showDateStr = showDateField.getText().trim();
                if (roomName == null || showDateStr.isEmpty()) {
                    throw new IllegalArgumentException("Vui lòng chọn phòng và nhập ngày giờ chiếu");
                }
                Room selectedRoom = roomBUS.getAllRooms().stream()
                        .filter(r -> r.getRoomName().equals(roomName))
                        .findFirst()
                        .orElse(null);
                if (selectedRoom == null) {
                    throw new IllegalArgumentException("Phòng không tồn tại");
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                java.util.Date showDate = sdf.parse(showDateStr);
                Showtime showtime = new Showtime();
                showtime.setRoomID(selectedRoom.getRoomID());
                showtime.setShowDate(showDate);
                showtime.setStatus("Không chiếu");
                showtime.setMovieID(0); // Initially no movie
                showtimeDAO.addShowtime(showtime);
                loadShowtimes(showtimeListPanel);
                JOptionPane.showMessageDialog(this, "Thêm suất chiếu thành công");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể thêm suất chiếu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showAddMovieToShowtimeDialog(Showtime showtime, JPanel showtimeListPanel) {
        JDialog dialog = new JDialog(this, "Thêm phim vào suất chiếu " + showtime.getRoomName(), true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Phim:"), gbc);
        JComboBox<String> movieCombo = new JComboBox<>();
        try {
            List<Movie> movies = movieBUS.getAllMovies();
            for (Movie movie : movies) {
                movieCombo.addItem(movie.getTitle());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(movieCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Ngày giờ chiếu (yyyy-MM-dd HH:mm):"), gbc);
        JTextField showDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(showtime.getShowDate()), 20);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(showDateField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String movieTitle = (String) movieCombo.getSelectedItem();
                String showDateStr = showDateField.getText().trim();
                if (movieTitle == null) {
                    throw new IllegalArgumentException("Vui lòng chọn phim");
                }
                Movie selectedMovie = movieBUS.getAllMovies().stream()
                        .filter(m -> m.getTitle().equals(movieTitle))
                        .findFirst()
                        .orElse(null);
                if (selectedMovie == null) {
                    throw new IllegalArgumentException("Phim không tồn tại");
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                showtime.setMovieID(selectedMovie.getMovieID());
                showtime.setShowDate(sdf.parse(showDateStr));
                showtime.setStatus("Sắp công chiếu");
                showtimeDAO.updateShowtime(showtime);
                loadShowtimes(showtimeListPanel);
                JOptionPane.showMessageDialog(this, "Thêm phim vào suất chiếu thành công");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể thêm phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showAddStaffToShowtimeDialog(Showtime showtime, JPanel showtimeListPanel) {
        JDialog dialog = new JDialog(this, "Thêm nhân viên vào suất chiếu " + showtime.getRoomName(), true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Nhân viên:"), gbc);
        JComboBox<String> staffCombo = new JComboBox<>();
        try {
            List<Staff> staffList = staffBUS.getAllStaff();
            for (Staff staff : staffList) {
                staffCombo.addItem(staff.getFullName());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(staffCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String staffName = (String) staffCombo.getSelectedItem();
                if (staffName == null) {
                    throw new IllegalArgumentException("Vui lòng chọn nhân viên");
                }
                Staff selectedStaff = staffBUS.getAllStaff().stream()
                        .filter(s -> s.getFullName().equals(staffName))
                        .findFirst()
                        .orElse(null);
                if (selectedStaff == null) {
                    throw new IllegalArgumentException("Nhân viên không tồn tại");
                }
                showtime.setStaffID(selectedStaff.getStaffID());
                showtimeDAO.updateShowtime(showtime);
                loadShowtimes(showtimeListPanel);
                JOptionPane.showMessageDialog(this, "Thêm nhân viên vào suất chiếu thành công");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể thêm nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Quản lý nhân viên", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel staffListPanel = new JPanel();
        staffListPanel.setLayout(new BoxLayout(staffListPanel, BoxLayout.Y_AXIS));
        JScrollPane staffScrollPane = new JScrollPane(staffListPanel);
        mainContent.add(staffScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm nhân viên");
        buttonPanel.add(addButton);
        mainContent.add(buttonPanel, BorderLayout.SOUTH);

        loadStaff(staffListPanel);

        addButton.addActionListener(e -> showAddStaffDialog(staffListPanel));

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }

    private void loadStaff(JPanel staffListPanel) {
        try {
            staffListPanel.removeAll();
            List<Staff> staffList = staffBUS.getAllStaff();
            for (Staff staff : staffList) {
                JPanel staffPanel = new JPanel(new BorderLayout());
                staffPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                staffPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                JLabel staffLabel = new JLabel(String.format("%02d - %s - Email: %s",
                        staff.getStaffID(), staff.getFullName(), staff.getEmail()));
                staffLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                staffPanel.add(staffLabel, BorderLayout.CENTER);

                JPanel actionPanel = new JPanel(new FlowLayout());
                JButton editButton = new JButton("Sửa");
                JButton deleteButton = new JButton("Xóa");
                actionPanel.add(editButton);
                actionPanel.add(deleteButton);
                staffPanel.add(actionPanel, BorderLayout.EAST);

                editButton.addActionListener(e -> showEditStaffDialog(staff, staffListPanel));
                deleteButton.addActionListener(e -> {
                    try {
                        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            staffBUS.deleteStaff(staff.getStaffID());
                            loadStaff(staffListPanel);
                            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });

                staffListPanel.add(staffPanel);
            }
            staffListPanel.revalidate();
            staffListPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddStaffDialog(JPanel staffListPanel) {
        JDialog dialog = new JDialog(this, "Thêm nhân viên", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Tên nhân viên:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(emailField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String fullName = nameField.getText().trim();
                String email = emailField.getText().trim();
                if (fullName.isEmpty() || email.isEmpty()) {
                    throw new IllegalArgumentException("Tên và email không được để trống");
                }
                staffBUS.addStaff(fullName, email);
                loadStaff(staffListPanel);
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công");
                dialog.dispose();
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Không thể thêm nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditStaffDialog(Staff staff, JPanel staffListPanel) {
        JDialog dialog = new JDialog(this, "Sửa nhân viên", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Tên nhân viên:"), gbc);
        JTextField nameField = new JTextField(staff.getFullName(), 20);
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(staff.getEmail(), 20);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(emailField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                staff.setFullName(nameField.getText().trim());
                staff.setEmail(emailField.getText().trim());
                if (staff.getFullName().isEmpty() || staff.getEmail().isEmpty()) {
                    throw new IllegalArgumentException("Tên và email không được để trống");
                }
                staffBUS.updateStaff(staff);
                loadStaff(staffListPanel);
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công");
                dialog.dispose();
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Quản lý khách hàng", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel customerListPanel = new JPanel();
        customerListPanel.setLayout(new BoxLayout(customerListPanel, BoxLayout.Y_AXIS));
        JScrollPane customerScrollPane = new JScrollPane(customerListPanel);
        mainContent.add(customerScrollPane, BorderLayout.CENTER);

        loadCustomers(customerListPanel);

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }

    private void loadCustomers(JPanel customerListPanel) {
        try {
            customerListPanel.removeAll();
            List<Customer> customers = customerBUS.getAllCustomers();
            for (Customer customer : customers) {
                JPanel customerPanel = new JPanel(new BorderLayout());
                customerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                customerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                int ticketCount = ticketBUS.getBookingHistory(customer.getCustomerID()).size();
                JLabel customerLabel = new JLabel(String.format("%s - Email: %s - Số vé đã đặt: %d",
                        customer.getFullName(), customer.getEmail(), ticketCount));
                customerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                customerPanel.add(customerLabel, BorderLayout.CENTER);

                customerListPanel.add(customerPanel);
            }
            customerListPanel.revalidate();
            customerListPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Thống kê doanh thu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane statsScrollPane = new JScrollPane(statsArea);
        mainContent.add(statsScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel dateLabel = new JLabel("Chọn ngày (yyyy-MM-dd):");
        JTextField dateField = new JTextField(10);
        JButton loadButton = new JButton("Tải thống kê");
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);
        inputPanel.add(loadButton);
        mainContent.add(inputPanel, BorderLayout.NORTH);

        loadButton.addActionListener(e -> {
            try {
                String dateStr = dateField.getText().trim();
                if (dateStr.isEmpty()) {
                    throw new IllegalArgumentException("Vui lòng nhập ngày");
                }
                statsArea.setText("");
                List<Showtime> showtimes = showtimeBUS.getAllShowtimes();
                double totalRevenue = revenueDAO.getTotalRevenueByDate(dateStr);
                statsArea.append(String.format("Tổng doanh thu ngày %s: %,.2f VND\n", dateStr, totalRevenue));
                for (Showtime showtime : showtimes) {
                    if (showtime.getShowDate().toString().startsWith(dateStr) && showtime.getMovieTitle() != null && !showtime.getMovieTitle().isEmpty()) {
                        double showtimeRevenue = revenueDAO.getRevenueByDateAndMovie(dateStr, showtime.getMovieTitle());
                        statsArea.append(String.format("Phim: %s - Doanh thu: %,.2f VND\n",
                                showtime.getMovieTitle(), showtimeRevenue));
                    }
                }
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Không thể tải thống kê: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }
}