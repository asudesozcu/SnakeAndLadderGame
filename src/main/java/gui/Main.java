package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.SwingUtilities;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import logic.GameLogic;

public class Main extends JFrame {
     private JLabel lblBoard;
    private JLabel lblDice;
    private JButton btnRoll;
    private JLabel[] playerPieces;
    private int[] positions;
    private int playerCount;
    private int playerNo;
    private PrintWriter out;
    private BufferedReader in;
    private boolean myTurn = false;

    public Main(int playerCount, int playerNo, PrintWriter out, BufferedReader in) {
        this.playerCount = playerCount;
        this.playerNo = playerNo;
        this.out = out;
        this.in = in;

      setTitle("Multiplayer Snakes & Ladders");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        java.net.URL boardUrl = getClass().getResource("/Image/board600.png");
        if (boardUrl == null) {
            JOptionPane.showMessageDialog(this, "Board image not found.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        lblBoard = new JLabel(new ImageIcon(boardUrl));
        lblBoard.setBounds(50, 30, 600, 600);
        add(lblBoard);
        lblBoard.setLayout(null);

        lblDice = new JLabel();
        lblDice.setBounds(680, 100, 100, 100);
        add(lblDice);

        btnRoll = new JButton("Roll Dice");
        btnRoll.setBounds(670, 220, 120, 40);
        btnRoll.setEnabled(false);
        add(btnRoll);

        btnRoll.addActionListener(e -> {
            out.println("ROLL");
            btnRoll.setEnabled(false);
        });

        // Create player pieces
        playerPieces = new JLabel[playerCount];
        positions = new int[playerCount];
        for (int i = 0; i < playerCount; i++) {
            java.net.URL pieceUrl = getClass().getResource("/Image/player " + (i + 1) + ".png");
            if (pieceUrl == null) {
                JOptionPane.showMessageDialog(this, "Player image not found: player " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            playerPieces[i] = new JLabel(new ImageIcon(pieceUrl));
            playerPieces[i].setSize(40, 40);
            lblBoard.add(playerPieces[i]);
            positions[i] = 0;
            int[] coords = getCoordinates(0);
            playerPieces[i].setLocation(coords[0] + (i * 10), coords[1] - (i * 10)); // offset for overlapping
        }

        // Listener thread
        Thread listener = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("TURN")) {
                        int turn = Integer.parseInt(line.split(" ")[1]);
                        myTurn = (turn == playerNo);
                        SwingUtilities.invokeLater(() -> btnRoll.setEnabled(myTurn));
                    } else if (line.startsWith("MOVE")) {
    String[] parts = line.split(" ");
    if (parts.length >= 4) {
        int mover = Integer.parseInt(parts[1]);
        int dice = Integer.parseInt(parts[2]);
        int newPos = Integer.parseInt(parts[3]);
        positions[mover - 1] = newPos;

        SwingUtilities.invokeLater(() -> {
            movePlayer(mover - 1, dice);
            java.net.URL diceUrl = getClass().getResource("/Image/dice " + dice + ".jpg");
            if (diceUrl != null) {
                lblDice.setIcon(new ImageIcon(diceUrl));
            }
        });
    }
}
 else if (line.startsWith("WINNER")) {
                        int winner = Integer.parseInt(line.split(" ")[1]);
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "🎉 Player " + winner + " wins the game!");
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listener.start();
    }

    private void movePlayer(int playerIdx, int dice) {
    int[] coords = getCoordinates(positions[playerIdx]);
    int xOffset = (playerIdx % 2) * 10;
    int yOffset = (playerIdx / 2) * 10;
    playerPieces[playerIdx].setLocation(coords[0] + xOffset, coords[1] - yOffset);
}


    private int[] getCoordinates(int pos) {
        if (pos <= 0) return new int[]{0, 540}; // start of board
        int row = (pos - 1) / 10;
        int col = (pos - 1) % 10;
        if (row % 2 == 1) {
            col = 9 - col; // reverse direction on odd rows
        }
        int tileSize = 60;
        int baseX = col * tileSize + 10;
        int baseY = 540 - row * tileSize + 10;
        return new int[]{baseX, baseY};
    }
}