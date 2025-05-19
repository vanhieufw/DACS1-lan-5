package com.movie.ui;

import com.movie.bus.CustomerBUS;
import com.movie.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {
    private final CustomerBUS customerBUS = new CustomerBUS();
    private final LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        initUI();
    }

    private void initUI() {
        setTitle("ĐĂNG KÝ KHÁCH HÀNG");
        setSize(400, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(200, 220, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("ĐĂNG KÝ KHÁCH HÀNG", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(20, 50, 100));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        JTextField fullNameField = new JTextField(20);
        fullNameField.setBorder(BorderFactory.createTitledBorder("Tên khách hàng"));
        gbc.gridy = 1;
        panel.add(fullNameField, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createTitledBorder("Mật khẩu"));
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        JTextField emailField = new JTextField(20);
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));
        gbc.gridy = 3;
        panel.add(emailField, gbc);

        JButton registerButton = new JButton("Đăng ký");
        registerButton.setBackground(new Color(50, 100, 200));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.addActionListener(e -> handleRegister(fullNameField, passwordField, emailField));
        gbc.gridy = 4;
        panel.add(registerButton, gbc);

        JButton backButton = new JButton("Quay lại");
        backButton.setBackground(new Color(150, 150, 150));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(e -> {
            dispose();
            loginFrame.setVisible(true);
        });
        gbc.gridy = 5;
        panel.add(backButton, gbc);

        add(panel);
        setVisible(true);
    }

    private void handleRegister(JTextField fullNameField, JPasswordField passwordField, JTextField emailField) {
        String fullName = fullNameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String email = emailField.getText().trim();

        if (fullName.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setPassword(password); // Không mã hóa, lưu plain text
        customer.setEmail(email);

        try {
            customerBUS.registerCustomer(customer);
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            loginFrame.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đăng ký: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}