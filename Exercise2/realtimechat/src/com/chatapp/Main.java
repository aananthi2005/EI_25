
package com.chatapp;

import com.chatapp.server.ChatServer;

public class Main {
public static void main(String[] args) {
int port = 12345;
if (args.length > 0) {
try {
port = Integer.parseInt(args[0]);
} catch (NumberFormatException e) {
System.out.println("Invalid port provided, using default 12345");
}
}

ChatServer server = ChatServer.getInstance();
server.start(port);

Runtime.getRuntime().addShutdownHook(new Thread(() -> {
System.out.println("Shutting down server...");
server.stop();
}));
}
}
