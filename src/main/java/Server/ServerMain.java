package Server;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerMain {
     private static final int PORT = 5000;
    private static final Queue<Socket> waitingQueue = new LinkedList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New player connected: " + socket);
                synchronized (waitingQueue) {
                    waitingQueue.offer(socket);
                    if (waitingQueue.size() >= 2) {
                        Socket player1 = waitingQueue.poll();
                        Socket player2 = waitingQueue.poll();
                        new Thread(new GameHandler(player1, player2)).start();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
