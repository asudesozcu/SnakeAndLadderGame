package gui;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sozcu
 */
import javax.swing.*;
import java.awt.*;


import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;



public class WaitingFrame extends JFrame {
   private JLabel loadingLabel;
    private JLabel nameLabel;
    private ImageIcon rawIcon;

    public WaitingFrame(String message) {
setLayout(null);
getContentPane().setBackground(Color.WHITE);
        setTitle("Waiting for Opponent");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Üstteki yazı
        nameLabel = new JLabel( message , SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setBounds(0, 0, getWidth(), 40);
      nameLabel.setBackground(Color.WHITE);

        add(nameLabel);

        // GIF label
        rawIcon = new ImageIcon(getClass().getResource("/Image/loading.gif")); 
        loadingLabel = new JLabel(rawIcon);
        loadingLabel.setBounds(0, 40, getWidth(), getHeight() - 40);
       loadingLabel.setOpaque(true);
loadingLabel.setBackground(Color.WHITE);
        add(loadingLabel);

        // Yeniden boyutlanınca GIF ayarla
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                nameLabel.setBounds(0, 0, getWidth(), 40);
                loadingLabel.setBounds(0, 40, getWidth(), getHeight() - 40);
            }
        });
    }
}
