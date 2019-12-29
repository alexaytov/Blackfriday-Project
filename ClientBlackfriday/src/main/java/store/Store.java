package store;

import common.ConstantMessages;
import common.ExceptionMessages;
import exceptions.NotFoundException;
import exceptions.WrongPasswordException;
import menu.Menu;
import product.Product;
import user.Client;
import user.Staff;
import user.interfaces.User;
import validator.Validator;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import static common.ConstantMessages.SUCCESSFUL_PURCHASE;
import static common.ConstantMessages.UNSUCCESSFUL_PURCHASE;
import static validator.Validator.*;

/**
 *
 */
public class Store {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader reader;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public Store(InetAddress ip, int port, BufferedReader reader) throws IOException {
        connectToServer(ip, port);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        this.reader = reader;
        oos = new ObjectOutputStream(outputStream);
        oos.flush();
        ois = new ObjectInputStream(inputStream);


    }

    private void connectToServer(InetAddress ip, int port) throws IOException {
        socket = new Socket(ip, port);
    }


    public void open() throws IOException, ClassNotFoundException {
        User user;
        while (true) {
            Menu.printMainMenu();
            int choice = Menu.getChoice(1, 3, this.reader);
            switch (choice) {
                case 1:
                    Menu.printLoginSubmenu();
                    int userTypeChoice = Menu.getChoice(1, 3, reader);
                    //1 -> client
                    //2 -> staff
                    //3 -> home menu
                    if (userTypeChoice == 3) {
                        continue;
                    }
                    UserType userType = UserType.getType(userTypeChoice);
                    System.out.println("Login:");
                    //  loginCredentials is null when entered login information is invalid
                    String[] loginCredentials = Menu.getLoginCredentials(reader);
                    if (loginCredentials != null) {
                        // user is null when there is no such user found in the system
                        try {
                            user = login(userType, loginCredentials[0], loginCredentials[1]);
                        } catch (WrongPasswordException | NotFoundException ex) {
                            System.out.println(ex.getMessage());
                            //user is not logged in
                            //jumps to home menu -> Login, Register, Exit
                            continue;
                        }
                        //user logged in
                        boolean logout = false;
                        while (!logout) {
                            System.out.println(String.format(ConstantMessages.WELCOME_MESSAGE, user.getFirstName(), user.getLastName()));
                            if (user instanceof Client) {
                                this.oos.writeObject("has promotions");
                                boolean storeHasPromotions = this.isServerReturnTrue();
                                if (storeHasPromotions) {
                                    Menu.printClientMenuWithPromotions();
                                    int clientSubmenuChoice = Menu.getChoice(1, 5, reader);
                                    switch (clientSubmenuChoice) {
                                        case 1:
                                            printProducts("get promotional product names");
                                            break;
                                        case 2:
                                            boolean exitProductsMenu = false;
                                            while (!exitProductsMenu) {
                                                List<String> productNames = getProductNamesFromServer("get product names");
                                                if (productNames.size() == 0) {
                                                    // there are currently no products
                                                    System.out.println(ConstantMessages.NO_PRODUCTS);
                                                    exitProductsMenu = true;
                                                    continue;
                                                }
                                                System.out.println("Products: ");
                                                int index = printProductNames(productNames);
                                                System.out.println(index + ". Back");
                                                int chosenIndexForProductName = Menu.getChoice(1, index, reader);
                                                String chosenProductName = productNames.get(chosenIndexForProductName - 1);
                                                if (chosenIndexForProductName != index) {
                                                    Product chosenProduct;
                                                    try {
                                                        chosenProduct = getProductByNameFromServer(chosenProductName);
                                                    } catch (NotFoundException e) {
                                                        System.out.println(e.getMessage());
                                                        continue;
                                                    }
                                                    System.out.println(chosenProduct);
                                                    System.out.println("1. Buy");
                                                    System.out.println("2. Back");
                                                    int productCommandChoice = Menu.getChoice(1, 2, reader);
                                                    executeClientCommandOnProduct(chosenProduct, productCommandChoice);
                                                } else {
                                                    exitProductsMenu = true;
                                                }

                                            }
                                            break;
                                        case 3:
                                            //client find product by name
                                            System.out.print(ConstantMessages.INPUT_PRODUCT_NAME);
                                            String productName = reader.readLine();
                                            try {
                                                Product product = getProductByNameFromServer(productName);
                                                boolean exitProductMenu = false;
                                                while (!exitProductMenu) {
                                                    System.out.println(product);
                                                    System.out.println("1. Buy");
                                                    System.out.println("2. Back");
                                                    int productCommandChoice = Menu.getChoice(1, 2, reader);
                                                    if (productCommandChoice == 1) {
                                                        try {
                                                            buyProduct(product);
                                                            product = getProductByNameFromServer(productName);
                                                        } catch (IllegalArgumentException ex) {
                                                            System.out.println(ex.getMessage());
                                                        } catch (NotFoundException ex) {
                                                            System.out.println(ex.getMessage());
                                                            this.oos.writeObject("back");
                                                            exitProductMenu = true;
                                                        }
                                                    } else {
                                                        this.oos.writeObject("back");
                                                        exitProductMenu = true;
                                                    }
                                                }
                                            } catch (NotFoundException e) {
                                                System.out.println(e.getMessage());

                                            }
                                            break;
                                        case 4:
                                            ClientSettingsMenu clientSettingsMenuExecutor = new ClientSettingsMenu(user).invoke();
                                            user = clientSettingsMenuExecutor.getUser();
                                            logout = clientSettingsMenuExecutor.isExit();
                                            break;
                                        case 5:
                                            this.oos.writeObject("logout");
                                            logout = true;
                                            break;
                                    }
                                } else {
                                    Menu.printClientMenuWithoutPromotions();
                                    int clientChoice = Menu.getChoice(1, 4, reader);
                                    switch (clientChoice) {
                                        case 1:
                                            printProducts("get product names");
                                            break;
                                        case 2:
                                            System.out.print("Enter name of product: ");
                                            String productName = reader.readLine();
                                            try {
                                                Product product = getProductByNameFromServer(productName);
                                                boolean exitProductMenu = false;
                                                while (!exitProductMenu) {
                                                    System.out.println(product);
                                                    System.out.println("1. Buy");
                                                    System.out.println("2. Back");
                                                    int productCommandChoice = Menu.getChoice(1, 2, reader);
                                                    if (productCommandChoice == 1) {
                                                        try {
                                                            buyProduct(product);
                                                            product = getProductByNameFromServer(productName);
                                                        } catch (IllegalArgumentException ex) {
                                                            System.out.println(ex.getMessage());
                                                        }
                                                    } else {
                                                        this.oos.writeObject("back");
                                                        exitProductMenu = true;
                                                    }
                                                }
                                            } catch (NotFoundException e) {
                                                System.out.println(e.getMessage());
                                            }

                                            break;
                                        case 3:
                                            //CLIENT SETTINGS MENU
                                            ClientSettingsMenu clientSettingsMenuExecutor = new ClientSettingsMenu(user).invoke();
                                            user = clientSettingsMenuExecutor.getUser();
                                            logout = clientSettingsMenuExecutor.isExit();
                                            break;
                                        case 4:
                                            this.oos.writeObject("logout");
                                            logout = true;
                                            break;
                                    }
                                }
                            } else if (user instanceof Staff) {
                                //staff logged in
                                Menu.printStaffMenu();
                                int staffChoice = Menu.getChoice(1, 14, reader);
                                switch (staffChoice) {
                                    case 1:
                                        //start black friday
                                        this.oos.writeObject("start blackFriday");
                                        System.out.println(ConstantMessages.START_BLACK_FRIDAY);
                                        break;
                                    case 2:
                                        //stop black friday
                                        this.oos.writeObject("stop blackFriday");
                                        System.out.println(ConstantMessages.STOP_BLACK_FRIDAY);
                                        break;
                                    case 3:
                                        //create new product option
                                        try {
                                            GetNewProductInformation getNewProductInformation = new GetNewProductInformation().invoke();
                                            String name = getNewProductInformation.getName();
                                            String description = getNewProductInformation.getDescription();
                                            int quantity = getNewProductInformation.getQuantity();
                                            double price = getNewProductInformation.getPrice();
                                            double minimumPrice = getNewProductInformation.getMinimumPrice();
                                            String size = getNewProductInformation.getSize();
                                            double discountPercent = getNewProductInformation.getDiscountPercent();


                                            Product product = new Product(name, description, quantity, price, minimumPrice, size, discountPercent);
                                            this.oos.writeObject("create product");
                                            this.oos.writeObject(product);
                                            String serverConfirmation = this.ois.readObject().toString();
                                            if (serverConfirmation.equalsIgnoreCase("successful")) {
                                                System.out.println(ConstantMessages.PRODUCT_CREATED);
                                            } else {
                                                System.out.println(serverConfirmation);
                                            }
                                        } catch (IllegalArgumentException ex) {
                                            //entered invalid data
                                            System.out.println(ex.getMessage());
                                        }
                                        break;
                                    case 4: {
                                        //products options
                                        boolean exitProductMenu = false;
                                        while (!exitProductMenu) {

                                            List<String> productNames = getProductNamesFromServer("get staff product names");
                                            if (productNames.size() == 0) {
                                                //there are currently no products
                                                System.out.println(ConstantMessages.NO_PRODUCTS);
                                                exitProductMenu = true;
                                            } else {
                                                exitProductMenu = productInteraction(productNames);
                                            }

                                        }
                                    }
                                    break;
                                    case 5:
                                        //staff show all promotional products
                                        boolean exitProductMenu = false;
                                        while (!exitProductMenu) {

                                            List<String> productNames = getProductNamesFromServer("get staff promotional products names");
                                            if (productNames.isEmpty()) {
                                                System.out.println(ConstantMessages.NO_DISCOUNTED_PRODUCTS);
                                                exitProductMenu = true;
                                            } else {
                                                exitProductMenu = productInteraction(productNames);
                                            }
                                        }
                                        break;
                                    case 6:
                                        //show all product below certain quantity
                                        try {
                                            boolean exitProductNames = false;
                                            while (!exitProductNames) {
                                                System.out.println("Please enter maximum quantity");
                                                int maximumQuantity = Integer.parseInt(reader.readLine());
                                                this.oos.writeObject("get product names with maximum quantity");
                                                this.oos.writeObject(maximumQuantity);
                                                List<String> productNames = (List<String>) this.ois.readObject();
                                                if (productNames.isEmpty()) {
                                                    System.out.println(ConstantMessages.NO_PRODUCTS);
                                                    exitProductNames = true;
                                                } else {
                                                    exitProductNames = productInteraction(productNames);
                                                }
                                            }
                                        } catch (NumberFormatException ex) {
                                            System.out.println(ExceptionMessages.INVALID_NUMBER);
                                        }
                                        break;
                                    case 7:
                                        //staff search product by name

                                        System.out.println("Please enter the name of the product: ");
                                        String productName = reader.readLine();

                                        try {
                                            productOption(productName);
                                        } catch (NotFoundException e) {
                                            System.out.println(e.getMessage());
                                        }
                                        break;
                                    case 8:
                                        //earnings for date
                                        try {
                                            LocalDate date = getDate();
                                            //input from user is valid
                                            this.oos.writeObject("earnings date");
                                            this.oos.writeObject(date);
                                            double earnings = (double) this.ois.readObject();
                                            System.out.println(String.format(
                                                    ConstantMessages.EARNINGS_FOR_DATE,
                                                    date.getYear(),
                                                    date.getMonth().getValue(),
                                                    date.getDayOfMonth(),
                                                    earnings));
                                        } catch (NumberFormatException ex) {
                                            System.out.println(ExceptionMessages.ENTER_NUMBER);
                                        } catch (DateTimeException ex) {
                                            System.out.println(ExceptionMessages.INVALID_DATE);
                                        }
                                        break;
                                    case 9:
                                        //get earning for month
                                        try {
                                            System.out.print(ConstantMessages.INPUT_MONTH);
                                            int month = Integer.parseInt(reader.readLine());
                                            System.out.print(ConstantMessages.INPUT_YEAR);
                                            int year = Integer.parseInt(reader.readLine());
                                            this.oos.writeObject("earnings month");
                                            this.oos.writeObject(month);
                                            this.oos.writeObject(year);
                                            double earnings = (double) this.ois.readObject();
                                            System.out.println(String.format(ConstantMessages.EARNINGS_MONTH, month, earnings));
                                        } catch (NumberFormatException ex) {
                                            System.out.println(ExceptionMessages.ENTER_NUMBER);
                                        }
                                        break;
                                    case 10:
                                        //get earnings for period of time
                                        try {
                                            System.out.println(ConstantMessages.INPUT_START_PERIOD);
                                            LocalDate startDate = getDate();
                                            System.out.println(ConstantMessages.INPUT_END_PERIOD);
                                            LocalDate endDate = getDate();

                                            // dates input is successful
                                            if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
                                                System.out.println(ConstantMessages.END_DATE_IS_BEFORE_START_DATE);
                                            } else {
                                                // the start and end date are valid
                                                this.oos.writeObject("earnings period");
                                                this.oos.writeObject(startDate);
                                                this.oos.writeObject(endDate);
                                                double earnings = (double) this.ois.readObject();
                                                System.out.println(String.format(ConstantMessages.EARNINGS_PERIOD, earnings));
                                            }
                                        } catch (NumberFormatException ex) {
                                            System.out.println(ExceptionMessages.ENTER_NUMBER);
                                        } catch (DateTimeException ex) {
                                            System.out.println(ExceptionMessages.INVALID_DATE);
                                        }
                                        break;
                                    case 11:
                                        //get earnings for year
                                        this.oos.writeObject("earnings year");
                                        System.out.print(ConstantMessages.INPUT_YEAR);
                                        try {
                                            int year = Integer.parseInt(reader.readLine());
                                            this.oos.writeObject(year);
                                            double earnings = (double) this.ois.readObject();
                                            System.out.println(String.format(ConstantMessages.EARNINGS_YEAR, year, earnings));
                                        } catch (NumberFormatException ex) {
                                            System.out.println(ExceptionMessages.ENTER_NUMBER);
                                        }
                                        break;
                                    case 12:
                                        //staff settings menu
                                        int staffSettingsChoice = 1;
                                        while ((staffSettingsChoice != 8 && staffSettingsChoice != 7) || logout) {
                                            Menu.printStaffSettings();
                                            staffSettingsChoice = Menu.getChoice(1, 8, reader);
                                            try {
                                                switch (staffSettingsChoice) {
                                                    case 1:
                                                        // change first name
                                                        user = changeUserProperties("change first name",
                                                                ConstantMessages.FIRST_NAME_CHANGE_SUCCESSFUL,
                                                                ConstantMessages.FIRST_NAME_CHANGE_UNSUCCESSFUL,
                                                                ConstantMessages.INPUT_FIRST_NAME);
                                                        break;
                                                    case 2:
                                                        // change last name
                                                        user = changeUserProperties("change last name",
                                                                ConstantMessages.LAST_NAME_CHANGE_SUCCESSFUL,
                                                                ConstantMessages.LAST_NAME_CHANGE_UNSUCCESSFUL,
                                                                ConstantMessages.INPUT_LAST_NAME);
                                                        break;
                                                    case 3:
                                                        // change username
                                                        user = changeUserProperties("change username",
                                                                ConstantMessages.USERNAME_CHANGE_SUCCESSFUL,
                                                                ConstantMessages.USERNAME_CHANGE_UNSUCCESSFUL,
                                                                ConstantMessages.INPUT_USERNAME);
                                                        break;
                                                    case 4:
                                                        // change password
                                                        user = changeUserProperties("change password",
                                                                ConstantMessages.PASSWORD_CHANGE_SUCCESSFUL,
                                                                ConstantMessages.PASSWORD_CHANGE_UNSUCCESSFUL,
                                                                ConstantMessages.INPUT_PASSWORD);
                                                        break;
                                                    case 5:
                                                        // change age
                                                        user = changeUserAge();
                                                        break;
                                                    case 6:
                                                        // delete client account
                                                        Menu.printDeleteClientSubMenu();
                                                        int staffChoiceDeleteClient = Menu.getChoice(1, 2, reader);
                                                        switch (staffChoiceDeleteClient) {
                                                            case 1:
                                                                //show all client accounts
                                                                this.oos.writeObject("get all client accounts username");
                                                                List<String> allClientUsername = (List<String>) this.ois.readObject();
                                                                if (allClientUsername.size() == 0) {
                                                                    System.out.println("There are no client registered");
                                                                } else {
                                                                    int index = 1;
                                                                    for (String clientUsername : allClientUsername) {
                                                                        System.out.println(index++ + ". " + clientUsername);
                                                                    }
                                                                    System.out.println(index + ". Back");
                                                                    int usernameChoice = Menu.getChoice(1, index, reader);
                                                                    if (usernameChoice != index) {
                                                                        //user chose client username
                                                                        String usernameOfChosenUser = allClientUsername.get(usernameChoice - 1);
                                                                        this.oos.writeObject("delete client by username");
                                                                        this.oos.writeObject(usernameOfChosenUser);
                                                                        if (isServerReturnTrue()) {
                                                                            System.out.println(ConstantMessages.CLIENT_DELETED_SUCCESSFUL);
                                                                        } else {
                                                                            System.out.println(String.format(ConstantMessages.CLIENT_NOT_EXIST, usernameOfChosenUser));
                                                                        }
                                                                    }
                                                                }
                                                                break;
                                                            case 2:
                                                                //delete client account by name
                                                                System.out.print("Enter client account username: ");
                                                                String username = reader.readLine();
                                                                try {
                                                                    Validator.validateString(username);
                                                                    this.oos.writeObject("delete client by username");
                                                                    this.oos.writeObject(username);
                                                                    if (isServerReturnTrue()) {
                                                                        System.out.println(ConstantMessages.CLIENT_DELETED_SUCCESSFUL);
                                                                    } else {
                                                                        System.out.println(String.format(ConstantMessages.CLIENT_NOT_EXIST, username));
                                                                    }

                                                                } catch (IllegalArgumentException ex) {
                                                                    System.out.println(ex.getMessage());
                                                                }
                                                                break;
                                                        }
                                                        break;
                                                    case 7:
                                                        //delete this staff account
                                                        this.oos.writeObject("delete staff");
                                                        if (isServerReturnTrue()) {
                                                            System.out.println(ConstantMessages.STAFF_DELETED_SUCCESSFUL);
                                                        } else {
                                                            System.out.println(ConstantMessages.STAFF_DELETED_UNSUCCESSFUL);
                                                        }
                                                        logout = true;
                                                        break;

                                                }
                                            } catch (IllegalArgumentException ex) {
                                                System.out.println(ex.getMessage());
                                            }
                                        }
                                        break;
                                    case 13:
                                        try {
                                            Staff staffToBeRegistered = getStaffToBeRegistered();
                                            if (registerUser(staffToBeRegistered)) {
                                                System.out.println(ConstantMessages.STAFF_REGISTERED);
                                            } else {
                                                System.out.println(ConstantMessages.STAFF_ALREADY_EXISTS);
                                            }
                                        } catch (NumberFormatException ex) {
                                            System.out.println(ex.getMessage());
                                        }
                                        break;
                                    case 14:
                                        logout = true;
                                        this.oos.writeObject("logout");
                                }
                            }

                        }

                    }


                    break;
                case 2:
                    //register client
                    try {
                        Client toBeRegisteredClient = getClientToBeRegistered();
                        if (registerUser(toBeRegisteredClient)) {
                            System.out.println(ConstantMessages.REGISTERED_SUCCESSFULLY);
                            break;
                        } else {
                            System.out.println(ConstantMessages.CLIENT_ALREADY_EXISTS);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 3:
                    //exit program
                    this.oos.close();
                    this.ois.close();
                    this.reader.close();
                    this.socket.close();
                    //TODO close program end all streams
                    return;
            }
        }

    }

    private boolean productInteraction(List<String> productNames) throws IOException, ClassNotFoundException {
        int index = printProductNames(productNames);
        System.out.println(index + ". Back");

        int indexOfChosenProductName = Menu.getChoice(1, index, reader);
        if (indexOfChosenProductName == index) {
            return true;
        }
        String chosenProductName = productNames.get(indexOfChosenProductName - 1);

        try {
            productOption(chosenProductName);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    private void productOption(String chosenProductName) throws IOException, ClassNotFoundException, NotFoundException {
        boolean exitProductSettings = false;
        while (!exitProductSettings) {

            Product chosenProduct = getProductByNameFromServer(chosenProductName);
            System.out.println("Product: ");
            System.out.println(chosenProduct);
            System.out.println("Minimum price: " + chosenProduct.getMinimumPrice());

            printStaffProductOptions();

            int staffCommandChoice = Menu.getChoice(1, 7, reader);

            ExecuteStaffCommandOnProduct executeStaffCommandOnProduct = new ExecuteStaffCommandOnProduct(chosenProductName, exitProductSettings, chosenProduct, staffCommandChoice).invoke();
            chosenProductName = executeStaffCommandOnProduct.getProductName();
            exitProductSettings = executeStaffCommandOnProduct.isExitChosenProduct();


        }
    }

    /**
     * Checks if the server return true or false when it returns a boolean
     *
     * @return if the server returns true of false
     * @throws IOException            if I/O error occurs
     * @throws ClassNotFoundException if the server returns something
     *                                different from a boolean
     */
    private boolean isServerReturnTrue() throws IOException, ClassNotFoundException {
        return (boolean) this.ois.readObject();
    }

    /**
     * Executes asks the user to enter wanted quantity and buys that amount
     * prints if the purchase was successful
     *
     * @param product which is being bought
     * @throws IOException            if I/O error occurs
     * @throws ClassNotFoundException from isServerReturnTrue method
     */
    private void buyProduct(Product product) throws IOException, ClassNotFoundException {
        System.out.println(ConstantMessages.ENTER_QUANTITY);
        System.out.println(ConstantMessages.ENTER_ZERO_BACK);
        int wantedQuantity = Menu.getChoice(0, product.getQuantity(), reader);
        if (wantedQuantity != 0) {
            this.oos.writeObject("buy product");
            this.oos.writeObject(product.getName());
            this.oos.writeObject(wantedQuantity);
            boolean isBought = this.isServerReturnTrue();
            if (isBought) {
                System.out.println(String.format(
                        SUCCESSFUL_PURCHASE,
                        wantedQuantity,
                        product.getName()));
            } else {
                System.out.println(UNSUCCESSFUL_PURCHASE);
            }
        }
    }

//    /**
//     * Wait for user to press enter in the console
//     * to continue the program execution
//     *
//     * @throws IOException if I/O error occurs
//     */
//    public void pressEnterToContinue() throws IOException {
//        System.out.println("Press enter to continue!");
//        System.in.read();
//    }

    /**
     * @param product              the client chosen product
     * @param productCommandChoice the command chosen by the user
     *                             1 -> buy product
     *                             2 -> go back
     * @throws IOException            if I/O error occurs
     * @throws ClassNotFoundException from buyProduct method
     */
    private void executeClientCommandOnProduct(Product product, int productCommandChoice) throws IOException, ClassNotFoundException {
        if (productCommandChoice == 1) {
            buyProduct(product);
        } else {
            this.oos.writeObject(ConstantMessages.GO_BACK);
        }
    }

    /**
     * @return LocalDate entered from the user
     * @throws IOException form BufferedReader if an I/O error occurs
     */
    private LocalDate getDate() throws IOException {
        //get input from user
        System.out.print(ConstantMessages.INPUT_YEAR);
        int year = Integer.parseInt(reader.readLine());
        System.out.print(ConstantMessages.INPUT_MONTH);
        int month = Integer.parseInt(reader.readLine());
        System.out.print(ConstantMessages.INPUT_DAY);
        int dayOfTheMonth = Integer.parseInt(reader.readLine());

        return LocalDate.of(year, month, dayOfTheMonth);
    }

    /**
     * @param serverCommand command sent to the server to get
     *                      product names
     */
    private List<String> getProductNamesFromServer(String serverCommand) throws IOException, ClassNotFoundException {
        this.oos.writeObject(serverCommand);
        return (List<String>) this.ois.readObject();
    }

    /**
     * Prints all the Staff options on products
     */
    private void printStaffProductOptions() {
        System.out.println("1. Set product name");
        System.out.println("2. Set product description");
        System.out.println("3. Set product quantity");
        System.out.println("4. Set product minimum price");
        System.out.println("5. Set discount percent");
        System.out.println("6. Delete product: ");
        System.out.println("7. Back");
    }

    /**
     * Checks if the server contains a product with the name productName
     *
     * @param productName the name of the searched product
     * @return The product if it exists or @throws NotFoundException
     */
    private Product getProductByNameFromServer(String productName) throws IOException, ClassNotFoundException, NotFoundException {
        this.oos.writeObject("get product by name");
        this.oos.writeObject(productName);
        Product product = (Product) this.ois.readObject();
        if (product == null) {
            throw new NotFoundException(String.format(
                    ExceptionMessages.PRODUCT_NOT_FOUND,
                    productName
            ));
        }
        return product;
    }

    /**
     * @param productNames list with names of products
     * @return number of product names
     */
    private int printProductNames(List<String> productNames) {
        int index = 1;
        for (String productName : productNames) {
            System.out.println(index++ + ". " + productName);
        }
        return index;
    }

    /**
     * @param serverCommand command send to the server
     * @throws IOException            if I/O error occurs
     * @throws ClassNotFoundException if server returns a non existent class
     */
    private void printProducts(String serverCommand) throws IOException, ClassNotFoundException {
        boolean exitProductsMenu = false;
        while (!exitProductsMenu) {
            List<String> productNames = getProductNamesFromServer(serverCommand);
            System.out.println("Products: ");
            int index = 1;
            for (String productName : productNames) {
                System.out.println(index++ + ". " + productName);
            }
            System.out.println(index + ". Back");
            int productChoiceIndex = Menu.getChoice(1, index, reader);
            if (productChoiceIndex != index) {
                this.oos.writeObject("get product by name");
                this.oos.writeObject(productNames.get(productChoiceIndex - 1));
                Product chosenProduct = (Product) this.ois.readObject();
                if (chosenProduct != null) {
                    System.out.println(chosenProduct);
                    System.out.println("1. Buy");
                    System.out.println("2. Back");
                    int productCommandChoice = Menu.getChoice(1, 2, reader);
                    executeClientCommandOnProduct(chosenProduct, productCommandChoice);
                }
            }
            exitProductsMenu = true;
        }
    }

    /**
     * @param commandToServer      the command send to the server
     *                             changes based on the option chosen by the user
     * @param printIfChangeWasMade prints this string trough the BufferedReader reader
     *                             if the change was successful
     * @param printIfChangeFailed  prints this string through the BufferedReader reader
     *                             if the change failed
     * @param messageToUser        prints this string trough the BufferedReader reader
     *                             to the user
     * @return modified User
     * @throws IOException            if I/O error occurs
     * @throws ClassNotFoundException if the returned object from
     *                                the server is not found
     */
    private User changeUserProperties(String commandToServer,
                                      String printIfChangeWasMade,
                                      String printIfChangeFailed,
                                      String messageToUser) throws IOException, ClassNotFoundException {
        User user;
        System.out.println(messageToUser);
        String property = reader.readLine();
        validateString(property);
        this.oos.writeObject(commandToServer);
        this.oos.writeObject(property);
        if ((boolean) (this.ois.readObject())) {
            System.out.println(printIfChangeWasMade);
        } else {
            System.out.println(printIfChangeFailed);
        }
        user = (User) this.ois.readObject();
        return user;
    }

    /**
     * Gets the new age from user and changes age property of an User object
     *
     * @return User object with modified age
     * @throws IOException              if I/O error occurs
     * @throws ClassNotFoundException   if the returned class from the server is not found
     * @throws IllegalArgumentException if age is negative is zero
     */
    private User changeUserAge() throws IOException, ClassNotFoundException {
        System.out.println(ConstantMessages.INPUT_PRODUCT_AGE);
        int newAge = Integer.parseInt(reader.readLine());
        validateAge(newAge);
        this.oos.writeObject("change age");
        this.oos.writeObject(newAge);
        if ((boolean) (this.ois.readObject())) {
            System.out.println(ConstantMessages.AGE_CHANGE_SUCCESSFUL);
        } else {
            System.out.println(ConstantMessages.AGE_CHANGE_UNSUCCESSFUL);
        }
        return (User) this.ois.readObject();
    }

    /**
     * @return Client object created with user
     * entered data from registerInformation object
     * @throws IOException if I/O error occurs
     */
    private Client getClientToBeRegistered() throws IOException {
        RegisterInformation registerInformation = new RegisterInformation().invoke();
        String firstName = registerInformation.getFirstName();
        String lastName = registerInformation.getLastName();
        String username = registerInformation.getUsername();
        String password = registerInformation.getPassword();
        int age = registerInformation.getAge();
        return new Client(username, password, firstName, lastName, age);
    }

    /**
     * @return Staff object created with user
     * entered data from registerInformation object
     * @throws IOException if I/O error occurs
     */
    private Staff getStaffToBeRegistered() throws IOException {
        RegisterInformation registerInformation = new RegisterInformation().invoke();
        String firstName = registerInformation.getFirstName();
        String lastName = registerInformation.getLastName();
        String username = registerInformation.getUsername();
        String password = registerInformation.getPassword();
        int age = registerInformation.getAge();
        return new Staff(username, password, firstName, lastName, age);
    }

    /**
     * @param toBeRegisteredUser User object which is going to be registered in the system
     * @return if the user was registered
     */
    private boolean registerUser(User toBeRegisteredUser) {
        try {
            if (toBeRegisteredUser instanceof Client) {
                this.oos.writeObject("register client");
            } else if (toBeRegisteredUser instanceof Staff) {
                this.oos.writeObject("register staff");
            }
            this.oos.writeObject(toBeRegisteredUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return this.ois.readObject().equals("true");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * attempts to login user
     *
     * @param type     the type of user
     * @param username username of user
     * @param password password of user
     * @return user object when login is successful
     * or null when the user doesn't exits or the
     * password is wrong
     */
    private User login(UserType type, String username, String password) throws WrongPasswordException, NotFoundException, IOException, ClassNotFoundException {
        StringBuilder sb = new StringBuilder();

        this.oos.writeObject("login");
        sb.append(type.toString().toLowerCase()).append(" ").append(username).append(" ").append(password);
        this.oos.writeObject(sb.toString());
        User user = (User) this.ois.readObject();
        if (user == null) {
            String exceptionType = this.ois.readObject().toString();
            if (exceptionType.equals("WrongPasswordException")) {
                throw new WrongPasswordException(ExceptionMessages.WRONG_PASSWORD);
            } else if (exceptionType.equals("NotFoundException")) {
                throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND, username));
            }
        }
        return user;
    }

    /**
     * Gets information from reader object needed to register a client
     */
    private class RegisterInformation {
        private String firstName;
        private String lastName;
        private String username;
        private String password;
        private int age;

        String getFirstName() {
            return firstName;
        }

        String getLastName() {
            return lastName;
        }

        String getUsername() {
            return username;
        }

        String getPassword() {
            return password;
        }

        int getAge() {
            return age;
        }

        RegisterInformation invoke() throws IOException {
            System.out.println("Enter your first name");
            firstName = Store.this.reader.readLine();
            System.out.println("Enter your last name");
            lastName = Store.this.reader.readLine();
            System.out.println("Enter your username");
            username = Store.this.reader.readLine();
            System.out.println("Enter your password name");
            password = Store.this.reader.readLine();
            System.out.println("Enter your age");
            age = Integer.parseInt(Store.this.reader.readLine());
            return this;
        }
    }

    /**
     * Prints client possible operation on Product. Gets user input for wanted operation.
     * Executes specified command by calling a function
     * Executes Client commands on a while loop until either the user selects back option or
     * chooses to delete the product
     */
    private class ClientSettingsMenu {
        private User user;
        private boolean exit;

        ClientSettingsMenu(User user) {
            this.user = user;
            this.exit = false;
        }

        public User getUser() {
            return user;
        }

        boolean isExit() {
            return exit;
        }

        ClientSettingsMenu invoke() throws IOException, ClassNotFoundException {
            int clientSettingsChoice = 1;
            while (clientSettingsChoice != 7 && clientSettingsChoice != 6) {
                Menu.printClientSetting();
                clientSettingsChoice = Menu.getChoice(1, 7, Store.this.reader);
                try {
                    switch (clientSettingsChoice) {
                        case 1:
                            user = changeUserProperties("change first name",
                                    ConstantMessages.FIRST_NAME_CHANGE_SUCCESSFUL,
                                    ConstantMessages.FIRST_NAME_CHANGE_UNSUCCESSFUL,
                                    ConstantMessages.INPUT_FIRST_NAME);
                            break;
                        case 2:
                            user = changeUserProperties("change last name",
                                    ConstantMessages.LAST_NAME_CHANGE_SUCCESSFUL,
                                    ConstantMessages.LAST_NAME_CHANGE_UNSUCCESSFUL,
                                    ConstantMessages.INPUT_LAST_NAME);
                            break;
                        case 3:
                            user = changeUserProperties("change username",
                                    ConstantMessages.USERNAME_CHANGE_SUCCESSFUL,
                                    ConstantMessages.USERNAME_CHANGE_UNSUCCESSFUL,
                                    ConstantMessages.INPUT_USERNAME);
                            break;
                        case 4:
                            user = changeUserProperties("change password",
                                    ConstantMessages.PASSWORD_CHANGE_SUCCESSFUL,
                                    ConstantMessages.PASSWORD_CHANGE_UNSUCCESSFUL,
                                    ConstantMessages.INPUT_PASSWORD);
                            break;
                        case 5:
                            user = changeUserAge();
                            break;
                        case 6:
                            Store.this.oos.writeObject("delete client");

                            if ((boolean) Store.this.ois.readObject()) {
                                System.out.println(ConstantMessages.CLIENT_DELETED_SUCCESSFUL);
                            } else {
                                System.out.println(ConstantMessages.CLIENT_DELETED_UNSUCCESSFUL);
                            }
                            exit = true;
                            break;
                    }
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            return this;
        }
    }


    /**
     * Execute all commands on products available to the Staff User
     *
     * <p>Constructor parameters
     * productName name of the product the operations are being preformed on
     * exitChosenProduct used to end while cycle for executing staff commands outside of the method object
     * product the porduct operations are being performed on
     * staffCommandChoice the user's choice of command
     */
    private class ExecuteStaffCommandOnProduct {
        private String chosenProductName;
        private boolean exitChosenProduct;
        private Product chosenProduct;
        private int staffCommandChoice;

        ExecuteStaffCommandOnProduct(String productName, boolean exitChosenProduct, Product product, int staffCommandChoice) {
            this.chosenProductName = productName;
            this.exitChosenProduct = exitChosenProduct;
            this.chosenProduct = product;
            this.staffCommandChoice = staffCommandChoice;
        }

        String getProductName() {
            return chosenProductName;
        }

        boolean isExitChosenProduct() {
            return exitChosenProduct;
        }


        ExecuteStaffCommandOnProduct invoke() throws IOException, ClassNotFoundException {
            switch (staffCommandChoice) {
                case 1:
                    Store.this.oos.writeObject("change product name");
                    System.out.print(ConstantMessages.INPUT_PRODUCT_NAME);
                    String newName = reader.readLine();
                    Store.this.oos.writeObject(newName);
                    if (isServerReturnTrue()) {
                        chosenProductName = newName;
                        System.out.println(ConstantMessages.PRODUCT_NAME_CHANGED_SUCCESSFUL);
                    } else {
                        System.out.println(ConstantMessages.PRODUCT_NAME_CHANGED_UNSUCCESSFUL);
                    }

                    break;
                case 2:
                    Store.this.oos.writeObject("change product description");
                    System.out.print(ConstantMessages.INPUT_PRODUCT_DESCRIPTION);
                    String newDescription = reader.readLine();
                    Store.this.oos.writeObject(newDescription);
                    if (isServerReturnTrue()) {
                        System.out.println(ConstantMessages.PRODUCT_DESCRIPTION_CHANGED_SUCCESSFUL);
                    } else {
                        System.out.println(ConstantMessages.PRODUCT_DESCRIPTION_CHANGED_UNSUCCESSFUL);
                    }
                    break;
                case 3:
                    Store.this.oos.writeObject("change product quantity");
                    System.out.print(ConstantMessages.INPUT_PRODUCT_QUANTITY);
                    int newQuantity = Integer.parseInt(reader.readLine());
                    Store.this.oos.writeObject(newQuantity);
                    if (isServerReturnTrue()) {
                        System.out.println(ConstantMessages.PRODUCT_QUANTITY_CHANGED_SUCCESSFUL);
                    } else {
                        System.out.println(ConstantMessages.PRODUCT_QUANTITY_CHANGED_UNSUCCESSFUL);
                    }
                    break;
                case 4:
                    Store.this.oos.writeObject("change product minimum price");
                    System.out.print(ConstantMessages.INPUT_PRODUCT_MINIMUM_PRICE);
                    try {
                        double minimumPrice = Double.parseDouble(reader.readLine());
                        Store.this.oos.writeObject(minimumPrice);
                        if (isServerReturnTrue()) {
                            System.out.println(ConstantMessages.PRODUCT_MINIMUM_PRICE_CHANGED_SUCCESSFUL);
                        } else {
                            System.out.println(ConstantMessages.PRODUCT_MINIMUM_PRICE_CHANGED_UNSUCCESSFUL);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println(ExceptionMessages.INVALID_NUMBER);
                    }
                    break;
                case 5:
                    Store.this.oos.writeObject("change product discount percent");
                    System.out.print(ConstantMessages.INPUT_PRODUCT_DISCOUNT_PERCENT);
                    double newDiscountPercent = Double.parseDouble(reader.readLine());
                    Store.this.oos.writeObject(newDiscountPercent);
                    if (isServerReturnTrue()) {
                        System.out.println(ConstantMessages.PRODUCT_DISCOUNT_PERCENT_CHANGED_SUCCESSFUL);
                    } else {
                        System.out.println(ConstantMessages.PRODUCT_DISCOUNT_PERCENT_CHANGED_UNSUCCESSFUL);
                    }
                    break;
                case 6:
                    Store.this.oos.writeObject("delete product");
                    Store.this.oos.writeObject(chosenProduct.getName());
                    if (isServerReturnTrue()) {
                        exitChosenProduct = true;
                    }
                    break;
                case 7:
                    Store.this.oos.writeObject("exit product options");
                    exitChosenProduct = true;
                    break;
            }
            return this;
        }
    }

    /**
     * Used for getting all information need to create a
     * new product from BufferedReader reader object
     */
    private class GetNewProductInformation {

        private String name;
        private String description;
        private int quantity;
        private double price;
        private double minimumPrice;
        private String size;
        private double discountPercent;

        String getName() {
            return name;
        }

        String getDescription() {
            return description;
        }

        int getQuantity() {
            return quantity;
        }

        double getPrice() {
            return price;
        }

        double getMinimumPrice() {
            return minimumPrice;
        }

        String getSize() {
            return size;
        }

        double getDiscountPercent() {
            return discountPercent;
        }

        GetNewProductInformation invoke() throws IOException {
            System.out.println("Creating new product");
            System.out.println("Please enter product values");
            System.out.print("Product name: ");
            name = reader.readLine();
            System.out.print("Product description: ");
            description = reader.readLine();
            System.out.print("Product quantity: ");
            quantity = Integer.parseInt(reader.readLine());
            System.out.print("Product price: ");
            price = Double.parseDouble(reader.readLine());
            System.out.print("Product minimum price: ");
            minimumPrice = Double.parseDouble(reader.readLine());
            System.out.print("Product size (can be left blank if needed): ");
            size = reader.readLine();
            size = size.equalsIgnoreCase("") ? null : size;
            System.out.print("Product discount percent: ");
            discountPercent = Double.parseDouble(reader.readLine());
            return this;
        }
    }
}
