package product;

import common.ExceptionMessages;
import exceptions.NotEnoughQuantity;
import product.interfaces.Buyable;
import product.interfaces.Promotional;
import user.interfaces.User;

import java.io.Serializable;
import java.util.Objects;

import static validator.Validator.*;

public class Product implements Buyable, Promotional, Serializable, Cloneable {

    private String name;
    private String description;
    private int quantity;
    private double price;
    private double discountPercent;
    private boolean discounted;
    private double minimumPrice;
    private String size;

    public Product(String name, String description, int quantity, double price, double minimumPrice, String size, double promotionalPricePercent) {
        this(name, description, quantity, price, minimumPrice, promotionalPricePercent);
        this.setSize(size);
    }

    public Product(String name, String description, int quantity, double price, double minimumPrice, String size) {
        this(name, description, quantity, price, minimumPrice);
        this.setSize(size);
    }

    public Product(String name, String description, int quantity, double price, double minimumPrice, double promotionalPricePercent) {
        this(name, description, quantity, price, minimumPrice);
        this.setDiscountPercent(promotionalPricePercent);
        this.setSize(null);
    }

    public Product(String name, String description, int quantity, double price, double minimumPrice) {
        this.setName(name);
        this.setDescription(description);
        this.setQuantity(quantity);
        this.setPrice(price);
        this.setDiscountPercent(0);
        this.setSize(null);
        this.setMinimumPrice(minimumPrice);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(double minimumPrice) {
        validateMinimumPrice(minimumPrice);
        this.minimumPrice = minimumPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    public String getSize() {
        return this.size;
    }

    //size CAN be null that means the certain product has no size
    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        validateDescription(description);
        this.description = description;
    }


    @Override
    public boolean isDiscounted() {
        return this.discounted;
    }

    public void setDiscounted(boolean discounted) {
        this.discounted = discounted;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(double price) {
        validatePrice(price, this.minimumPrice);
        this.price = price;
    }

    @Override
    public void buy(User user, int quantity) throws NotEnoughQuantity {
        if (quantity > this.getQuantity()) {
            throw new NotEnoughQuantity(String.format(ExceptionMessages.NOT_ENOUGH_QUANTITY, this.getQuantity(), this.getName(), quantity));
        }
        this.setQuantity(this.getQuantity() - quantity);
    }

    private double calculateDiscountedPrice() {
        if (!this.discounted) {
            return this.price;
        }
        return this.price * (1 - this.discountPercent / 100);
    }

    @Override
    public double getDiscountPercent() {
        return this.discountPercent;
    }

    //if the discountPercent is 0 the promotional status of the product becomes false and if the is valid the promotional status becomes true
    @Override
    public void setDiscountPercent(double discountPercent) {
        if (discountPercent == 0) {
            this.discounted = false;
        } else {
            validatePromotionalPricePercent(discountPercent, this.price, this.minimumPrice);
            this.discountPercent = discountPercent;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: " + this.getName())
                .append(System.lineSeparator())
                .append("Description: ")
                .append(this.getDescription())
                .append(System.lineSeparator())
                .append("Quantity: ")
                .append(this.getQuantity())
                .append(System.lineSeparator());
        if (this.getSize() != null) {
            sb
                    .append("Size: ")
                    .append(this.getSize())
                    .append(System.lineSeparator());
        }
        if (this.isDiscounted()) {
            sb.append(String.format("Old price: %.2f", this.getPrice()))
                    .append(System.lineSeparator())
                    .append(String.format("New price: %.2f", this.calculateDiscountedPrice()));
        } else {
            sb.append(String.format("Price: %.2f", this.getPrice()));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return this.getName().equals(product.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }
}
