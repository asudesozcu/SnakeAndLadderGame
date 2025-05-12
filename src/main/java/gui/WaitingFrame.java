package gui;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sozcu
 */
import Client.ClientMain;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class WaitingFrame extends JFrame {
   public WaitingFrame() {
        setTitle("Waiting for Opponent");
        setSize(400, 150);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Waiting for another player...", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.CENTER);

        JLabel sub = new JLabel("Please wait...", SwingConstants.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        add(sub, BorderLayout.SOUTH);
    }
}