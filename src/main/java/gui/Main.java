package gui;


import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import javax.swing.Timer;
import network.GameMessageListener;
import network.GameNetworkHandler;


/**
 * Main class represents the main game window for multiplayer Snakes & Ladders.
 * It implements GameMessageListener to handle network game events.
 */

public class Main extends JFrame implements GameMessageListener {
 private JLabel lblTurn;            // Label displaying current turn info
    private JButton btnRoll;           // Button to roll the dice
    private JLabel lblDice;            // Label showing dice image
    private GameBoardPanel gameBoard;  // Custom panel for game board and pieces
    private GameNetworkHandler networkHandler; // Network message handler thread
    private boolean gameEnded = false; // Flag to indicate if the game has ended

    private final int playerNo;        // Player number (1 or 2)
    private final PrintWriter out;     // Output stream to server
    private final BufferedReader in;   // Input stream from server

    /**
     * Constructor initializes the main game window and UI components.
     * Starts the network handler thread to listen for server messages.
     * Displays pawn selection dialog based on player number.
     * 
     * @param playerCount Number of players
     * @param playerNo Player number (1 or 2)
     * @param out PrintWriter for sending messages to server
     * @param in BufferedReader for receiving messages from server
     * @param disabledPawn Pawn id that the opponent has already taken (for player 2)
     */
    public Main(int playerCount, int playerNo, PrintWriter out, BufferedReader in, int disabledPawn) {
        this.playerNo = playerNo;
        this.out = out;
        this.in = in;

        setTitle("Multiplayer Snakes & Ladders");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Load and scale the background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/Image/background.png"));
        Image bgImage = bgIcon.getImage().getScaledInstance(800, 700, Image.SCALE_SMOOTH);

        // Create a JPanel with the background image painted on it
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        // Initialize game board panel
        gameBoard = new GameBoardPanel(playerCount);
        add(gameBoard);

        // Dice label initialized with dice face 1 image
        lblDice = new JLabel(new ImageIcon(getClass().getResource("/Image/dice 1.jpg")));
        lblDice.setBounds(680, 100, 100, 100);
        add(lblDice);

        // Label to display whose turn it is
        lblTurn = new JLabel("Waiting for your turn...", SwingConstants.CENTER);
        lblTurn.setBounds(650, 20, 130, 30);
        lblTurn.setFont(new Font("Arial", Font.BOLD, 12));
        lblTurn.setForeground(Color.DARK_GRAY);
        add(lblTurn);

        // Button to roll the dice, initially disabled
        btnRoll = new JButton("Roll Dice");
        btnRoll.setBounds(670, 220, 120, 40);
        btnRoll.setEnabled(false);
        add(btnRoll);

        // ActionListener for roll button: sends "ROLL" command to server and disables button
        btnRoll.addActionListener(e -> {
            out.println("ROLL");
            btnRoll.setEnabled(false);
        });

        // Show pawn selection dialog for player 1 immediately
        if (playerNo == 1) {
            SwingUtilities.invokeLater(() -> new PawnSelection(this, -1, e -> handlePawnSelection(e)));
        } else {
            // For player 2, set opponent's pawn icon and show selection after a short delay
            gameBoard.setPieceIcon(0, new ImageIcon(getClass().getResource("/Image/player " + disabledPawn + ".png")));
            Timer timer = new Timer(100, ev -> new PawnSelection(this, disabledPawn, e -> handlePawnSelection(e)));
            timer.setRepeats(false);
            timer.start();
        }

        // Initialize and start network message handler thread
        networkHandler = new GameNetworkHandler(in, this);
        new Thread(networkHandler).start();
    }

    /**
     * Handles the event when the player selects a pawn.
     * Updates the board and sends the selection to the server.
     */
    private void handlePawnSelection(java.awt.event.ActionEvent e) {
        int selected = ((PawnSelection) ((JButton) e.getSource()).getTopLevelAncestor()).getSelectedPawn();
        int index = (playerNo == 1) ? 0 : 1;
        gameBoard.setPieceIcon(index, new ImageIcon(getClass().getResource("/Image/player " + selected + ".png")));
        out.println("PAWN " + selected);
    }

    /**
     * Called when the turn changes.
     * Updates the UI to show whether it is this player's turn.
     */
    public void onTurn(int currentPlayer) {
        boolean myTurn = (currentPlayer == playerNo);
        lblTurn.setText(myTurn ? "Your Turn!" : "Opponent's Turn...");
        lblTurn.setForeground(myTurn ? Color.GREEN.darker() : Color.RED);
        btnRoll.setEnabled(myTurn);
    }

    /**
     * Called when a player moves.
     * Animates the dice roll and moves the player's piece on the board.
     */
    @Override
    public void onMove(int player, int dice, int position) {
        int playerIndex = player - 1;
        int current = gameBoard.getCurrentPosition(playerIndex);
        DiceAnimator.animate(lblDice, dice, () ->
                gameBoard.animatePiecePosition(playerIndex, current, position)
        );
    }

    /**
     * Called when the opponent selects their pawn.
     * Updates the opponent's pawn icon on the board.
     */
    public void onPawnTaken(int pawnId) {
        int opponent = (playerNo == 1) ? 1 : 0;
        gameBoard.setPieceIcon(opponent, new ImageIcon(getClass().getResource("/Image/player " + pawnId + ".png")));
    }

    /**
     * Called when this player wins the game.
     * Displays a dialog and offers to restart or exit.
     */
    public void onWin() {
        gameEnded = true;
        int choice = JOptionPane.showConfirmDialog(this,
                "🎉 You win 🎉 !\nDo you want to play again?",
                "Game Over", JOptionPane.YES_NO_OPTION);
        dispose();
        if (choice == JOptionPane.YES_OPTION) {
            new Thread(() -> Client.ClientMain.main(new String[0])).start();
        } else {
            System.exit(0);
        }
    }

    /**
     * Called when the opponent wins or the game ends.
     * Displays a dialog and offers to restart or exit.
     */
    public void onGameOver() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Game Over\nDo you want to play again?",
                "Game Over", JOptionPane.YES_NO_OPTION);
        dispose();
        if (choice == JOptionPane.YES_OPTION) {
            new Thread(() -> Client.ClientMain.main(new String[0])).start();
        } else {
            System.exit(0);
        }
    }

    /**
     * Called when the opponent disconnects unexpectedly.
     * Prompts the user to find a new match or exit.
     */
    public void onDisconnected() {
        if (!gameEnded) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Your opponent has disconnected.\nDo you want to search for a new match?",
                        "Disconnected",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    new Thread(() -> Client.ClientMain.main(new String[0])).start();
                } else {
                    JOptionPane.showMessageDialog(null, "You have left the game.");
                    System.exit(0);
                }
            });
        }
    }
}