/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author sozcu
 */
public class ServerHandler implements Runnable {
    private final Socket socket;
    private final int playerNumber;

    public ServerHandler(Socket socket, int playerNumber) {
        this.socket = socket;
        this.playerNumber = playerNumber;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeInt(playerNumber);
            out.writeInt(2); // hardcoded for 2-player version
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}