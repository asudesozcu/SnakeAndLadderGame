/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

/**
 *
 * @author sozcu
 */
public interface GameMessageListener {
    void onTurn(int currentPlayer);
    void onMove(int player, int dice, int position);
    void onPawnTaken(int pawnId);
    void onWin();
    void onGameOver();
    void onDisconnected();
}