package com.eshop.ui;

import com.eshop.models.User;
import javax.swing.*;
import java.awt.*;

public class AccountFrame extends JFrame {
    public AccountFrame(User user) {
        setTitle("My Account");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitle = new JLabel("Account Information", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        
        JTextArea txtInfo = new JTextArea(
            "Username: " + user.getUsername() + "\n" +
            "Full Name: " + user.getFullName() + "\n" +
            "Email: " + user.getEmail() + "\n" +
            "Address: " + user.getAddress() + "\n" +
            "Role: " + user.getRole()
        );
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(txtInfo), BorderLayout.CENTER);
        
        add(mainPanel);
    }
}
