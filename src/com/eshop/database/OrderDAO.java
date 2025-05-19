package com.eshop.database;

import com.eshop.models.Order;
import com.eshop.models.OrderItem;
import com.eshop.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    private final ProductDAO productDAO = new ProductDAO();
    
    public int saveOrder(Order order, List<OrderItem> items) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert order
            String orderQuery = "INSERT INTO orders (user_id, total_amount, status, shipping_address) " +
                                "VALUES (?, ?, ?, ?)";
            
            int orderId;
            try (PreparedStatement pstmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, order.getUserId());
                pstmt.setDouble(2, order.getTotalAmount());
                pstmt.setString(3, order.getStatus());
                pstmt.setString(4, order.getShippingAddress());
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }
            
            // Insert order items
            String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) " +
                              "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(itemQuery)) {
                for (OrderItem item : items) {
                    pstmt.setInt(1, orderId);
                    pstmt.setInt(2, item.getProductId());
                    pstmt.setInt(3, item.getQuantity());
                    pstmt.setDouble(4, item.getPrice());
                    pstmt.addBatch();
                    
                    // Update product stock
                    productDAO.updateStock(item.getProductId(), item.getQuantity());
                }
                pstmt.executeBatch();
            }
            
            conn.commit();
            return orderId;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Error during rollback", ex);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public List<Order> getOrdersByUser(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("order_date"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("shipping_address")
                    );
                    
                    // Get order items for this order
                    order.setOrderItems(getOrderItemsByOrderId(order.getId()));
                    orders.add(order);
                }
            }
        }
        
        return orders;
    }
    
    public List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT * FROM order_items WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, orderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                    );
                    
                    // Get product details
                    Product product = productDAO.getProductById(item.getProductId());
                    item.setProduct(product);
                    
                    items.add(item);
                }
            }
        }
        
        return items;
    }
    
    public Order getOrderById(int orderId) throws SQLException {
        String query = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, orderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("order_date"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("shipping_address")
                    );
                    
                    // Get order items
                    order.setOrderItems(getOrderItemsByOrderId(orderId));
                    return order;
                }
            }
        }
        
        return null;
    }
    
    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            
            pstmt.executeUpdate();
        }
    }
}
