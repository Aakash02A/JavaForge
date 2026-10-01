package com.javaforge.chat.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

/**
 * ClientHandler: Manages I/O for a single connected client in its own thread.
 * Reads messages from the client and broadcasts them to all other connected clients.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Set<PrintWriter> clientWriters;
    private PrintWriter writer;
    private String clientName;

    public ClientHandler(Socket socket, Set<PrintWriter> clientWriters) {
        this.socket = socket;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.writer = writer;
            clientWriters.add(writer);

            // First message from client is its name
            writer.println("Enter your name:");
            clientName = reader.readLine();
            broadcast("[Server] " + clientName + " has joined the chat!");

            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
                broadcast("[" + clientName + "]: " + message);
            }
        } catch (Exception e) {
            System.out.println("[Server] Client disconnected: " + clientName);
        } finally {
            if (writer != null) clientWriters.remove(writer);
            if (clientName != null) broadcast("[Server] " + clientName + " has left the chat.");
            try { socket.close(); } catch (Exception ignored) {}
        }
    }

    private void broadcast(String message) {
        System.out.println(message); // Log on server
        for (PrintWriter w : clientWriters) {
            w.println(message);
        }
    }
}
