/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

/**
 *
 * @author sozcu
 */
import java.io.BufferedReader;
import java.io.IOException;
import network.GameMessageListener;

/**
 * GameNetworkHandler listens to incoming messages from the server
 * through a BufferedReader and dispatches them to a GameMessageListener.
 * It runs on its own thread and continuously reads lines until disconnected.
 */
public class GameNetworkHandler implements Runnable {
    private final BufferedReader in;           // Input stream to read server messages line by line
    private final GameMessageListener listener; // Listener interface to handle different message types

    /**
     * Constructor initializes the input stream and message listener.
     *
     * @param in BufferedReader connected to the server input stream
     * @param listener GameMessageListener to handle events
     */
    public GameNetworkHandler(BufferedReader in, GameMessageListener listener) {
        this.in = in;
        this.listener = listener;
    }

    /**
     * Main loop of the handler thread.
     * Continuously reads lines from the server and parses them.
     * Calls appropriate listener methods based on message type.
     */
    public void run() {
        try {
            String line;
            // Keep reading as long as connection is open
            while ((line = in.readLine()) != null) {
                System.out.println("[GameNetworkHandler] Received: " + line);

                if (line.startsWith("TURN")) { // Turn changed message: "TURN <playerNo>"
                    int current = Integer.parseInt(line.split(" ")[1]);
                    listener.onTurn(current);

                } else if (line.startsWith("MOVE")) { // Move message: "MOVE <player> <dice> <newPos>"
                    String[] parts = line.split(" ");
                    int player = Integer.parseInt(parts[1]);
                    int dice = Integer.parseInt(parts[2]);
                    int newPos = Integer.parseInt(parts[3]);
                    listener.onMove(player, dice, newPos);

                } else if (line.startsWith("PAWN_TAKEN")) { // Pawn selection message: "PAWN_TAKEN <pawnId>"
                    int pawnId = Integer.parseInt(line.split(" ")[1]);
                    listener.onPawnTaken(pawnId);

                } else if (line.startsWith("WINNER")) { // Game won message
                    listener.onWin();

                } else if (line.startsWith("GAMEOVER")) { // Game over message
                    listener.onGameOver();

                } else if (line.startsWith("DISCONNECTED")) { // Opponent disconnected
                    listener.onDisconnected();
                }
            }
        } catch (IOException e) {
            System.err.println("[GameNetworkHandler] Connection lost: " + e.getMessage());
            // Notify listener that connection was lost
            listener.onDisconnected();
        }
    }
}