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

public class Main extends JFrame {
   private JLabel lblTurn;
    private Timer diceAnimationTimer;
    private int[] diceFrames = {1, 2, 3, 4, 5, 6};
    private int diceAnimIndex = 0;
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
    private boolean animating = false;
    private boolean gameEnded = false;
    private int disabledPawn = -1;
    private int selectedPawn = -1;
    private boolean pawnSelectionOpened = false;
private boolean pawnSelected = false;

    public Main(int playerCount, int playerNo, PrintWriter out, BufferedReader in, int disabledPawn) {
        this.playerCount = playerCount;
        this.playerNo = playerNo;
        this.out = out;
        this.in = in;
        this.disabledPawn = disabledPawn;

        setTitle("Multiplayer Snakes & Ladders");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        lblBoard = new JLabel(new ImageIcon(getClass().getResource("/Image/board600.png")));
        lblBoard.setBounds(50, 30, 600, 600);
        add(lblBoard);
        lblBoard.setLayout(null);

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

        playerPieces = new JLabel[playerCount];
        positions = new int[playerCount];
        for (int i = 0; i < playerCount; i++) {
            playerPieces[i] = new JLabel();
            playerPieces[i].setSize(40, 40);
            lblBoard.add(playerPieces[i]);
            positions[i] = 0;
            int[] coords = getCoordinates(0);
            playerPieces[i].setLocation(coords[0] + (i * 10), coords[1] - (i * 10)); //üst üste binmesin
        }

        // Pawn seçimi
       if (playerNo == 1) {
    SwingUtilities.invokeLater(this::showPawnSelection);
} else if (playerNo == 2) {
    if (disabledPawn != -1) {
      SwingUtilities.invokeLater(() -> {
            // Rakibin taşını göster
            int opponentIndex = 0;
            ImageIcon opponentIcon = new ImageIcon(getClass().getResource("/Image/player " + disabledPawn + ".png"));
            playerPieces[opponentIndex].setIcon(opponentIcon);

            // 🔴 Problemli satır:
Timer oneShotTimer = new Timer(100, ev -> showPawnSelection());
oneShotTimer.setRepeats(false); // ✅ sadece 1 kez çalışsın
oneShotTimer.start();        });
    } else {
        SwingUtilities.invokeLater(this::showPawnSelection);
    }

}

        // Server mesajlarını dinle
        Thread listener = new Thread(() -> {
            try {
                String line;
              while ((line = in.readLine()) != null) {
    if (line.startsWith("TURN")) {
        int turn = Integer.parseInt(line.split(" ")[1]);
        myTurn = (turn == playerNo);
        SwingUtilities.invokeLater(() -> {
            btnRoll.setEnabled(myTurn);
            updateTurnLabel(myTurn);
            System.out.println("[Main] Received TURN " + turn + ", myPlayerNo: " + playerNo);
        });
    }
 else if (line.startsWith("MOVE")) {
                        String[] parts = line.split(" ");
                        int mover = Integer.parseInt(parts[1]);
                        int dice = Integer.parseInt(parts[2]);
                        int newPos = Integer.parseInt(parts[3]);
                        positions[mover - 1] = newPos;

                        SwingUtilities.invokeLater(() -> startDiceAnimation(dice, () -> movePlayer(mover - 1)));
                    } else if (line.startsWith("WINNER")) {
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
                    } else if (line.startsWith("GAMEOVER")) {
                        int choice = JOptionPane.showConfirmDialog(this,
                                "Game Over\nDo you want to play again?",
                                "Game Over", JOptionPane.YES_NO_OPTION);
                        dispose();
                        if (choice == JOptionPane.YES_OPTION) {
                            new Thread(() -> Client.ClientMain.main(new String[0])).start();
                        } else {
                            System.exit(0);
                        }
                    } else if (line.startsWith("DISCONNECTED")) {
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
                            break;
                        }
                    } else if (line.startsWith("PAWN_TAKEN")) {
                        int takenPawn = Integer.parseInt(line.split(" ")[1]);
                        int opponentIndex = (playerNo == 1) ? 1 : 0;

                        SwingUtilities.invokeLater(() -> {
                            ImageIcon icon = new ImageIcon(getClass().getResource("/Image/player " + takenPawn + ".png"));
                            playerPieces[opponentIndex].setIcon(icon);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        listener.start();
    }

    private void movePlayer(int playerIdx) {
        int[] coords = getCoordinates(positions[playerIdx]);
        int xOffset = (playerIdx % 2) * 10;
        int yOffset = (playerIdx / 2) * 10;
        playerPieces[playerIdx].setLocation(coords[0] + xOffset, coords[1] - yOffset);
    }

    private int[] getCoordinates(int pos) {
        if (pos <= 0) return new int[]{0, 540};
        int row = (pos - 1) / 10;
        int col = (pos - 1) % 10;
        if (row % 2 == 1) col = 9 - col;
        int tileSize = 60;
        int baseX = col * tileSize + 10;
        int baseY = 540 - row * tileSize + 10;
        return new int[]{baseX, baseY};
    }

    private void startDiceAnimation(int finalValue, Runnable onComplete) {
        animating = true;
        diceAnimIndex = 0;

        if (diceAnimationTimer != null && diceAnimationTimer.isRunning()) {
            diceAnimationTimer.stop();
        }

        diceAnimationTimer = new Timer(50, e -> {
            int frame = diceFrames[diceAnimIndex % diceFrames.length];
            lblDice.setIcon(new ImageIcon(getClass().getResource("/Image/dice " + frame + ".jpg")));
            diceAnimIndex++;

            if (diceAnimIndex >= 6) {
                diceAnimationTimer.stop();
                lblDice.setIcon(new ImageIcon(getClass().getResource("/Image/dice " + finalValue + ".jpg")));
                animating = false;
                if (onComplete != null) onComplete.run();
            }
        });

        diceAnimationTimer.start();
    }

    private void updateTurnLabel(boolean yourTurn) {
        if (yourTurn) {
            lblTurn.setText("Your Turn!");
            lblTurn.setForeground(new Color(0, 128, 0));
        } else {
            lblTurn.setText("Opponent’s Turn...");
            lblTurn.setForeground(Color.RED);
        }
    }

   public void showPawnSelection() {
    if (pawnSelected) {
        System.out.println("[Main] Pawn already selected — not opening selection screen again.");
        return;
    }

    if (pawnSelectionOpened) {
        System.out.println("[Main] PawnSelection already open — skipping");
        return;
    }

    pawnSelectionOpened = true;
    System.out.println("[Main] Opening PawnSelection");

    new PawnSelection(this, disabledPawn, e -> {
        selectedPawn = ((PawnSelection) e.getSource()).getSelectedPawn();
        int ownIndex = (playerNo == 1) ? 0 : 1;
        ImageIcon icon = new ImageIcon(getClass().getResource("/Image/player " + selectedPawn + ".png"));
        playerPieces[ownIndex].setIcon(icon);
        out.println("PAWN " + selectedPawn);

        pawnSelectionOpened = false;
        pawnSelected = true; // 🔒 Artık seçim tamamlandı
    });
}


}