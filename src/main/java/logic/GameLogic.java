package logic;

import java.util.HashMap;
import java.util.Map;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sozcu
 */

public class GameLogic {
    private int[] playerPositions;
    private int[] consecutiveSixes;
    private int finishedPlayers;
    private int currentPlayer;
    private final int totalPlayers;

    private final Map<Integer, Integer> snakesAndLadders;

    public GameLogic(int totalPlayers) {
        this.totalPlayers = totalPlayers;
        this.playerPositions = new int[totalPlayers];
        this.consecutiveSixes = new int[totalPlayers];
        this.currentPlayer = 0;
        this.finishedPlayers = 0;
        this.snakesAndLadders = initializeSnakesAndLadders();
    }

    /**
     * Initializes all snakes and ladders on the board.
     * @return Map of start positions to end positions.
     */
    private Map<Integer, Integer> initializeSnakesAndLadders() {
        Map<Integer, Integer> map = new HashMap<>();
        // Snakes (from > to)
        map.put(21, 2);
        map.put(27, 15);
        map.put(47, 29);
        map.put(56, 37);
        map.put(74, 46);
        map.put(90, 52);
        map.put(99, 41);

        // Ladders (from < to)
        map.put(23, 42);
        map.put(32, 51);
        map.put(61, 79);
        map.put(65, 84);
        map.put(75, 96);

        return map;
    }

    /**
     * Returns the current player's index (0-based).
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Rolls a fair 6-sided dice.
     */
    public int rollDice() {
        return (int) (Math.random() * 6) + 1;
    }

    /**
     * Gets the current position of the specified player.
     */
    public int getPlayerPosition(int playerIndex) {
        return playerPositions[playerIndex];
    }

    /**
     * Moves the current player based on dice value and game rules.
     * Handles snakes, ladders, boundaries, and triple six logic.
     * @param diceValue The value from 1 to 6.
     * @return The updated player position.
     */
    public int movePlayer(int diceValue) {
        int player = currentPlayer;

        // Track consecutive sixes
        if (diceValue == 6) {
            consecutiveSixes[player]++;
        } else {
            consecutiveSixes[player] = 0;
        }

        // Triple six rule (player loses turn)
        if (consecutiveSixes[player] == 3) {
            consecutiveSixes[player] = 0; // reset
            advanceTurn(); // skip turn
            return playerPositions[player]; // no movement
        }

        int newPosition = playerPositions[player] + diceValue;

        // Cannot go beyond 100
        if (newPosition > 100) {
            newPosition = playerPositions[player]; // stay
        } else {
            // Check for snake or ladder
            newPosition = snakesAndLadders.getOrDefault(newPosition, newPosition);
        }

        playerPositions[player] = newPosition;

        // Check if player won
        if (newPosition == 100) {
            finishedPlayers++;
        }

        // If not a six, move to next player
        if (diceValue != 6) {
            advanceTurn();
        }

        return newPosition;
    }

    /**
     * Advances to the next player.
     */
    private void advanceTurn() {
        currentPlayer = (currentPlayer + 1) % totalPlayers;
    }

    /**
     * Checks if the game is finished.
     */
    public boolean isGameOver() {
        return finishedPlayers == totalPlayers;
    }

    /**
     * Checks if a player has reached position 100.
     */
    public boolean isWinner(int playerIndex) {
        return playerPositions[playerIndex] == 100;
    }

    /**
     * Resets all player positions and game state.
     */
    public void resetGame() {
        for (int i = 0; i < totalPlayers; i++) {
            playerPositions[i] = 0;
            consecutiveSixes[i] = 0;
        }
        finishedPlayers = 0;
        currentPlayer = 0;
    }
}