
package gui;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import java.util.Map;

import java.util.Map;

import java.util.HashMap;

/**
 *
 * @author sozcu
 */
/**
 * GameBoardPanel represents the Snakes & Ladders board UI.
 * It manages player pieces, positions, animations, and board background.
 */
public class GameBoardPanel extends JPanel {
    private final JLabel[] pieces;          // Array of player piece icons on the board
    private final int[] positions;          // Current tile positions of each player
    private final Image backgroundImage;    // Background image of the game board
    private final Map<Integer, Integer> snakesAndLadders; // Map of snakes/ladders start → end positions

    /**
     * Constructor initializes the board panel with given number of players.
     * Loads board background, initializes snakes & ladders positions,
     * and places player pieces at starting positions.
     * 
     * @param playerCount Number of players in the game
     */
    public GameBoardPanel(int playerCount) {
        setLayout(null);
        setBounds(50, 30, 600, 600);
        setOpaque(false);

        // Load board background image
        backgroundImage = new ImageIcon(getClass().getResource("/Image/board600.png")).getImage();

        // Initialize snake and ladder positions on the board
        snakesAndLadders = initializeSnakesAndLadders();

        // Initialize player pieces and their positions
        pieces = new JLabel[playerCount];
        positions = new int[playerCount];

        for (int i = 0; i < playerCount; i++) {
            pieces[i] = new JLabel();
            pieces[i].setSize(40, 40);
            add(pieces[i]);
            setPiecePosition(i, 0); // Place all pieces at start (position 0)
        }
    }

    /**
     * Paints the background image of the board.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    /**
     * Sets the icon image for the player piece.
     * 
     * @param index Index of the player piece
     * @param icon Icon to set (usually player's token image)
     */
    public void setPieceIcon(int index, Icon icon) {
        pieces[index].setIcon(icon);
    }

    /**
     * Updates the pixel location of the player piece on the board
     * based on the tile position.
     * 
     * Note: Does not check for snakes or ladders here.
     * 
     * @param index Player index
     * @param pos Tile position (0 = start)
     */
    public void setPiecePosition(int index, int pos) {
        positions[index] = pos;
        int[] coords = getCoordinates(pos);

        // Slight offset so pieces don't overlap exactly if multiple players on the same tile
        int xOffset = (index % 2) * 10;
        int yOffset = (index / 2) * 10;

        pieces[index].setLocation(coords[0] + xOffset, coords[1] - yOffset);
    }

    /**
     * Converts tile position (1 to 100) to pixel coordinates on the panel.
     * The board alternates direction every row.
     * 
     * @param pos Tile number (1-based), 0 for start
     * @return int array {x, y} pixel coordinates
     */
    private int[] getCoordinates(int pos) {
        if (pos <= 0) return new int[]{0, 540}; // Start position coordinates

        int row = (pos - 1) / 10;
        int col = (pos - 1) % 10;

        // Alternate direction for every odd row (right to left)
        if (row % 2 == 1) col = 9 - col;

        int tileSize = 60; // Size of each square tile
        int baseX = col * tileSize + 10;
        int baseY = 540 - row * tileSize + 10;
        return new int[]{baseX, baseY};
    }

    /**
     * Initializes the mapping of snakes and ladders on the board.
     * Key = start tile, Value = destination tile.
     * 
     * @return Map of snakes and ladders
     */
    private Map<Integer, Integer> initializeSnakesAndLadders() {
        Map<Integer, Integer> map = new HashMap<>();

        // Snakes: from higher to lower tile (slide down)
        map.put(21, 2);
        map.put(27, 15);
        map.put(47, 29);
        map.put(56, 37);
        map.put(74, 46);
        map.put(90, 52);
        map.put(99, 41);

        // Ladders: from lower to higher tile (climb up)
        map.put(23, 42);
        map.put(32, 51);
        map.put(61, 79);
        map.put(65, 84);
        map.put(75, 96);

        return map;
    }

    /**
     * Animates the movement of a player piece from start tile to end tile.
     * After reaching end tile, if there is a snake or ladder, it "jumps" to destination.
     * 
     * @param index Player index
     * @param start Starting tile
     * @param end Ending tile
     */
    public void animatePiecePosition(int index, int start, int end) {
        new Thread(() -> {
            int step = (end > start) ? 1 : -1;

            // Move step by step towards the end position
            for (int pos = start + step; pos != end + step; pos += step) {
                setPiecePosition(index, pos);
                try {
                    Thread.sleep(150); // Pause for animation effect
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Update final position
            positions[index] = end;

            // If end tile is a snake or ladder start, jump to its destination
            if (snakesAndLadders.containsKey(end)) {
                int jumpTo = snakesAndLadders.get(end);

                System.out.println("[GameBoard] Snake/Ladder triggered: " + end + " → " + jumpTo);

                try {
                    Thread.sleep(300); // Pause before jump
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Move piece instantly to jump destination on the UI thread
                int[] coords = getCoordinates(jumpTo);
                SwingUtilities.invokeLater(() -> pieces[index].setLocation(coords[0], coords[1]));

                positions[index] = jumpTo;
            }
        }).start();
    }

    /**
     * Returns the current tile position of a player piece.
     * 
     * @param index Player index
     * @return Tile position (0 if not moved)
     */
    public int getCurrentPosition(int index) {
        return positions[index];
    }
}