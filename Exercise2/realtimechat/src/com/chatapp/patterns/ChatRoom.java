// File: src/com/chatapp/patterns/ChatRoom.java
package com.chatapp.patterns;

import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

// ChatRoom acts as Subject in Observer pattern: notifies users (observers) on new messages/user events
public class ChatRoom {
    private static final Logger LOGGER = Logger.getLogger(ChatRoom.class.getName());

    private final String roomId;
    private final Map<String, User> users = new HashMap<>();
    private final List<Message> history = new CopyOnWriteArrayList<>();

    public ChatRoom(String roomId) {
        this.roomId = roomId;
    }

    public synchronized void join(User user) {
        users.put(user.getUsername(), user);
        user.setRoomId(roomId);
        broadcastSystem(String.format("%s joined the room", user.getUsername()));
        notifyUserList();
    }

    public synchronized void leave(String username) {
        users.remove(username);
        broadcastSystem(String.format("%s left the room", username));
        notifyUserList();
    }

    public synchronized void broadcast(Message message) {
        history.add(message);
        for (User u : users.values()) {
            try {
                if (u.getAdapter() instanceof com.chatapp.adapter.SocketClientAdapter) {
                    ((com.chatapp.adapter.SocketClientAdapter) u.getAdapter()).sendLine(formatMessage(message));
                }
            } catch (Exception e) {
                LOGGER.warning("Failed to notify user: " + u.getUsername());
            }
        }
    }

    public synchronized boolean sendPrivate(String from, String to, String text) {
        User target = users.get(to);
        if (target == null) return false;
        Message message = new Message(from + " (private)", text, java.time.Instant.now());
        try {
            if (target.getAdapter() instanceof com.chatapp.adapter.SocketClientAdapter) {
                ((com.chatapp.adapter.SocketClientAdapter) target.getAdapter()).sendLine(formatMessage(message));
            }
            // also echo to sender
            User sender = users.get(from);
            if (sender != null && sender.getAdapter() instanceof com.chatapp.adapter.SocketClientAdapter) {
                ((com.chatapp.adapter.SocketClientAdapter) sender.getAdapter()).sendLine(formatMessage(message));
            }
        } catch (Exception e) {
            LOGGER.warning("Private message delivery failed");
            return false;
        }
        return true;
    }

    public synchronized List<String> listUsers() {
        return new ArrayList<>(users.keySet());
    }

    public synchronized List<Message> getHistory() {
        return new ArrayList<>(history);
    }

    private void notifyUserList() {
        String list = "Active users: " + String.join(", ", users.keySet());
        for (User u : users.values()) {
            try {
                if (u.getAdapter() instanceof com.chatapp.adapter.SocketClientAdapter) {
                    ((com.chatapp.adapter.SocketClientAdapter) u.getAdapter()).sendLine(list);
                }
            } catch (Exception e) {
                LOGGER.warning("Failed to send user list to " + u.getUsername());
            }
        }
    }

    private void broadcastSystem(String text) {
        Message sys = new Message("SYSTEM", text, java.time.Instant.now());
        broadcast(sys);
    }

    private String formatMessage(Message m) {
        return String.format("[%s] %s: %s", m.getTimestamp().toString(), m.getSender(), m.getText());
    }
}

