package logic;

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
    private int[] chokkaCounts;
    private int winingPosition;
    private int currentPlayer;
    private final int totalPlayers;

    public GameLogic(int totalPlayers) {
        this.totalPlayers = totalPlayers;
        this.playerPositions = new int[totalPlayers];
        this.chokkaCounts = new int[totalPlayers];
        this.currentPlayer = 0;
        this.winingPosition = 0;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int rollDice() {
        return (int) (Math.random() * 6) + 1;
    }

    public int getPlayerPosition(int playerIndex) {
        return playerPositions[playerIndex];
    }

    public int movePlayer(int diceValue) {
        int player = currentPlayer;

        if (diceValue == 6) chokkaCounts[player]++;
        else chokkaCounts[player] = 0;

        int newPosition = playerPositions[player] + diceValue;

        if (newPosition > 100) {
            newPosition = playerPositions[player];
        } else if (chokkaCounts[player] == 3) {
            newPosition = playerPositions[player];
        } else {
            newPosition = checkSnakeOrLadder(newPosition);
        }

        playerPositions[player] = newPosition;

        if (newPosition == 100) winingPosition++;

        if (diceValue != 6) currentPlayer = (currentPlayer + 1) % totalPlayers;

        return newPosition;
    }

    public boolean isGameOver() {
        return winingPosition == totalPlayers;
    }

    public boolean isWinner(int playerIndex) {
        return playerPositions[playerIndex] == 100;
    }

    public int checkSnakeOrLadder(int pos) {
        switch (pos) {
            case 21: return 2;
            case 27: return 15;
            case 23: return 42;
            case 32: return 51;
            case 47: return 29;
            case 56: return 37;
            case 61: return 79;
            case 65: return 84;
            case 74: return 46;
            case 75: return 96;
            case 90: return 52;
            case 99: return 41;
            default: return pos;
        }
    }

    public void resetGame() {
        for (int i = 0; i < totalPlayers; i++) {
            playerPositions[i] = 0;
            chokkaCounts[i] = 0;
        }
        winingPosition = 0;
        currentPlayer = 0;
    }
}
