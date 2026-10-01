package com.javaforge.chat;

import com.javaforge.chat.client.ChatClient;
import com.javaforge.chat.server.ChatServer;

/**
 * Main entry point for the Chat Server project.
 *
 * USAGE:
 *   Run as Server:  java -cp out com.javaforge.chat.Main server
 *   Run as Client:  java -cp out com.javaforge.chat.Main client
 *
 * How to Demo:
 *   1. Open a terminal. Run as "server" - it will start listening.
 *   2. Open another terminal. Run as "client" - enter your name.
 *   3. Open a third terminal. Run as "client" again - enter a different name.
 *   4. Type a message in one client terminal and see it appear in the other!
 */
public class Main {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Main [server|client]");
            System.out.println("  server - Start the Chat Server");
            System.out.println("  client - Connect as a Chat Client");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "server" -> {
                ChatServer server = new ChatServer(PORT);
                server.start();
            }
            case "client" -> {
                ChatClient client = new ChatClient(HOST, PORT);
                client.connect();
            }
            default -> System.out.println("Unknown mode. Use 'server' or 'client'.");
        }
    }
}
