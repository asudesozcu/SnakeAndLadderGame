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
import gui.WaitingFrame;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
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

            while (true) {
                String line = in.readLine();
                if (line == null) break;
                if (line.startsWith("START")) {
                    int playerNo = Integer.parseInt(line.split(" ")[1]);
                    waiting.dispose();
                    SwingUtilities.invokeLater(() -> new Main(2, playerNo, out, in).setVisible(true));
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
