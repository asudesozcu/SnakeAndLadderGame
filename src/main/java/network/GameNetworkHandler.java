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

public class GameNetworkHandler implements Runnable {
    private final BufferedReader in;
    private final GameMessageListener listener;

    public GameNetworkHandler(BufferedReader in, GameMessageListener listener) {
        this.in = in;
        this.listener = listener;
    }

    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                    System.out.println("[GameNetworkHandler] Received: " + line);

                if (line.startsWith("TURN")) {
                    int current = Integer.parseInt(line.split(" ")[1]);
                    listener.onTurn(current);

                } else if (line.startsWith("MOVE")) {
                    String[] parts = line.split(" ");
                    int player = Integer.parseInt(parts[1]);
                    int dice = Integer.parseInt(parts[2]);
                    int newPos = Integer.parseInt(parts[3]);
                    listener.onMove(player, dice, newPos);

                } else if (line.startsWith("PAWN_TAKEN")) {
                    int pawnId = Integer.parseInt(line.split(" ")[1]);
                    listener.onPawnTaken(pawnId);

                } else if (line.startsWith("WINNER")) {
                    
                    listener.onWin();

                } else if (line.startsWith("GAMEOVER")) {
                    listener.onGameOver();

                } else if (line.startsWith("DISCONNECTED")) {
                    listener.onDisconnected();
                }
            }
        } catch (IOException e) {
            System.err.println("[GameNetworkHandler] Connection lost: " + e.getMessage());
            listener.onDisconnected();
        }
    }
}
