/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
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

    public GameHandler(Socket p1, Socket p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    public void run() {
        try {
            PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
            PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

            out1.println("START 1");
            out2.println("START 2");
            out1.println("TURN 1");
            out2.println("TURN 1");

            new Thread(() -> listen(player1, 1, out1, out2)).start();
            new Thread(() -> listen(player2, 2, out2, out1)).start();

        } catch (IOException e) {
            System.out.println("Error sending START message.");
        }
    }

    private void handleMove(BufferedReader in, PrintWriter outSelf, PrintWriter outOther, int playerNo) throws IOException {
        String line = in.readLine();
        if (line.equals("ROLL") && gameLogic.getCurrentPlayer() == playerNo - 1) {
            int dice = gameLogic.rollDice();
            int pos = gameLogic.movePlayer(dice);
            outSelf.println("MOVE " + playerNo + " " + dice + " " + pos);
            outOther.println("MOVE " + playerNo + " " + dice + " " + pos);

            if (gameLogic.isWinner(playerNo - 1)) {
                outSelf.println("WINNER " + playerNo);
                outOther.println("WINNER " + playerNo);
            }

            int next = gameLogic.getCurrentPlayer() + 1;
            outSelf.println("TURN " + next);
            outOther.println("TURN " + next);
        }
    }

    private void tryReconnect(Socket disconnected, Socket opponent) {
        try {
            if (!opponent.isClosed()) {
                PrintWriter out = new PrintWriter(opponent.getOutputStream(), true);
                out.println("DISCONNECTED");
            }
        } catch (IOException ignored) {}

        try { if (!disconnected.isClosed()) disconnected.close(); } catch (IOException ignored) {}
        try { if (!opponent.isClosed()) opponent.close(); } catch (IOException ignored) {}
    }

    private void listen(Socket socket, int playerNo, PrintWriter outSelf, PrintWriter outOther) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("ROLL") && gameLogic.getCurrentPlayer() == playerNo - 1) {
                    int dice = gameLogic.rollDice();
                    int newPosition = gameLogic.movePlayer(dice);

                    outSelf.println("MOVE " + playerNo + " " + dice + " " + newPosition);
                    outOther.println("MOVE " + playerNo + " " + dice + " " + newPosition);

                    if (gameLogic.isWinner(playerNo - 1)) {
                        outSelf.println("WINNER " + playerNo);
                        outOther.println("WINNER " + playerNo);
                    }

                    int next = gameLogic.getCurrentPlayer() + 1;
                    outSelf.println("TURN " + next);
                    outOther.println("TURN " + next);
                }
            }

            System.out.println("Player " + playerNo + " disconnected.");
            try {
                outOther.println("DISCONNECTED");
            } catch (Exception ex) {
                System.out.println("Error sending DISCONNECTED: " + ex.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Connection lost with player " + playerNo);
            try {
                outOther.println("DISCONNECTED");
            } catch (Exception ex) {
                System.out.println("Error notifying opponent: " + ex.getMessage());
            }
        }

        try { socket.close(); } catch (IOException ignored) {}
    }
}