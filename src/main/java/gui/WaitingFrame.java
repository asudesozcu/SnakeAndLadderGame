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

/**
 * WaitingFrame is a simple JFrame that shows a waiting message
 * along with a loading GIF animation while the client waits for an opponent.
 */
public class WaitingFrame extends JFrame {
    private JLabel loadingLabel;   // Label to display the loading GIF
    private JLabel nameLabel;      // Label to display the waiting message
    private ImageIcon rawIcon;     // The loading GIF icon

    /**
     * Constructor initializes the waiting frame with a message.
     *
     * @param message The text message to display on top of the frame
     */
    public WaitingFrame(String message) {
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        setTitle("Waiting for Opponent");
        setSize(600, 600);
        setLocationRelativeTo(null);  // Center the window on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Label at the top displaying the message
        nameLabel = new JLabel(message, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setBounds(0, 0, getWidth(), 40);
        nameLabel.setBackground(Color.WHITE);
        add(nameLabel);

        // Loading GIF displayed below the message
        rawIcon = new ImageIcon(getClass().getResource("/Image/loading.gif"));
        loadingLabel = new JLabel(rawIcon);
        loadingLabel.setBounds(0, 40, getWidth(), getHeight() - 40);
        loadingLabel.setOpaque(true);
        loadingLabel.setBackground(Color.WHITE);
        add(loadingLabel);

        // Adjust the label bounds when the window is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                nameLabel.setBounds(0, 0, getWidth(), 40);
                loadingLabel.setBounds(0, 40, getWidth(), getHeight() - 40);
            }
        });
    }
}
