package com.eshop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Static block to ensure driver loads at class loading
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver successfully loaded");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("CRITICAL ERROR: MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        // Add these connection parameters
        String url = "jdbc:mysql://localhost:3306/eshop_db?"
                   + "useSSL=false&"
                   + "allowPublicKeyRetrieval=true&"
                   + "serverTimezone=UTC";
        
        String user = "root";
        String password = "Jatin@123"; // Your MySQL password here
        
        System.out.println("Attempting connection to: " + url);
        return DriverManager.getConnection(url, user, password);
    }
}