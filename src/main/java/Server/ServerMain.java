package Server;

import java.net.*;
import java.io.*;
import java.util.*;
import logic.GameLogic;

public class ServerMain {
    private static final int PORT = 5000;
    private static final int TOTAL_PLAYERS = 2;

    private static ArrayList<Socket> clients = new ArrayList<>();
    private static ArrayList<PrintWriter> writers = new ArrayList<>();
    private static GameLogic gameLogic = new GameLogic(TOTAL_PLAYERS);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            // Accept players
            while (clients.size() < TOTAL_PLAYERS) {
                Socket socket = serverSocket.accept();
                clients.add(socket);
                writers.add(new PrintWriter(socket.getOutputStream(), true));
                System.out.println("Player connected: " + socket);
                int playerNumber = clients.size();
                writers.get(playerNumber - 1).println("WAITING " + playerNumber);
            }

            // Notify game start
            for (int i = 0; i < TOTAL_PLAYERS; i++) {
                writers.get(i).println("START " + (i + 1));
            }

            // Start handling clients
            for (int i = 0; i < TOTAL_PLAYERS; i++) {
                final int playerNo = i + 1;
                Socket socket = clients.get(i);
                new Thread(() -> handleClient(socket, playerNo)).start();
            }

            // Send initial turn info
            broadcast("TURN 1");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   private static void handleClient(Socket socket, int playerNo) {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Received from Player " + playerNo + ": " + line);

            if (line.equals("ROLL") && gameLogic.getCurrentPlayer() == playerNo - 1) {
                int dice = gameLogic.rollDice();
                int newPosition = gameLogic.movePlayer(dice);

                // ✔️ DÜZENLENEN KISIM
                broadcast("MOVE " + playerNo + " " + dice + " " + newPosition);

                if (gameLogic.isWinner(playerNo - 1)) {
                    broadcast("WINNER " + playerNo);
                }

                broadcast("TURN " + (gameLogic.getCurrentPlayer() + 1));
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