
# **RealTimeChat Application**

## **Overview**
The **RealTimeChat** project is a console-based chat application built using **Java sockets**.  
It allows multiple clients to join chat rooms, send public/private messages, view history, and leave gracefully.  

---

## **Features**
- **Server** that manages rooms and connected clients  
- **Client** that can:
  - **Join a room**
  - **Send public messages**
  - **Send private messages**
  - **View chat history**
  - **Leave the room gracefully**

---

```## **Project Structure**
RealTimeChat/
├── server/
│ ├── ChatServer.java
│ └── ChatRoom.java
├── client/
│ ├── ChatClient.java
│ └── ClientHandler.java
├── run_server.bat
└── run_client.bat
```

---

## **How to Run**

### **1. Start the Server**
Run the batch file (or compile manually):  
```bash
run_server.bat
```

### **2. Start Clients**

Run the client batch file:

```
run_client.bat
```
Enter a username and room ID to join.

### **3. Available Commands**
```
JOIN <roomId> <username>   → Join a room
MSG <message>              → Send a message
PRIVATE <username> <msg>   → Private message
LIST                       → List users
HISTORY                    → Show chat history
LEAVE                      → Exit the room
```
