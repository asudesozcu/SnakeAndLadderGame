/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

import gui.Login;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.swing.SwingUtilities;
import gui.Main;
import gui.WaitingFrame;
import java.net.Socket;
import network.GameNetworkHandler;

/**
 *
 * @author sozcu
 */
/**
 * ClientMain class handles the client-side connection and initialization for
 * the multiplayer game.
 */

public class ClientMain {
    private static int playerNo = -1;

    // Pawn already taken by the opponent to prevent selecting the same pawn
    private static int takenPawn = -1;

    /**
     * The main entry point of the client application.
     * Shows the login window safely on the Event Dispatch Thread (EDT).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true)); // Display login UI on EDT
    }

    /**
     * Establishes the connection to the game server and manages initial handshake.
     * Listens for "START" and "PAWN_TAKEN" messages to initialize the game UI.
     */
    public static void startConnection() {
        try {
            // Connect to the game server socket at specified IP and port
            Socket socket = new Socket("51.20.104.21", 5000);

            // Initialize input and output streams for communication with the server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Show a waiting frame with a message while searching for an opponent
            WaitingFrame waiting = new WaitingFrame("Welcome! Waiting for a match...");
            SwingUtilities.invokeLater(() -> waiting.setVisible(true));

            // Flags to track receipt of required messages from server before game start
            boolean startReceived = false;
            boolean pawnReceived = false;

            // Loop to listen for server messages during handshake phase
            while (true) {
                String line = in.readLine();
                if (line == null) break;  // Connection closed

                System.out.println("[Client] Received: " + line);

                if (line.startsWith("START")) {
                    // Parse "START" message, expected format: "START <playerNo> [PAWN_TAKEN <pawnId>]"
                    String[] parts = line.split(" ");
                    playerNo = Integer.parseInt(parts[1]);
                    startReceived = true;

                    // For player 2, pawn info might be included in START message to disable selection of the taken pawn
                    if (parts.length == 4 && parts[2].equals("PAWN_TAKEN")) {
                        takenPawn = Integer.parseInt(parts[3]);
                        pawnReceived = true;
                        System.out.println("[Client] Pawn received in START message: " + takenPawn);
                    }
                } else if (line.startsWith("PAWN_TAKEN")) {
                    // For player 1, pawn info may come separately after START message
                    takenPawn = Integer.parseInt(line.split(" ")[1]);
                    pawnReceived = true;
                    System.out.println("[Client] Pawn received: " + takenPawn);
                }

                // Once all necessary data is received, initialize the main game UI
                if (startReceived && (playerNo == 1 || (playerNo == 2 && pawnReceived))) {
                    SwingUtilities.invokeLater(() -> {
                        waiting.dispose();

                        // Create and show the main game window, passing connection info and taken pawn
                        new Main(2, playerNo, out, in, takenPawn).setVisible(true);
                    });
                    break; // Exit the listening loop to proceed with the game
                }
            }

        } catch (Exception e) {
            // If connection fails or an error occurs, display an error message to the user
            SwingUtilities.invokeLater(() -> {
                WaitingFrame errorFrame = new WaitingFrame("Could not connect to the server.");
                errorFrame.setVisible(true);
            });
            e.printStackTrace();
        }
    }
}