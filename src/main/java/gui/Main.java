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
import java.io.BufferedReader;
import java.io.PrintWriter;
import javax.swing.Timer;
import network.GameMessageListener;
import network.GameNetworkHandler;

public class Main extends JFrame implements GameMessageListener {
  private JLabel lblTurn;
    private JButton btnRoll;
    private JLabel lblDice;
    private GameBoardPanel gameBoard;
    private GameNetworkHandler networkHandler;
     private boolean gameEnded = false;

    private final int playerNo;
    private final PrintWriter out;
    private final BufferedReader in;

    public Main(int playerCount, int playerNo, PrintWriter out, BufferedReader in, int disabledPawn) {
        this.playerNo = playerNo;
        this.out = out;
        this.in = in;

        setTitle("Multiplayer Snakes & Ladders");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        gameBoard = new GameBoardPanel(playerCount);
        add(gameBoard);

        lblDice = new JLabel(new ImageIcon(getClass().getResource("/Image/dice 1.jpg")));
        lblDice.setBounds(680, 100, 100, 100);
        add(lblDice);

        lblTurn = new JLabel("Waiting for your turn...", SwingConstants.CENTER);
        lblTurn.setBounds(650, 20, 130, 30);
        lblTurn.setFont(new Font("Arial", Font.BOLD, 12));
        lblTurn.setForeground(Color.DARK_GRAY);
        add(lblTurn);

        btnRoll = new JButton("Roll Dice");
        btnRoll.setBounds(670, 220, 120, 40);
        btnRoll.setEnabled(false);
        add(btnRoll);

        btnRoll.addActionListener(e -> {
            out.println("ROLL");
            btnRoll.setEnabled(false);
        });

        if (playerNo == 1) {
            SwingUtilities.invokeLater(() -> new PawnSelection(this, -1, e -> handlePawnSelection(e)));
        } else {
            gameBoard.setPieceIcon(0, new ImageIcon(getClass().getResource("/Image/player " + disabledPawn + ".png")));
            Timer timer = new Timer(100, ev -> new PawnSelection(this, disabledPawn, e -> handlePawnSelection(e)));
            timer.setRepeats(false);
            timer.start();
        }

        networkHandler = new GameNetworkHandler(in, this);
        new Thread(networkHandler).start();
    }

    private void handlePawnSelection(java.awt.event.ActionEvent e) {
        int selected = ((PawnSelection) ((JButton) e.getSource()).getTopLevelAncestor()).getSelectedPawn();
        int index = (playerNo == 1) ? 0 : 1;
        gameBoard.setPieceIcon(index, new ImageIcon(getClass().getResource("/Image/player " + selected + ".png")));
        out.println("PAWN " + selected);
    }

    public void onTurn(int currentPlayer) {
        boolean myTurn = (currentPlayer == playerNo);
        lblTurn.setText(myTurn ? "Your Turn!" : "Opponent's Turn...");
        lblTurn.setForeground(myTurn ? Color.GREEN.darker() : Color.RED);
        btnRoll.setEnabled(myTurn);
    }

    public void onMove(int player, int dice, int position) {
        DiceAnimator.animate(lblDice, dice, () -> gameBoard.setPiecePosition(player - 1, position));
    }

    public void onPawnTaken(int pawnId) {
        int opponent = (playerNo == 1) ? 1 : 0;
        gameBoard.setPieceIcon(opponent, new ImageIcon(getClass().getResource("/Image/player " + pawnId + ".png")));
    }

    public void onWin() {
        gameEnded=true;
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

    public void onDisconnected() {
         if (!gameEnded) { dispose();
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
        });}
       
    }
}
