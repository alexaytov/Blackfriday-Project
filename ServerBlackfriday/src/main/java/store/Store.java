package store;

import common.ExceptionMessages;
import database.ProductDatabase;
import database.PurchaseDatabase;
import database.UserDatabase;
import exceptions.*;
import product.Product;
import store.earnings.Earnings;
import store.earnings.Purchase;
import user.Client;
import user.Staff;
import user.interfaces.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static validator.Validator.validateQuantity;

public class Store {

    private UserDatabase<Client> clientDatabase;
    private UserDatabase<Staff> staffDatabase;
    private ProductDatabase productDatabase;
    private PurchaseDatabase purchaseDatabase;
    private Earnings earnings;
    private boolean blackFriday;


    public Store(String clientDatabaseFileName, String staffDatabaseFileName, String productDatabaseFileName, String purchasesDatabaseFailName) throws IOException {
        this.clientDatabase = new UserDatabase<>(clientDatabaseFileName, "client");
        this.staffDatabase = new UserDatabase<>(staffDatabaseFileName, "staff");
        this.productDatabase = new ProductDatabase(productDatabaseFileName);
        this.purchaseDatabase = new PurchaseDatabase(purchasesDatabaseFailName);
        this.earnings = new Earnings(this.purchaseDatabase);
        this.blackFriday = false;

    }

    /**
     * Checks if the client database contains client object
     * with the same(@code username) and (@code password)
     *
     * @param username username of the client account to be checked
     * @param password password of the client account to be checked
     * @return Client object from the client database
     * @throws NotFoundException      if there is client with this (@code username)
     * @throws WrongPasswordException if the client registered with (@code username) has different password
     */
    public Client loginAsClient(String username, String password) throws NotFoundException, WrongPasswordException {
        Client user = this.clientDatabase.getByName(username);
        if (user.getPassword().equals(password)) {
            return user;
        }
        throw new WrongPasswordException();

    }

    /**
     * Checks if the staff database contains staff object
     * with the same(@code username) and (@code password)
     *
     * @param username username of the staff account to be checked
     * @param password password of the staff account to be checked
     * @return Staff object from the staff database
     * @throws NotFoundException      if there is staff with this (@code username)
     * @throws WrongPasswordException if the staff registered with (@code username) has different password
     */
    public Staff loginAsStaff(String username, String password) throws NotFoundException, WrongPasswordException {
        Staff user = this.staffDatabase.getByName(username);
        if (user.getPassword().equals(password)) {
            return user;
        }
        throw new WrongPasswordException();
    }

    /**
     * Adds client account to the client database
     *
     * @param client client object to be added to the client database
     * @throws UserAlreadyExistsException if client account with the same username already exists
     */
    public void registerClient(Client client) throws UserAlreadyExistsException {
        if (this.clientDatabase.contains(client)) {
            throw new UserAlreadyExistsException();
        }
        this.clientDatabase.write(client);
    }

    /**
     * Adds staff account to the staff database
     *
     * @param staff staff object to be added to the staff database
     * @throws UserAlreadyExistsException if staff account with the same username already exists
     */
    public void registerStaff(Staff staff) throws UserAlreadyExistsException {
        if (this.staffDatabase.contains(staff)) {
            throw new UserAlreadyExistsException();
        }
        this.staffDatabase.write(staff);
    }

    public void deleteClient(String username) throws NotFoundException {
        this.clientDatabase.delete(username);
    }

    public void deleteStaff(String username) throws NotFoundException {
        this.staffDatabase.delete(username);
    }

    public List<String> getAllClientUsername() {
        return new ArrayList<>(this.clientDatabase.getData()
                .keySet());
    }

