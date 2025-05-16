 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

import gui.Login;
import gui.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;
import gui.Main;
import gui.PawnSelection;
import gui.WaitingFrame;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            Socket socket = new Socket("localhost", 5000);
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

                if (line.startsWith("START")) {
                    String[] parts = line.split(" ");
                    playerNo = Integer.parseInt(parts[1]);
                    startReceived = true;

                    if (parts.length == 4 && parts[2].equals("PAWN_TAKEN")) {
                        takenPawn = Integer.parseInt(parts[3]);
                        pawnReceived = true;
                    }
                } else if (line.startsWith("PAWN_TAKEN")) {
                    takenPawn = Integer.parseInt(line.split(" ")[1]);
                    pawnReceived = true;
                }

                if ((playerNo == 1 && startReceived) || (playerNo == 2 && startReceived && pawnReceived)) {
                    int finalPlayerNo = playerNo;
                    int finalTakenPawn = takenPawn;
                    SwingUtilities.invokeLater(() -> {
                        waiting.dispose();
                        new Main(2, finalPlayerNo, out, in, finalTakenPawn).setVisible(true);
                    });
                    break;
                }
            }

        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                WaitingFrame errorFrame = new WaitingFrame("Could not connect to the server.");
                errorFrame.setVisible(true);
            });
        }
    }
}