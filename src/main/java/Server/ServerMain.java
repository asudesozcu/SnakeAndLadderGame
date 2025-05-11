package Server;

import java.net.*;
import java.io.*;

public class ServerMain {
    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            // Sonsuz döngüyle sürekli yeni bağlantıları kabul eder
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New client: " + client.getRemoteSocketAddress());
                // Basit demo: client’a hoş geldin mesajı gönderip kapat:
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("Welcome to Snakes and ladders Server!");
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
