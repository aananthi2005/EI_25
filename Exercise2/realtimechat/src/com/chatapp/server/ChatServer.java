package com.chatapp.server;

import com.chatapp.adapter.ClientAdapter;
import com.chatapp.adapter.SocketClientAdapter;
import com.chatapp.patterns.ChatRoomManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

// Singleton ChatServer - entry point to accept client connections (socket-based)
public class ChatServer {
    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());
    private static ChatServer instance;

    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    private ChatServer() {}

    public static synchronized ChatServer getInstance() {
        if (instance == null) {
            instance = new ChatServer();
        }
        return instance;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            LOGGER.info(() -> "ChatServer started on port " + port);

            // Accept loop - configuration-driven (not while(true) hardcoded flag)
            while (running && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.info(() -> "Accepted connection from " + clientSocket.getRemoteSocketAddress());
                    ClientAdapter adapter = new SocketClientAdapter(clientSocket);
                    clientPool.submit(adapter);
                } catch (IOException e) {
                    if (running) {
                        LOGGER.log(Level.SEVERE, "Error accepting client connection", e);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to start server", e);
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing server socket", e);
        }
        clientPool.shutdownNow();
        ChatRoomManager.getInstance().shutdown();
        LOGGER.info("Server stopped");
    }
}
