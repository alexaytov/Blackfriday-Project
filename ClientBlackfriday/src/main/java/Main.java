import store.Store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            int port = 4444;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            Store store = new Store(ip, port, reader);
            store.open();

        } catch (IOException e) {
            System.out.println("Sorry there was a problem with connecting to the server. Please try again later.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
