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
/**
 *
 * @author sozcu
 */
public class ClientMain {
   public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }

    public static void startConnection() {
        try {
            Socket socket = new Socket("51.20.104.21", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            WaitingFrame waiting = new WaitingFrame("Welcome! Waiting for a match...");
            SwingUtilities.invokeLater(() -> waiting.setVisible(true));

            int playerNo = -1;
            int takenPawn = -1;
            boolean startReceived = false;
            boolean pawnReceived = false;

            while (true) {
                String line = in.readLine();
                if (line == null) break;

                System.out.println("[Client] Received: " + line);

                if (line.startsWith("START")) {
                    String[] parts = line.split(" ");
                    playerNo = Integer.parseInt(parts[1]);
                    startReceived = true;

                    if (parts.length == 4 && parts[2].equals("PAWN_TAKEN")) {
                        takenPawn = Integer.parseInt(parts[3]);
                        pawnReceived = true;
                        System.out.println("[Client] Pawn received in START message: " + takenPawn);
                    }
                } else if (line.startsWith("PAWN_TAKEN")) {
                    takenPawn = Integer.parseInt(line.split(" ")[1]);
                    pawnReceived = true;
                    System.out.println("[Client] Pawn received: " + takenPawn);
                }

                if (startReceived && (playerNo == 1 || (playerNo == 2 && pawnReceived))) {
                    int finalPlayerNo = playerNo;
                    int finalTakenPawn = takenPawn;

                    SwingUtilities.invokeLater(() -> {
                        waiting.dispose();
                        new Main(2, finalPlayerNo, out, in, finalTakenPawn).setVisible(true);
                    });
                    break;
                }
            }

        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                WaitingFrame errorFrame = new WaitingFrame("Could not connect to the server.");
                errorFrame.setVisible(true);
            });
        }
    }
}