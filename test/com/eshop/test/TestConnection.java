package com.eshop.test;

import com.eshop.database.DatabaseConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Starting database connection test...");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("SUCCESS! Connected to database");
            System.out.println("Connection details: " + conn.getMetaData().getURL());
        } catch (Exception e) {
            System.err.println("FAILED to connect:");
            e.printStackTrace();
        }
    }
}