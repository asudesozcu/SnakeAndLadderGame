package Server;

import java.net.*;
import java.io.*;
import java.util.*;


/**
 * ServerMain class accepts incoming client connections and manages matchmaking.
 * It pairs clients from the waiting queue to start new game sessions.
 */
public class ServerMain {
   private static final int PORT = 5000;
       // queue to hold waiting players until paired for a game

    private static final Queue<Socket> waitingQueue = new LinkedList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server] Server started on port " + PORT);

            while (true) {//Continuously accept new player connections

                Socket socket = serverSocket.accept();
                System.out.println("[Server] New player connected: " + socket.getInetAddress() + ":" + socket.getPort());

                // Synchronize access to the waiting queue to handle concurrent connections safely

              synchronized (waitingQueue) {
                    waitingQueue.offer(socket); // new player added to end of queue

                    if (waitingQueue.size() >= 2) {//if we have enough waiting player then start a new game
                        Socket player1 = waitingQueue.poll();                        // Remove two players from the queue to pair them for a game

                        Socket player2 = waitingQueue.poll();
                        new Thread(new GameHandler(player1, player2)).start(); // to manage new game : a new thread 
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Server] Exception in main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}