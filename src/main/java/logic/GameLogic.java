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

public class GameLogic {
   private final int[] positions;
    private int currentPlayer;
    private final Random rand;


    public GameLogic(int playerCount) {
        this.positions = new int[playerCount];
        this.currentPlayer = 0;
        this.rand = new Random();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int rollDice() {
        return rand.nextInt(6) + 1;
    }

  


    public boolean isWinner(int playerIndex) {
        return positions[playerIndex] >= 100;
    }

    public int getPlayerPosition(int index) {
        return positions[index];
    }
    private final Map<Integer, Integer> snakesAndLadders = initializeSnakesAndLadders();

public int movePlayer(int dice) {
    positions[currentPlayer] += dice;

    // ✅ Snake/Ladder uygulanıyor
    int original = positions[currentPlayer];
    positions[currentPlayer] = snakesAndLadders.getOrDefault(original, original);

    int newPos = positions[currentPlayer];

    System.out.println("[Server] Player " + currentPlayer + " rolled " + dice + " → moved to " + original);
    if (newPos != original) {
        System.out.println("[Server] Snake or ladder triggered: " + original + " → " + newPos);
    }

    currentPlayer = (currentPlayer + 1) % positions.length;
    return newPos;
}

private Map<Integer, Integer> initializeSnakesAndLadders() {
    Map<Integer, Integer> map = new HashMap<>();
    map.put(21, 2);
    map.put(27, 15);
    map.put(47, 29);
    map.put(56, 37);
    map.put(74, 46);
    map.put(90, 52);
    map.put(99, 41);
    map.put(23, 42);
    map.put(32, 51);
    map.put(61, 79);
    map.put(65, 84);
    map.put(75, 96);
    return map;
}

} 