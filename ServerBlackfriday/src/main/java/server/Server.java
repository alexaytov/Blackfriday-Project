package server;

import store.Store;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final Store STORE;
    private final int PORT;

    public Server(Store store, int PORT) {
        this.STORE = store;
        this.PORT = PORT;
    }

    /**
     * Starts the server
     */
    public void launch() {
        int numberOfThreads = 0;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket;
                socket = serverSocket.accept();

                ClientThread clientThread = new ClientThread(socket, STORE);
                numberOfThreads++;
                Thread thread = new Thread(clientThread);
                thread.start();
                System.out.println("Thread with name " + (numberOfThreads - 1) + " started!!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
