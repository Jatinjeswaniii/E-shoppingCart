package com.eshop.ui;

import com.eshop.database.UserDAO;
import com.eshop.models.User;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RegisterFrame extends JFrame {
    private JTextField txtUsername, txtFullName, txtEmail, txtAddress;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister, btnCancel;
    private final UserDAO userDAO;
    private final JFrame parent;
    
    public RegisterFrame(JFrame parent) {
        this.parent = parent;
        this.userDAO = new UserDAO();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("E-Shop Registration");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Header
        JLabel lblHeader = new JLabel("Create New Account", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setForeground(new Color(70, 130, 180));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username field
        addFormField(formPanel, gbc, "Username:", txtUsername = new JTextField(20), 0);
        
        // Password field
        addFormField(formPanel, gbc, "Password:", txtPassword = new JPasswordField(20), 1);
        
        // Confirm Password field
        addFormField(formPanel, gbc, "Confirm Password:", txtConfirmPassword = new JPasswordField(20), 2);
        
        // Full Name field
        addFormField(formPanel, gbc, "Full Name:", txtFullName = new JTextField(20), 3);
        
        // Email field
        addFormField(formPanel, gbc, "Email:", txtEmail = new JTextField(20), 4);
        
        // Address field
        addFormField(formPanel, gbc, "Address:", txtAddress = new JTextField(20), 5);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        btnRegister = new JButton("Register");
        btnRegister.setPreferredSize(new Dimension(100, 35));
        btnRegister.setBackground(new Color(70, 130, 180));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        
        btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setBackground(new Color(220, 220, 220));
        btnCancel.setForeground(new Color(70, 70, 70));
        btnCancel.setFocusPainted(false);
        
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);
        
        // Add action listeners
        btnRegister.addActionListener(e -> registerUser());
        btnCancel.addActionListener(e -> backToLogin());
        
        // Add components to main panel
        mainPanel.add(lblHeader, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Handle window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                backToLogin();
            }
        });
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        field.setPreferredSize(new Dimension(250, 30));
        panel.add(field, gbc);
    }
    
    private void backToLogin() {
        this.dispose();
        parent.setVisible(true);
    }
    
    private void registerUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();
        
        // Validate input
        StringBuilder errors = new StringBuilder();
        
        if (username.isEmpty()) errors.append("• Username is required\n");
        else if (username.length() < 4) errors.append("• Username must be at least 4 characters\n");
        
        if (password.isEmpty()) errors.append("• Password is required\n");
        else if (password.length() < 8) errors.append("• Password must be at least 8 characters\n");
        else if (!UserDAO.validatePassword(password)) 
            errors.append("• Password must contain uppercase, lowercase, number and special character\n");
        
        if (!password.equals(confirmPassword)) 
            errors.append("• Passwords do not match\n");
        
        if (fullName.isEmpty()) errors.append("• Full name is required\n");
        
        if (email.isEmpty()) errors.append("• Email is required\n");
        else if (!UserDAO.validateEmail(email)) 
            errors.append("• Invalid email format\n");
        
        if (address.isEmpty()) errors.append("• Address is required\n");
        
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, errors.toString(), 
                "Registration Errors", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create user object
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setAddress(address);
        user.setRole("customer"); // Default role
        
        try {
            // Check if username already exists
            if (userDAO.isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this, 
                    "Username already exists. Choose another one.", 
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if email already exists
            if (userDAO.isEmailTaken(email)) {
                JOptionPane.showMessageDialog(this, 
                    "Email already registered", 
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Register user
            userDAO.registerUser(user);
            JOptionPane.showMessageDialog(this, 
                "Registration successful! You can now login.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Return to login screen
            backToLogin();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}