// File: src/com/chatapp/patterns/ChatRoomManager.java
package com.chatapp.patterns;

import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

// Singleton ChatRoomManager manages rooms and acts as Facade for server operations
public class ChatRoomManager {
    private static final Logger LOGGER = Logger.getLogger(ChatRoomManager.class.getName());

    private static ChatRoomManager instance;

    private final Map<String, ChatRoom> rooms = new ConcurrentHashMap<>();

    private ChatRoomManager() {}

    public static synchronized ChatRoomManager getInstance() {
        if (instance == null) instance = new ChatRoomManager();
        return instance;
    }

    public void joinRoom(String roomId, User user) {
        ChatRoom room = rooms.computeIfAbsent(roomId, ChatRoom::new);
        room.join(user);
        LOGGER.info(() -> user.getUsername() + " joined " + roomId);
    }

    public void leaveRoom(String roomId, String username) {
        ChatRoom room = rooms.get(roomId);
        if (room != null) room.leave(username);
    }

    public void sendMessage(String roomId, Message message) {
        ChatRoom room = rooms.get(roomId);
        if (room != null) room.broadcast(message);
    }

    public boolean sendPrivateMessage(String roomId, String from, String to, String text) {
        ChatRoom room = rooms.get(roomId);
        if (room == null) return false;
        return room.sendPrivate(from, to, text);
    }

    public List<String> listUsers(String roomId) {
        ChatRoom room = rooms.get(roomId);
        if (room == null) return List.of();
        return room.listUsers();
    }

    public List<Message> getHistory(String roomId) {
        ChatRoom room = rooms.get(roomId);
        if (room == null) return List.of();
        return room.getHistory();
    }

    public void shutdown() {
        rooms.clear();
        LOGGER.info("ChatRoomManager shutdown");
    }
}