/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import logic.GameLogic;

/**
 *
 * @author sozcu
 */
public class GameHandler implements Runnable {
    private final Socket player1;
    private final Socket player2;
    private final GameLogic gameLogic = new GameLogic(2);

    private volatile int selectedPawn = -1;
    private volatile boolean pawnSelected = false;

    private PrintWriter out1;
    private PrintWriter out2;

    public GameHandler(Socket p1, Socket p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    @Override
    public void run() {
        try {
            out1 = new PrintWriter(player1.getOutputStream(), true);
            out2 = new PrintWriter(player2.getOutputStream(), true);

            System.out.println("[GameHandler] Players connected:");
            System.out.println("  Player 1: " + player1.getInetAddress() + ":" + player1.getPort());
            System.out.println("  Player 2: " + player2.getInetAddress() + ":" + player2.getPort());

            // Başlangıç mesajları
            out1.println("START 1");
            out2.println("START 2");
            System.out.println("[GameHandler] Sent START messages to both players");

            if (pawnSelected) {
                out2.println("PAWN_TAKEN " + selectedPawn);
                System.out.println("[GameHandler] Sent existing PAWN_TAKEN " + selectedPawn + " to Player 2");
            }

            out1.println("TURN 1");
            System.out.println("[GameHandler] Sent TURN 1 to Player 1");

            new Thread(() -> listen(player1, 1, out1, out2)).start();
            new Thread(() -> listen(player2, 2, out2, out1)).start();

        } catch (IOException e) {
            System.err.println("[GameHandler] Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listen(Socket socket, int playerNo, PrintWriter outSelf, PrintWriter outOther) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("[GameHandler] Received from Player " + playerNo + ": " + line);

                if (line.startsWith("PAWN")) {
                    selectedPawn = Integer.parseInt(line.split(" ")[1]);
                    pawnSelected = true;
                    System.out.println("[GameHandler] Player " + playerNo + " selected pawn: " + selectedPawn);

                    System.out.println("[GameHandler] Sending PAWN_TAKEN " + selectedPawn + " to Player " + (3 - playerNo));
                    outOther.println("PAWN_TAKEN " + selectedPawn);
                    outOther.flush();
                    System.out.println("[GameHandler] PAWN_TAKEN sent successfully");
                }

                if (line.equals("ROLL") && gameLogic.getCurrentPlayer() == playerNo - 1) {
                    int dice = gameLogic.rollDice();
                    int newPos = gameLogic.movePlayer(dice);

                    outSelf.println("MOVE " + playerNo + " " + dice + " " + newPos);
                    outOther.println("MOVE " + playerNo + " " + dice + " " + newPos);

                    System.out.println("[GameHandler] Player " + playerNo + " rolled " + dice + " and moved to " + newPos);

                    if (gameLogic.isWinner(playerNo - 1)) {
                        outSelf.println("WINNER " + playerNo);
                        outOther.println("GAMEOVER");
                        System.out.println("[GameHandler] Player " + playerNo + " won the game!");
                        break;
                    }

                    int next = gameLogic.getCurrentPlayer() + 1;
                    outSelf.println("TURN " + next);
                    outOther.println("TURN " + next);
                    System.out.println("[GameHandler] Sent TURN " + next + " to both players");
                }
            }
        } catch (IOException e) {
            System.err.println("[GameHandler] Connection lost with Player " + playerNo + ": " + e.getMessage());
            try {
                outOther.println("DISCONNECTED");
                outOther.flush();
            } catch (Exception ignored) {}
        }

        try {
            socket.close();
        } catch (IOException ignored) {}

        System.out.println("[GameHandler] Player " + playerNo + " disconnected.");
    }
}