package com.javaforge.chat.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * ChatServer: Accepts incoming client connections and manages all active clients.
 * Each client is handled in its own thread via ClientHandler.
 * Uses CopyOnWriteArraySet for a thread-safe set of connected writers.
 */
public class ChatServer {
    private final int port;
    // Thread-safe set of writers, one per connected client
    private final Set<PrintWriter> clientWriters = new CopyOnWriteArraySet<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("[Server] Chat Server started on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] New client connected: " + clientSocket.getInetAddress());
                // Spawn a new thread for each client
                ClientHandler handler = new ClientHandler(clientSocket, clientWriters);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.out.println("[Server] Server stopped: " + e.getMessage());
        }
    }

    public void broadcast(String message, PrintWriter excludeWriter) {
        for (PrintWriter writer : clientWriters) {
            if (writer != excludeWriter) {
                writer.println(message);
            }
        }
    }
}
