/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author sozcu
 */
public class ClientMain {

    public static void main(String[] args) {
        String serverIp = "51.20.104.21"; // örneğin: "3.122.45.67"
        int port = 5000;

       try (Socket socket = new Socket(serverIp, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Sunucudan gelen karşılama mesajı
            String response = in.readLine();
            System.out.println("Server said: " + response);

            // (İsteğe bağlı) Sunucuya mesaj gönder
            // out.println("Hello from Client");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}