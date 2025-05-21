package Server;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerMain {
   private static final int PORT = 5000;
    private static final Queue<Socket> waitingQueue = new LinkedList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server] Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[Server] New player connected: " + socket.getInetAddress() + ":" + socket.getPort());

                synchronized (waitingQueue) {
                    waitingQueue.offer(socket);
                    System.out.println("[Server] Waiting queue size: " + waitingQueue.size());

                    if (waitingQueue.size() >= 2) {
                        Socket player1 = waitingQueue.poll();
                        Socket player2 = waitingQueue.poll();

                        System.out.println("[Server] Starting new game session...");
                        System.out.println("  Player 1: " + player1.getInetAddress() + ":" + player1.getPort());
                        System.out.println("  Player 2: " + player2.getInetAddress() + ":" + player2.getPort());

                        new Thread(new GameHandler(player1, player2)).start();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Server] Exception in main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}