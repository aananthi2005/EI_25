package com.chatapp.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Simplified ChatClient
 * Automatically joins a room and lets user type messages directly.
 */
public class ChatClient {

    private final String hostname;
    private final int port;
    private final String username;
    private final String roomId;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClient(String hostname, int port, String roomId, String username) {
        this.hostname = hostname;
        this.port = port;
        this.roomId = roomId;
        this.username = username;
    }

    public void start() {
        try {
            socket = new Socket(hostname, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Automatically send JOIN command
            out.println("JOIN " + roomId + " " + username);

            System.out.println("Joined room " + roomId + " as " + username);
            System.out.println("Type your messages below. Type /quit to leave.");

            // Start thread to listen for server messages
            new Thread(new IncomingReader()).start();

            Scanner scanner = new Scanner(System.in);
    while (true) {
        String message = scanner.nextLine().trim();

        if (message.equalsIgnoreCase("/quit")) {
        out.println("LEAVE");
        break;
        }
    // Commands start with HISTORY, LIST, PRIVATE
        else if (message.equalsIgnoreCase("HISTORY") ||
             message.equalsIgnoreCase("LIST") ||
             message.toUpperCase().startsWith("PRIVATE")) {
        out.println(message);  // send command as-is
    } 
        else {
            out.println("MSG " + message);  // normal chat message
    }
        }


            shutdown();

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    private void shutdown() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Disconnected.");
        } catch (IOException e) {
            System.err.println("Error closing client: " + e.getMessage());
        }
    }

    // Thread to listen for server messages
    private class IncomingReader implements Runnable {
        public void run() {
            String serverMessage;
            try {
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                System.err.println("Connection lost: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Room ID: ");
        String roomId = scanner.nextLine();

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        String host = "localhost";
        int port = 12345;

        ChatClient client = new ChatClient(host, port, roomId, username);
        client.start();
    }
}
