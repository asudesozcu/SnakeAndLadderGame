package Server;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerMain {
   private static final int PORT = 5000;
    private static ArrayList<Socket> clients = new ArrayList<>();
    private static ArrayList<PrintWriter> writers = new ArrayList<>();
    private static int currentTurn = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            // Accept 2 players
            while (clients.size() < 2) {
                Socket socket = serverSocket.accept();
                clients.add(socket);
                writers.add(new PrintWriter(socket.getOutputStream(), true));
                System.out.println("Player connected: " + socket);
                int playerNumber = clients.size();
                writers.get(playerNumber - 1).println("WAITING " + playerNumber);
            }

            // Notify both players game is starting
            for (int i = 0; i < clients.size(); i++) {
                writers.get(i).println("START " + (i + 1));
            }

            // Start listening to moves from clients
            for (int i = 0; i < clients.size(); i++) {
                final int playerNo = i + 1;
                Socket socket = clients.get(i);
                new Thread(() -> handleClient(socket, playerNo)).start();
            }

            // Notify turn
            broadcast("TURN 1");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket, int playerNo) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("ROLL") && playerNo == currentTurn) {
                    broadcast("MOVE " + playerNo);
                    currentTurn = (currentTurn % 2) + 1;
                    broadcast("TURN " + currentTurn);
                }
            }
        } catch (Exception e) {
            System.out.println("Player " + playerNo + " disconnected.");
        }
    }

    private static void broadcast(String msg) {
        for (PrintWriter out : writers) {
            out.println(msg);
        }
    }
}