    /**
     * Changes user first name
     *
     * @param user      user object to be modified
     * @param firstName the new first name to be set to the user
     * @return if the new first name was set successfully
     */
    public boolean changeUserFirstName(User user, String firstName) {
        try {
            user.setFirstName(firstName);
            if (user instanceof Client) {
                this.clientDatabase.saveAllChanges();
            } else if (user instanceof Staff) {
                this.staffDatabase.saveAllChanges();
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;

    }

    /**
     * Changes user last name
     *
     * @param user     user object to be modified
     * @param lastName the new last name to be set to the user
     * @return if the new last name was set successfully
     */
    public boolean changeUserLastName(User user, String lastName) {
        try {
            user.setLastName(lastName);
            if (user instanceof Client) {
                this.clientDatabase.saveAllChanges();
            } else if (user instanceof Staff) {
                this.staffDatabase.saveAllChanges();
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;

    }

    /**
     * Changes user's username
     *
     * @param user        user object to be modified
     * @param newUsername the new username to be set to the user
     * @return if the new username was set successfully
     */
    public boolean changeUsername(User user, String newUsername) {
        try {
            if (user instanceof Client) {
                this.clientDatabase.delete(user.getUsername());
                user.setUsername(newUsername);
                this.clientDatabase.write((Client) user);
                this.clientDatabase.saveAllChanges();
                user.setUsername(newUsername);
            } else if (user instanceof Staff) {
                this.staffDatabase.delete(user.getUsername());
                user.setUsername(newUsername);
                this.staffDatabase.write((Staff) user);
                this.staffDatabase.saveAllChanges();
            }
        } catch (IllegalArgumentException | NotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Changes user password
     *
     * @param user        user object to be modified
     * @param newPassword the new password to be set to the user
     * @return if the password was successfully changed
     */
    public boolean changePassword(User user, String newPassword) {
        try {
            user.setPassword(newPassword);
            if (user instanceof Client) {
                this.clientDatabase.saveAllChanges();
            } else if (user instanceof Staff) {
                this.staffDatabase.saveAllChanges();
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * Changes age of user
     *
     * @param user   user object to be modified
     * @param newAge the new age to be set to the user
     * @return if the new age is set successful
     */
    public boolean changeAge(User user, int newAge) {
        try {
            user.setAge(newAge);
            if (user instanceof Client) {
                this.clientDatabase.saveAllChanges();
            } else if (user instanceof Staff) {
                this.staffDatabase.saveAllChanges();
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public boolean isBlackFriday() {
        return blackFriday;
    }

    /**
     * Sets black friday status for the store
     * <p>
     * if (@code blackFriday) is true sets all products
     * discounted status to true if their
     * discount percent is higher than 10
     * <p>
     * if (@code blackFriday) is false sets all products
     * discounted status to false
     *
     * @param blackFriday if is true
     */
    public void setBlackFriday(boolean blackFriday) {
        if (blackFriday) {
            for (Map.Entry<String, Product> productEntry : this.productDatabase.getData().entrySet()) {
                if (productEntry.getValue().getDiscountPercent() != 0) {
                    productEntry.getValue().setDiscounted(true);
                }
            }
        } else {
            for (Map.Entry<String, Product> productEntry : this.productDatabase.getData().entrySet()) {
                productEntry.getValue().setDiscounted(false);
            }

        }
        this.blackFriday = blackFriday;
    }

    public ProductDatabase getProductDatabase() {
        return this.productDatabase;
    }

    /**
     * Adds the given product to the product database
     *
     * @param product object to be added
     * @throws ProductAlreadyExistsException if the product already exists
     */
    public void addProduct(Product product) throws ProductAlreadyExistsException {
        if (this.productDatabase.getData().containsKey(product.getName())) {
            throw new ProductAlreadyExistsException(String.format(ExceptionMessages.PRODUCT_ALREADY_EXISTS, product.getName()));
        }
        this.productDatabase.write(product);
    }

    /**
     * Deleted given product from the product database
     *
     * @param productName name of the product
     * @throws NotFoundException if the product isn't found in the database
     */
    public void deleteProduct(String productName) throws NotFoundException {
        this.productDatabase.delete(productName);
    }

    /**
     * @return all promotional product names
     * which have quantity higher than 10
     */
    public Collection<String> getPromotionalProductNamesForClient() {
        return this.productDatabase
                .getData().values().stream()
                .filter(product -> product.isDiscounted() && product.getQuantity() > 0)
                .map(Product::getName)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return all the products which are discounted
     */
    public Collection<String> getPromotionalProductNamesForStaff() {
        return this.productDatabase
                .getData().values().stream()
                .filter(Product::isDiscounted)
                .map(Product::getName)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return all the product names from the
     * database which have quantity higher than 10
     */
    public List<String> getProductNamesForClient() {
        return this.getProductDatabase()
                .getData().values().stream()
                .filter(product -> product.getQuantity() > 0)
                .map(Product::getName)
                .collect(Collectors.toList());
    }

    /**
     * @return all product names in the database
     */
    public List<String> getProductNamesForStaff() {
        return this.getProductDatabase()
                .getData().values().stream()
                .map(Product::getName)
                .collect(Collectors.toList());
    }

    public Product getProductByName(String name) throws NotFoundException {
        return this.productDatabase.getByName(name);
    }

    /**
     * Finds product by name and
     * executes Product buy method
     *
     * @param productName the product's name which is being bought
     * @param user        person who is buying the product
     * @param quantity    the number the product is being bought
     * @return if the purchase was successful
     * @throws NotFoundException if the product is not found
     */
    public boolean buyProduct(String productName, User user, int quantity) throws NotFoundException {
        for (Product product : this.productDatabase.getData().values()) {
            if (product.getName().equalsIgnoreCase(productName)) {
                try {
                    product.buy(user, quantity);
                    this.productDatabase.saveAllChanges();
                    Purchase purchase = new Purchase(productName, user.getUsername(), quantity, product.getPrice());
                    earnings.logPurchase(purchase);
                    return true;
                } catch (NotEnoughQuantity notEnoughQuantity) {
                    return false;
                }
            }
        }
        throw new NotFoundException();
    }

    /**
     * @param quantity maximum quantity
     * @return all product names with quantity lower than (@code quantity)
     */
    public List<String> getProductNamesBelowQuantity(int quantity) {
        validateQuantity(quantity);
        return this.productDatabase.getData().values()
                .stream()
                .filter(product -> product.getQuantity() < quantity)
                .map(Product::getName)
                .collect(Collectors.toList());
    }

    public void changeProductName(Product product, String newProductName) throws NotFoundException {
        this.productDatabase.delete(product.getName());
        product.setName(newProductName);
        this.productDatabase.write(product);
        this.productDatabase.saveAllChanges();
    }

    public void changeProductDescription(Product product, String newDescription) throws NotFoundException {
        this.productDatabase.delete(product.getName());
        product.setDescription(newDescription);
        this.productDatabase.write(product);
        this.productDatabase.saveAllChanges();
    }

    public void changeDiscountPercent(Product product, double newDiscountPercent) throws NotFoundException {
        this.productDatabase.delete(product.getName());
        product.setDiscountPercent(newDiscountPercent);
        this.productDatabase.write(product);
        this.productDatabase.saveAllChanges();
    }

    public void changeProductQuantity(Product product, int quantity) throws NotFoundException {
        this.productDatabase.delete(product.getName());
        product.setQuantity(quantity);
        this.productDatabase.write(product);
        this.productDatabase.saveAllChanges();
    }

    public double getEarnings(int year) {
        return earnings.getEarnings(year);
    }

    public double getEarnings(int month, int year) {
        return earnings.getEarnings(month, year);
    }

    public double getEarnings(LocalDate startDate, LocalDate endDate) {
        return earnings.getEarnings(startDate, endDate);
    }

    public double getEarnings(LocalDate date) {
        return earnings.getEarnings(date);
    }

}
