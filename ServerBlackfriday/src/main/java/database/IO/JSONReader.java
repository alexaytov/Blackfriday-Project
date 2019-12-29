package database.IO;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import product.Product;
import store.earnings.Purchase;
import user.Client;
import user.Staff;
import user.interfaces.User;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONReader {

    /**
     * Reads all products written in a file with name (@code filePath)
     * <p>
     * If file doesn't exits it creates an empty one
     *
     * @param filePath the file path for the product database file
     * @return Map with key - product name and value - product fields
     */
    public static Map<String, Product> readProducts(String filePath) {
        List<Product> products = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        File productsDB = new File(filePath);
        if (!productsDB.exists()) {
            try {
                productsDB.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        HashMap<String, Product> readProducts = new HashMap<>();
        try (FileReader fileReader = new FileReader(productsDB)) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);

            for (Object kvpAsObject : jsonObject.entrySet()) {
                Map.Entry<String, Map<String, Object>> kvp = (Map.Entry<String, Map<String, Object>>) kvpAsObject;

                String name = kvp.getKey();
                String description = kvp.getValue().get("description").toString();
                int quantity = ((Long) kvp.getValue().get("quantity")).intValue();
                double price = Double.parseDouble(kvp.getValue().get("price").toString());
                double promotionalPricePercent = Double.parseDouble(kvp.getValue().get("promotionalPricePercent").toString());
                double minimumPrice = Double.parseDouble(kvp.getValue().get("minimumPrice").toString());
                Product product;
                try {
                    String size = kvp.getValue().get("size").toString();
                    product = new Product(name, description, quantity, price, minimumPrice, size, promotionalPricePercent);
                } catch (NullPointerException ex) {
                    product = new Product(name, description, quantity, price, minimumPrice, promotionalPricePercent);
                }
                readProducts.put(product.getName(), product);
            }
            return readProducts;
        } catch (ParseException ex) {
            return new HashMap<>();
        }catch (IOException ex){
            ex.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Reads all purchases written in a file with name (@code filePath)
     * <p>
     * If file doesn't exits it creates an empty one
     *
     * @param filePath  the file path to the file used for users database
     * @param classType the class of user
     * @param <T>       Class which extends the user interface
     * @return Map with key - user's username and values - user's fields
     * @throws IOException if I/O error occurs
     */
    public static <T extends User> Map<String, T> readUsers(String filePath, String classType) throws IOException {

        JSONParser jsonParser = new JSONParser();
        File clientDBFile = new File(filePath);
        if (!clientDBFile.exists()) {
            clientDBFile.createNewFile();
        }
        try (FileReader fileReader = new FileReader(clientDBFile)) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
            Map<String, Map<String, Object>> usersInformationMap = jsonObject;
            Map<String, T> data = new HashMap<>();
            for (Map.Entry<String, Map<String, Object>> stringMapEntry : usersInformationMap.entrySet()) {
                String username = stringMapEntry.getKey();
                Map<String, Object> userParameters = stringMapEntry.getValue();
                String password = (String) userParameters.get("password");
                String firstName = (String) userParameters.get("firstName");
                String lastName = (String) userParameters.get("lastName");
                int age = ((Long) userParameters.get("age")).intValue();
                ZonedDateTime dateOfCreation = ZonedDateTime.parse(userParameters.get("dateOfCreation").toString());
                T object = null;
                if (classType.equals("client")) {
                    object = (T) new Client(username, password, firstName, lastName, age, dateOfCreation);
                } else if (classType.equals("staff")) {
                    object = (T) new Staff(username, password, firstName, lastName, age, dateOfCreation);
                }
                data.put(username, object);
            }
            return data;
        } catch (ParseException e) {
            return new HashMap<>();
        }
    }

    /**
     * Reads all purchases written in a file with name (@code filePath)
     * <p>
     * If file doesn't exits it creates an empty one
     *
     * @param filePath file path of the file in which purchases were written
     * @return Map with key - name of the user doing the purchase and keys - all the purchases the user made
     * if file has no purchases returns empty map
     * @throws IOException if I/O error occurs
     */
    public static Map<String, List<Purchase>> readPurchases(String filePath) throws IOException {

        JSONParser jsonParser = new JSONParser();
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        Map<String, List<Purchase>> readPurchases = new HashMap<>();

        try (FileReader fileReader = new FileReader(filePath)) {
            Map<String, List<Map<String, Object>>> purchases = (Map<String, List<Map<String, Object>>>) jsonParser.parse(fileReader);

            for (Map.Entry<String, List<Map<String, Object>>> purchaseEntry : purchases.entrySet()) {
                List<Map<String, Object>> listOfPurchasedValues = purchaseEntry.getValue();

                List<Purchase> listOfPurchases = new ArrayList<>();
                for (Map<String, Object> purchasedValue : listOfPurchasedValues) {
                    String productName = purchasedValue.get("productName").toString();
                    String userName = purchasedValue.get("productName").toString();
                    Long quantity = (Long) purchasedValue.get("quantity");
                    double price = (double) purchasedValue.get("price");
                    ZonedDateTime date = ZonedDateTime.parse(purchasedValue.get("date").toString());
                    Purchase purchase = new Purchase(productName, userName, quantity.intValue(), price, date);
                    listOfPurchases.add(purchase);
                }
                readPurchases.put(purchaseEntry.getKey(), listOfPurchases);
            }
            return readPurchases;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        } catch (ParseException e) {
            return new HashMap<>();
        }
    }

}
