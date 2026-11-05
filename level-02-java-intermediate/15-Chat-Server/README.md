# 15 - Chat Server

Build a **Multi-Client Chat Server** where multiple users can connect and exchange messages in real time using Java Sockets.

# Problem Statement

Real-time communication between multiple users requires a server to relay messages between clients. Without a proper architecture, messages either don't reach the right users or the server crashes when multiple clients connect simultaneously.

The **Chat Server** uses Java's `ServerSocket` and `Socket` APIs with multi-threading to accept multiple client connections simultaneously and broadcast messages from one client to all others in real time.

# Objectives

- Start a server that listens for incoming client connections on a specific port.
- Accept multiple clients simultaneously using threads.
- Each client registers with a username.
- Broadcast any message from one client to all other connected clients.
- Notify all users when a client joins or leaves.
- Allow clients to gracefully disconnect with `/quit`.
- Survive abrupt client disconnections without crashing.

# Features

- Multi-Client TCP Chat Server
- Per-Client Thread (ClientHandler)
- Username Registration
- Real-Time Broadcast to All Clients
- Join / Leave Notifications
- Graceful `/quit` Disconnect
- Abrupt Disconnect Safety
- Server Console Logging

# Tech Stack

- Java (Intermediate)
- java.net.ServerSocket / Socket
- java.io.BufferedReader / PrintWriter
- java.lang.Thread / Runnable
- java.util.concurrent.CopyOnWriteArraySet (thread-safe client list)

# Suggested Class Structure

ChatServer
----------
port
clientWriters (CopyOnWriteArraySet<PrintWriter>)
start()

ClientHandler implements Runnable
-----------------------------------
socket
clientWriters
clientName
run()
broadcast()

ChatClient
----------
host
port
connect()

Main
----
main()  // switches between "server" and "client" mode via args

# How to Run

Requirements: JDK 21 and Maven. First compile the project:

```bash
mvn clean package
```

Start the server from this directory:

```bash
# Terminal 1
java -cp target/classes com.javaforge.chat.Main server
# Terminal 2 (and additional client terminals)
java -cp target/classes com.javaforge.chat.Main client
```

The server listens on port `5000`; clients connect to `localhost`. Enter a username when prompted. Type `/quit` to disconnect a client cleanly. Only one server process should bind to the configured port at a time.

# Sample Output (Server Terminal)

[Server] Chat Server started on port 5000
[Server] New client connected: /127.0.0.1
[Server] Alice has joined the chat!
[Server] New client connected: /127.0.0.1
[Server] Bob has joined the chat!
[Alice]: Hello Bob!
[Server] Bob has left the chat.

# Sample Output (Client Terminal - Bob)

[Client] Connected! Type messages below.
[Server] Alice has joined the chat!
[Server] Bob has joined the chat!
[Alice]: Hello Bob!

# Learning Outcomes

By completing this project, you will learn:

- How TCP Sockets work (`ServerSocket` and `Socket`)
- Accepting and handling multiple clients with threads
- Reading and writing over network streams (`BufferedReader`, `PrintWriter`)
- Using `CopyOnWriteArraySet` for thread-safe broadcasting
- Handling network IOExceptions (abrupt disconnects)
- Designing a two-thread client (reader thread + writer main thread)
- The Client-Server architecture pattern

# Project Details

Difficulty: ⭐⭐⭐⭐☆ (Intermediate-Advanced)

Estimated Time: 8–12 hours

Skills Gained:
- Java Sockets (TCP/IP)
- Multi-threading
- Client-Server Architecture
- Network I/O

## Tests

Run `mvn test` to compile and execute any configured tests. This project currently has no automated test classes; verify broadcasting by connecting at least two clients to the same server.
