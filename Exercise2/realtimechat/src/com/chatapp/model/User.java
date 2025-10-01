// File: src/com/chatapp/model/User.java
package com.chatapp.model;

import com.chatapp.adapter.SocketClientAdapter;
import com.chatapp.adapter.ClientAdapter;

public class User {
    private final String username;
    private final ClientAdapter adapter;
    private String roomId;

    public User(String username, ClientAdapter adapter) {
        this.username = username;
        this.adapter = adapter;
    }

    public String getUsername() {
        return username;
    }

    public ClientAdapter getAdapter() {
        return adapter;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}

