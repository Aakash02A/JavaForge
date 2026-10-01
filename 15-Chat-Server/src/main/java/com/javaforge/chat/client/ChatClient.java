package com.javaforge.chat.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ChatClient: Connects to the ChatServer and enables two-way communication.
 * Runs two threads:
 *   1. A reader thread to receive messages from the server.
 *   2. The main thread to send messages typed by the user.
 */
public class ChatClient {
    private final String host;
    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        System.out.println("[Client] Connecting to server at " + host + ":" + port);
        try (
            Socket socket = new Socket(host, port);
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("[Client] Connected! Type messages below (type /quit to exit).");

            // Thread: reads messages from server and prints them
            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    System.out.println("[Client] Disconnected from server.");
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

            // Main thread: reads user input and sends to server
            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                writer.println(userInput);
                if (userInput.equalsIgnoreCase("/quit")) break;
            }
        } catch (Exception e) {
            System.out.println("[Client] Could not connect: " + e.getMessage());
        }
    }
}
