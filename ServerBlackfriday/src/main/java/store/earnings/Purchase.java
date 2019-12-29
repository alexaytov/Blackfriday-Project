package store.earnings;

import java.time.ZonedDateTime;

import static validator.Validator.*;

/**
 * The Purchase class represents a given purchase
 * made from the at moment it was made
 * <p>
 * Provides the following information about a purchase
 * name of the product being bought
 * name of the user who bought the product
 * the quantity bough
 * the peirce at which it was bought
 * the date the purchase happened
 */
public class Purchase {

    private String productName;
    private String userName;
    private int quantity;
    private double price;
    private ZonedDateTime date;

    public Purchase(String productName, String userName, int quantity, double price, ZonedDateTime date) {
        this.setProductName(productName);
        this.setQuantity(quantity);
        this.setPrice(price);
        this.setUserName(userName);
        this.date = date;
    }

    public Purchase(String productName, String userName, int quantity, double price) {
        this.setProductName(productName);
        this.setQuantity(quantity);
        this.setPrice(price);
        this.setUserName(userName);
        this.date = ZonedDateTime.now();
    }

    public String getProductName() {
        return this.productName;
    }

    public String getUserName() {
        return this.userName;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public double getPrice() {
        return this.price;
    }

    private void setProductName(String productName) {
        validateName(productName);
        this.productName = productName;
    }

    private void setUserName(String userName) {
        validateName(userName);
        this.userName = userName;
    }

    private void setQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    private void setPrice(double price) {
        validatePrice(price, 0);
        this.price = price;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    /**
     * @return the total amount of money from the purchase
     * cost = bought quantity * the price at which the product was sold
     */
    double getCost() {
        return this.price * this.quantity;
    }
}
