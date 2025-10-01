// File: src/com/chatapp/adapter/SocketClientAdapter.java
package com.chatapp.adapter;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.patterns.ChatRoomManager;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

// Adapter to support socket clients (telnet/netcat). Implements Observer registration by interacting with ChatRoomManager
public class SocketClientAdapter implements ClientAdapter {
    private static final Logger LOGGER = Logger.getLogger(SocketClientAdapter.class.getName());

    private final Socket socket;
    private volatile boolean active = true;
    private BufferedReader reader;
    private BufferedWriter writer;
    private User user;

    public SocketClientAdapter(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            sendLine("Welcome to RealTimeChat!\nCommands:\nJOIN <roomId> <username>\nMSG <message>\nPRIVATE <username> <message>\nLIST\nHISTORY\nLEAVE\nQUIT");

            String line;
            while (active && (line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    handleCommand(line);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error handling command", e);
                    sendLine("ERROR: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Client disconnected abruptly", e);
        } finally {
            cleanup();
        }
    }

    private void handleCommand(String line) throws IOException {
        String[] tokens = line.split(" ", 3);
        String cmd = tokens[0].toUpperCase();
        switch (cmd) {
            case "JOIN":
                if (tokens.length < 3) {
                    sendLine("Usage: JOIN <roomId> <username>");
                    return;
                }
                String roomId = tokens[1].trim();
                String username = tokens[2].trim();
                // create user and join
                user = new User(username, this);
                ChatRoomManager.getInstance().joinRoom(roomId, user);
                sendLine("Joined room: " + roomId);
                break;
            case "MSG":
                if (user == null || user.getRoomId() == null) {
                    sendLine("You must JOIN a room before sending messages.");
                    return;
                }
                if (tokens.length < 2) {
                    sendLine("Usage: MSG <message>");
                    return;
                }
                String msg = line.substring(4); // preserve spaces
                Message message = new Message(user.getUsername(), msg, Instant.now());
                ChatRoomManager.getInstance().sendMessage(user.getRoomId(), message);
                break;
            case "PRIVATE":
                if (tokens.length < 3) {
                    sendLine("Usage: PRIVATE <username> <message>");
                    return;
                }
                if (user == null || user.getRoomId() == null) {
                    sendLine("You must JOIN a room before sending private messages.");
                    return;
                }
                String target = tokens[1].trim();
                String pmsg = tokens[2].trim();
                ChatRoomManager.getInstance().sendPrivateMessage(user.getRoomId(), user.getUsername(), target, pmsg);
                break;
            case "LIST":
                if (user == null || user.getRoomId() == null) {
                    sendLine("You must JOIN a room to list users.");
                    return;
                }
                sendLine("Active users: " + String.join(", ", ChatRoomManager.getInstance().listUsers(user.getRoomId())));
                break;
            case "HISTORY":
                if (user == null || user.getRoomId() == null) {
                    sendLine("You must JOIN a room to view history.");
                    return;
                }
                var history = ChatRoomManager.getInstance().getHistory(user.getRoomId());
                if (history.isEmpty()) sendLine("No history.");
                else {
                    for (Message m : history) sendLine(formatMessage(m));
                }
                break;
            case "LEAVE":
                if (user != null && user.getRoomId() != null) {
                    ChatRoomManager.getInstance().leaveRoom(user.getRoomId(), user.getUsername());
                    user.setRoomId(null);
                    sendLine("Left room");
                } else sendLine("Not in a room");
                break;
            case "QUIT":
                sendLine("Goodbye!");
                active = false;
                break;
            default:
                sendLine("Unknown command: " + cmd);
        }
    }

    public synchronized void sendLine(String s) {
        try {
            writer.write(s + "\n");
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to send to client", e);
            active = false;
            cleanup();
        }
    }

    private String formatMessage(Message m) {
        return String.format("[%s] %s: %s", m.getTimestamp().toString(), m.getSender(), m.getText());
    }

    private void cleanup() {
        try {
            if (user != null && user.getRoomId() != null) {
                ChatRoomManager.getInstance().leaveRoom(user.getRoomId(), user.getUsername());
            }
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.FINER, "Error during cleanup", e);
        }
    }
}

