/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

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
        try {
            Socket socket = new Socket("localhost", 5000); // Change to actual IP in deployment
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            WaitingFrame waitingFrame = new WaitingFrame();
            waitingFrame.setVisible(true);

            String msg;
            int playerNo = -1;
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith("WAITING")) {
                    playerNo = Integer.parseInt(msg.split(" ")[1]);
                    // Remain on waiting screen until START received
                } else if (msg.startsWith("START")) {
                    playerNo = Integer.parseInt(msg.split(" ")[1]);
                    break;
                }
            }

            int finalPlayerNo = playerNo;
            waitingFrame.dispose();
            SwingUtilities.invokeLater(() -> {
                Main game = new Main(2, finalPlayerNo, out, in);
                game.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}