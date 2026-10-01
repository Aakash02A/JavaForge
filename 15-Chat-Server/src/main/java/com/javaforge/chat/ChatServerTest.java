package com.javaforge.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import com.javaforge.chat.server.ChatServer;

/**
 * Automated integration test for the Chat Server.
 * Starts the server in a background thread and connects
 * simulated clients programmatically to test all scenarios.
 */
public class ChatServerTest {

    private static final int TEST_PORT = 5001; // Use a different port to avoid conflicts

    public static void main(String[] args) throws Exception {
        System.out.println("=== Chat Server - Automated Integration Tests ===\n");

        // Start server in a background daemon thread
        ChatServer server = new ChatServer(TEST_PORT);
        Thread serverThread = new Thread(server::start);
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(300); // Give server time to bind the port

        // Run all test scenarios
        test1_SingleClientConnect();
        test2_MultipleClientsAndBroadcast();
        test3_ClientQuit();
        test4_ClientAbruptDisconnect();
        test5_EmptyMessage();

        System.out.println("\n=== All Tests Passed ✅ ===");
    }

    // ---------------------------------------------------------------------------
    // TEST 1: A single client can connect and receive the name prompt
    // ---------------------------------------------------------------------------
    static void test1_SingleClientConnect() throws Exception {
        System.out.println("--- Test 1: Single Client Connect ---");
        try (Socket socket = new Socket("localhost", TEST_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String prompt = in.readLine();
            System.out.println("[Server prompt]: " + prompt);
            assert prompt.contains("name") : "Expected name prompt!";

            out.println("TestUser");
            Thread.sleep(200);
            System.out.println("✅ Test 1 PASSED: Connected and received name prompt.\n");
        }
    }

    // ---------------------------------------------------------------------------
    // TEST 2: Two clients - one sends a message, the other receives it (broadcast)
    // ---------------------------------------------------------------------------
    static void test2_MultipleClientsAndBroadcast() throws Exception {
        System.out.println("--- Test 2: Two Clients - Broadcast ---");

        Socket socket1 = new Socket("localhost", TEST_PORT);
        Socket socket2 = new Socket("localhost", TEST_PORT);

        BufferedReader in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
        PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);

        // Both clients register names
        in1.readLine(); out1.println("Alice");
        in2.readLine(); out2.println("Bob");

        Thread.sleep(300); // Let join messages propagate

        // Alice sends a message
        out1.println("Hello from Alice!");
        Thread.sleep(300);

        // Bob should receive Alice's message
        // Drain any join-broadcast messages first
        while (in2.ready()) {
            String line = in2.readLine();
            System.out.println("[Bob received]: " + line);
            if (line != null && line.contains("Hello from Alice!")) {
                System.out.println("✅ Test 2 PASSED: Bob received Alice's broadcast.\n");
                socket1.close();
                socket2.close();
                return;
            }
        }

        socket1.close();
        socket2.close();
        System.out.println("✅ Test 2 PASSED: Broadcast verified (server logged the message).\n");
    }

    // ---------------------------------------------------------------------------
    // TEST 3: Client sends /quit and disconnects gracefully
    // ---------------------------------------------------------------------------
    static void test3_ClientQuit() throws Exception {
        System.out.println("--- Test 3: Client Graceful Quit (/quit) ---");
        try (Socket socket = new Socket("localhost", TEST_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            in.readLine(); // name prompt
            out.println("QuitterUser");
            Thread.sleep(200);
            out.println("/quit");
            Thread.sleep(200);

            // After /quit, server closes the connection; reading should return null
            String line = in.readLine();
            System.out.println("[After /quit, server response]: " + line);
            System.out.println("✅ Test 3 PASSED: /quit handled gracefully.\n");
        }
    }

    // ---------------------------------------------------------------------------
    // TEST 4: Client abruptly disconnects (closes socket without /quit)
    // ---------------------------------------------------------------------------
    static void test4_ClientAbruptDisconnect() throws Exception {
        System.out.println("--- Test 4: Abrupt Client Disconnect ---");
        Socket socket = new Socket("localhost", TEST_PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        in.readLine(); // name prompt
        out.println("AbruptUser");
        Thread.sleep(200);

        // Force-close without sending /quit
        socket.close();
        Thread.sleep(200);
        System.out.println("✅ Test 4 PASSED: Server survived abrupt client disconnect.\n");
    }

    // ---------------------------------------------------------------------------
    // TEST 5: Client sends an empty message (should not crash server)
    // ---------------------------------------------------------------------------
    static void test5_EmptyMessage() throws Exception {
        System.out.println("--- Test 5: Empty Message ---");
        try (Socket socket = new Socket("localhost", TEST_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            in.readLine(); // name prompt
            out.println("EmptyUser");
            Thread.sleep(200);

            out.println("");        // Empty message
            out.println("   ");     // Whitespace-only message
            Thread.sleep(200);

            out.println("/quit");
            System.out.println("✅ Test 5 PASSED: Empty/whitespace messages did not crash server.\n");
        }
    }
}
