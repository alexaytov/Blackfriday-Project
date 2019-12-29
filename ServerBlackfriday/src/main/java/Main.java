import server.Server;
import store.Store;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        final String CLIENT_DATABASE_FILE_NAME = "src/Database/clientsDB.txt";
        final String STAFF_DATABASE_FILE_NAME = "src/Database/staffDB.txt";
        final String PRODUCT_DATABASE_FILE_NAME = "src/Database/productDB.txt";
        final String PURCHASES_DATABASE_FAIL_NAME = "src/Database/purchaseDB.txt";
        final int port = 4444;
        try {
            Store store = new Store(CLIENT_DATABASE_FILE_NAME, STAFF_DATABASE_FILE_NAME, PRODUCT_DATABASE_FILE_NAME, PURCHASES_DATABASE_FAIL_NAME);
            Server server = new Server(store, port);
            server.launch();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
