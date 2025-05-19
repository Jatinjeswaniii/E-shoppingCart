package com.eshop.ui;

import com.eshop.database.UserDAO;
import com.eshop.models.User;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister, btnForgotPassword;
    private final UserDAO userDAO;
    private static final Logger logger = Logger.getLogger(LoginFrame.class.getName());
    
    public LoginFrame() {
        userDAO = new UserDAO();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("E-Shop Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Logo/Header
        JLabel lblHeader = new JLabel("E-Shop", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 24));
        lblHeader.setForeground(new Color(70, 130, 180));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username field
        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField(15);
        txtUsername.setPreferredSize(new Dimension(200, 30));
        
        // Password field
        JLabel lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField(15);
        txtPassword.setPreferredSize(new Dimension(200, 30));
        
        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblUsername, gbc);
        
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblPassword, gbc);
        
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);
        
        // Forgot password link
        btnForgotPassword = new JButton("Forgot Password?");
        btnForgotPassword.setBorderPainted(false);
        btnForgotPassword.setContentAreaFilled(false);
        btnForgotPassword.setForeground(new Color(70, 130, 180));
        btnForgotPassword.addActionListener(e -> showPasswordResetDialog());
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(btnForgotPassword, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(100, 35));
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        
        btnRegister = new JButton("Register");
        btnRegister.setPreferredSize(new Dimension(100, 35));
        btnRegister.setBackground(new Color(220, 220, 220));
        btnRegister.setForeground(new Color(70, 70, 70));
        btnRegister.setFocusPainted(false);
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        
        // Add action listeners
        btnLogin.addActionListener(e -> loginUser());
        btnRegister.addActionListener(e -> openRegisterFrame());
        
        // Add components to main panel
        mainPanel.add(lblHeader, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void loginUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            User user = userDAO.authenticateUser(username, password);
            if (user != null) {
                logger.log(Level.INFO, "User logged in: {0}", username);
                dispose();
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
            } else {
                logger.log(Level.WARNING, "Failed login attempt for username: {0}", username);
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password.", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Database error during login", ex);
            JOptionPane.showMessageDialog(this, 
                "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showPasswordResetDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        JTextField txtEmail = new JTextField();
        
        panel.add(new JLabel("Enter your registered email:"));
        panel.add(txtEmail);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Password Reset", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String email = txtEmail.getText().trim();
            if (email.isEmpty() || !UserDAO.validateEmail(email)) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid email address",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                logger.log(Level.INFO, "Password reset requested for email: {0}", email);
                JOptionPane.showMessageDialog(this,
                    "Password reset link has been sent to " + email,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error sending password reset email", ex);
                JOptionPane.showMessageDialog(this,
                    "Error sending reset email: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void openRegisterFrame() {
        RegisterFrame registerFrame = new RegisterFrame(this);
        registerFrame.setVisible(true);
        this.setVisible(false);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}