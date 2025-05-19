package com.eshop.ui;

import com.eshop.database.ProductDAO;
import com.eshop.models.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
    private final User currentUser;
    private final ShoppingCart cart;
    private final ProductDAO productDAO;
    private final DecimalFormat df = new DecimalFormat("$#,##0.00");
    
    // UI Components
    private JPanel productPanel;
    private JLabel lblCartCount, lblCartTotal;
    private JButton btnViewCart, btnMyAccount, btnLogout;

    public MainFrame(User user) {
        this.currentUser = user;
        this.cart = new ShoppingCart();
        this.productDAO = new ProductDAO();
        
        initUI();
        loadProducts();
    }

    private void initUI() {
        setTitle("E-Shop - Welcome " + currentUser.getFullName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel setup
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Create and add header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Product listings panel
        productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(new Color(245, 245, 245));
        
        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Store title
        JLabel lblStore = new JLabel("E-Shop Store");
        lblStore.setFont(new Font("Arial", Font.BOLD, 24));
        lblStore.setForeground(Color.WHITE);
        
        // Cart info panel
        JPanel cartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cartPanel.setOpaque(false);
        
        lblCartCount = new JLabel("Cart: 0 items");
        lblCartTotal = new JLabel("Total: $0.00");
        styleCartLabels();
        
        btnViewCart = new JButton("View Cart");
        styleButton(btnViewCart, new Color(50, 110, 160));
        
        cartPanel.add(lblCartCount);
        cartPanel.add(Box.createHorizontalStrut(10));
        cartPanel.add(lblCartTotal);
        cartPanel.add(Box.createHorizontalStrut(20));
        cartPanel.add(btnViewCart);
        
        // User panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        userPanel.setOpaque(false);
        
        btnMyAccount = new JButton("My Account");
        btnLogout = new JButton("Logout");
        styleButton(btnMyAccount, new Color(50, 110, 160));
        styleButton(btnLogout, new Color(50, 110, 160));
        
        userPanel.add(btnMyAccount);
        userPanel.add(Box.createHorizontalStrut(5));
        userPanel.add(btnLogout);
        
        // Combine panels
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(cartPanel, BorderLayout.CENTER);
        rightPanel.add(userPanel, BorderLayout.EAST);
        
        headerPanel.add(lblStore, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        // Add action listeners
        btnViewCart.addActionListener(e -> viewCart());
        btnMyAccount.addActionListener(e -> viewAccount());
        btnLogout.addActionListener(e -> logout());
        
        return headerPanel;
    }

    private void styleCartLabels() {
        lblCartCount.setForeground(Color.WHITE);
        lblCartCount.setFont(new Font("Arial", Font.PLAIN, 14));
        lblCartTotal.setForeground(Color.WHITE);
        lblCartTotal.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void loadProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            productPanel.removeAll();
            
            if (products.isEmpty()) {
                productPanel.add(new JLabel("No products available", SwingConstants.CENTER));
            } else {
                for (Product product : products) {
                    productPanel.add(createProductCard(product));
                    productPanel.add(Box.createVerticalStrut(10));
                }
            }
            productPanel.revalidate();
            productPanel.repaint();
        } catch (SQLException ex) {
            showError("Error loading products: " + ex.getMessage());
        }
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(800, 200));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
        
        // Image panel (placeholder)
        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(150, 150));
        imagePanel.setBackground(new Color(240, 240, 240));
        
        // Product details panel
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(Color.WHITE);
        
        // Product info
        JLabel lblName = new JLabel(product.getName());
        lblName.setFont(new Font("Arial", Font.BOLD, 16));
        
        JTextArea txtDescription = new JTextArea(product.getDescription());
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setEditable(false);
        txtDescription.setBackground(Color.WHITE);
        txtDescription.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Price and stock
        JLabel lblPrice = new JLabel(df.format(product.getPrice()));
        lblPrice.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel lblStock = new JLabel("In Stock: " + product.getStock());
        lblStock.setFont(new Font("Arial", Font.ITALIC, 12));
        
        // Quantity controls
        JSpinner spinner = new JSpinner(
            new SpinnerNumberModel(1, 1, product.getStock(), 1));
        spinner.setPreferredSize(new Dimension(60, 30));
        
        JButton btnAddToCart = new JButton("Add to Cart");
        styleButton(btnAddToCart, new Color(70, 130, 180));
        btnAddToCart.addActionListener(e -> addToCart(product, (int)spinner.getValue()));
        
        // Layout components
        JPanel infoPanel = new JPanel(new BorderLayout(5, 10));
        infoPanel.setOpaque(false);
        infoPanel.add(lblName, BorderLayout.NORTH);
        infoPanel.add(txtDescription, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Qty:"));
        actionPanel.add(spinner);
        actionPanel.add(btnAddToCart);
        
        JPanel pricePanel = new JPanel(new BorderLayout(5, 10));
        pricePanel.setOpaque(false);
        pricePanel.add(lblPrice, BorderLayout.NORTH);
        pricePanel.add(lblStock, BorderLayout.CENTER);
        pricePanel.add(actionPanel, BorderLayout.SOUTH);
        
        detailsPanel.add(infoPanel, BorderLayout.CENTER);
        detailsPanel.add(pricePanel, BorderLayout.EAST);
        
        card.add(imagePanel, BorderLayout.WEST);
        card.add(detailsPanel, BorderLayout.CENTER);
        
        return card;
    }

    private void addToCart(Product product, int quantity) {
        try {
            cart.addItem(product, quantity);
            updateCartInfo();
            showSuccess("Added " + quantity + " × " + product.getName() + " to cart");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateCartInfo() {
        lblCartCount.setText("Cart: " + cart.getItemCount() + " items");
        lblCartTotal.setText("Total: " + df.format(cart.getTotal()));
    }

    private void viewCart() {
        if (cart.getItems().isEmpty()) {
            showInfo("Your cart is empty");
            return;
        }
        new CartFrame(this, cart, currentUser).setVisible(true);
    }

    private void viewAccount() {
        new AccountFrame(currentUser).setVisible(true);
    }

    private void logout() {
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout Confirmation",
            JOptionPane.YES_NO_OPTION)) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    public void refreshCart() {
        updateCartInfo();
    }

    // Helper methods for dialogs
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}