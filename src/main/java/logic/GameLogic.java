package logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sozcu
 */

//zar fotosu değişsin

/**
 * GameLogic handles the core rules and state of the Snakes & Ladders game.
 * It tracks player positions, current turn, dice rolls, and snakes/ladders transitions.
 */
public class GameLogic {
    private final int[] positions;                 // Positions of each player on the board
    private int currentPlayer;                      // Index of current player (0-based)
    private final Random rand;                      // Random number generator for dice rolls
    private final Map<Integer, Integer> snakesAndLadders = initializeSnakesAndLadders(); // Map of snakes and ladders

    /**
     * Constructor initializes player positions and sets the first player.
     * 
     * @param playerCount Number of players in the game
     */
    public GameLogic(int playerCount) {
        this.positions = new int[playerCount];
        this.currentPlayer = 0;   // Start with player 0
        this.rand = new Random();
    }

    /**
     * Returns the current player's index (0-based).
     * 
     * @return current player index
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Rolls a six-sided dice and returns the result (1-6).
     * 
     * @return dice roll result
     */
    public int rollDice() {
        return rand.nextInt(6) + 1;
    }

    /**
     * Checks if the specified player has won by reaching or passing tile 100.
     * 
     * @param playerIndex Index of the player
     * @return true if player has won, false otherwise
     */
    public boolean isWinner(int playerIndex) {
        return positions[playerIndex] >= 100;
    }

    /**
     * Returns the current position of the specified player.
     * 
     * @param index Player index
     * @return Player's tile position
     */
    public int getPlayerPosition(int index) {
        return positions[index];
    }

    /**
     * Moves the current player forward by the dice roll amount.
     * Checks for snakes or ladders on the new position and updates accordingly.
     * Then advances the turn to the next player.
     * 
     * @param dice The dice roll number
     * @return The final position of the player after moving and any jumps
     */
    public int movePlayer(int dice) {
        positions[currentPlayer] += dice;
        int pos = positions[currentPlayer];

        System.out.println("[Server] Player " + currentPlayer + " rolled " + dice + " → landed on " + pos);

        // If player lands on a snake or ladder, update position accordingly
        if (snakesAndLadders.containsKey(pos)) {
            int oldPos = pos;
            pos = snakesAndLadders.get(pos);
            System.out.println("[Server] Player " + currentPlayer + " moved from " + oldPos + " to " + pos + " via snake or ladder.");
        }

        positions[currentPlayer] = pos;

        // Pass the turn to the next player (wrap around)
        currentPlayer = (currentPlayer + 1) % positions.length;
        return pos;
    }

    /**
     * Initializes the positions of snakes and ladders on the board.
     * Keys represent the start tile, values represent the destination tile.
     * 
     * @return Map containing snakes and ladders
     */
    private Map<Integer, Integer> initializeSnakesAndLadders() {
        Map<Integer, Integer> map = new HashMap<>();

        // Snakes (slide down)
        map.put(21, 2);
        map.put(27, 15);
        map.put(47, 29);
        map.put(56, 37);
        map.put(74, 46);
        map.put(90, 52);
        map.put(99, 41);

        // Ladders (climb up)
        map.put(23, 42);
        map.put(32, 51);
        map.put(61, 79);
        map.put(65, 84);
        map.put(75, 96);

        return map;
    }
}