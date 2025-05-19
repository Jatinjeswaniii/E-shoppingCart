package com.eshop.models;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<CartItem> items = new ArrayList<>();

    // Add item to cart or update quantity if already exists
    public void addItem(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    // Remove item from cart
    public void removeItem(int productId) {
        items.removeIf(item -> item.getProduct().getId() == productId);
    }

    // Update quantity of specific item
    public void updateQuantity(int productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    // Get all cart items
    public List<CartItem> getItems() {
        return new ArrayList<>(items); // Return copy to prevent external modification
    }

    // Get total number of items in cart
    public int getItemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    // Calculate total cost of all items
    public double getTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    // Empty the cart
    public void clear() {
        items.clear();
    }

    // Validate stock availability
    public void validateStock() throws IllegalArgumentException {
        for (CartItem item : items) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                throw new IllegalArgumentException(
                    "Not enough stock for " + item.getProduct().getName() + 
                    " (Available: " + item.getProduct().getStock() + ")");
            }
        }
    }
}