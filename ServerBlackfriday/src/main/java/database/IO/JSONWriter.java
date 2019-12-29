package database.IO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import product.Product;
import store.earnings.Purchase;
import user.interfaces.User;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static validator.Validator.validateName;

public class JSONWriter {

    /**
     * Writes products to file with (code filePath)
     * by map key - name of Product and value - fields of the product
     *
     * @param products the products to be written
     * @param filePath file path of the product database file
     */
    public static synchronized void writeProducts(Map<String, Product> products, String filePath) {
        validateName(filePath);

        JSONObject jsonObject = new JSONObject();
        JSONArray productsArray = new JSONArray();
        for (Map.Entry<String, Product> kvp : products.entrySet()) {
            Product product = kvp.getValue();
            Map<String, Object> values = new HashMap<>();
            values.put("description", product.getDescription());
            values.put("quantity", product.getQuantity());
            values.put("price", product.getPrice());
            values.put("promotionalPricePercent", product.getDiscountPercent());
            values.put("minimumPrice", product.getMinimumPrice());
            values.put("size", product.getSize());

            jsonObject.put(product.getName(), values);

        }
//        jsonObject.put("products", productsArray);

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            jsonObject.writeJSONString(fileWriter);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    /**
     * Writes users to file with (@code filePath)
     * by map key - username of the User and value - fields of the User
     *
     * @param users    the users to be written
     * @param filePath the file path to the file with the users database
     * @param <T>      Object which extends User interface
     */
    public static synchronized <T extends User> void writeUsers(Map<String, T> users, String filePath) {
        validateName(filePath);

        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, T> user : users.entrySet()) {
            JSONObject userMap = new JSONObject();
            userMap.put("password", user.getValue().getPassword());
            userMap.put("firstName", user.getValue().getFirstName());
            userMap.put("lastName", user.getValue().getLastName());
            userMap.put("age", user.getValue().getAge());
            userMap.put("dateOfCreation", user.getValue().getDateOfCreation().toString());
            jsonObject.put(user.getKey(), userMap);
        }
        try (FileWriter writer = new FileWriter(filePath, false)) {
            jsonObject.writeJSONString(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes purchases to file with (@code filePath)
     * by map key - username of the User buyer and value - fields of the purchase
     *
     * @param purchases the purchases to be written
     * @param filePath  the file path to the file with the purchases database
     */
    public static synchronized void writePurchase(Map<String, List<Purchase>> purchases, String filePath) {
        validateName(filePath);

        JSONObject jsonObject = new JSONObject();
        // key -> id value -> purchase
        JSONArray listOfPurchasesValues = new JSONArray();
        JSONObject purchaseValues = new JSONObject();
        for (Map.Entry<String, List<Purchase>> purchaseEntry : purchases.entrySet()) {
            for (Purchase purchase : purchaseEntry.getValue()) {
                purchaseValues.put("productName", purchase.getProductName());
                purchaseValues.put("userName", purchase.getUserName());
                purchaseValues.put("quantity", purchase.getQuantity());
                purchaseValues.put("price", purchase.getPrice());
                purchaseValues.put("date", purchase.getDate().toString());
                listOfPurchasesValues.add(purchaseValues);
            }
            jsonObject.put(purchaseEntry.getKey(), listOfPurchasesValues);
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            jsonObject.writeJSONString(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
