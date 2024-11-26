package Yootgame.source.backend.Handler;


import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.backend.multiroom.RoomManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class YutGameSessionHandler extends RoomConnectionHandler {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Room currentRoom;
    private Map<String, Integer> gameState;
    private List<PrintWriter> clientWriters;
    private RoomManager roomManager;  // RoomManager 필드 추가

    public YutGameSessionHandler(Socket socket, RoomManager roomManager,
                                 Map<String, Integer> gameState, List<PrintWriter> clientWriters) {
        super(socket, roomManager);
        this.socket = socket;
        this.gameState = gameState;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        try {
            setupStreams();
            handleClientCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void setupStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        synchronized (clientWriters) {
            clientWriters.add(out);
        }
        sendWelcomeMessage();
    }

    private void sendWelcomeMessage() {
        out.println("Connected to server. Available commands:");
        out.println("/create [roomName] [turnTime] [maxPlayers] - Create a new room");
        out.println("/join [roomName] - Join a room");
        out.println("/list - List all rooms");
        out.println("/leave - Leave current room");
        out.println("MOVE [player] [piece] [steps] - Move a game piece");
        out.println("RESET_STATE - Reset the game state");
    }

    private void handleClientCommunication() throws IOException {
        String message;
        while ((message = in.readLine()) != null) {
            System.out.println("Received: " + message);

            if (message.startsWith("/")) {
                handleRoomCommand(message);
            } else {
                handleGameCommand(message);
            }
        }
    }

    private void handleGameCommand(String message) {
        if (currentRoom == null) {
            out.println("Please join a room first.");
            return;
        }

        if (message.startsWith("MOVE")) {
            handleMoveCommand(message);
        } else if (message.startsWith("RESET_STATE")) {
            handleResetCommand();
        }
    }

    private void handleMoveCommand(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 4) {
            String player = parts[1];
            String piece = parts[2];
            int steps = Integer.parseInt(parts[3]);
            updateGameState(player, piece, steps);
            broadcastToRoom("UPDATE_STATE " + formatGameState());
        }
    }
    private void handleRoomCommand(String command) {
        if (command.startsWith("/create ")) {
            String[] params = command.substring(8).trim().split(" ");
            String roomName = params[0];
            int turnTime = params.length > 1 ? Integer.parseInt(params[1]) : 30;
            int maxPlayers = params.length > 2 ? Integer.parseInt(params[2]) : 4;
            roomManager.createRoom(roomName, turnTime, maxPlayers);
            out.println("Room '" + roomName + "' created.");

            //방 만들고 바로 들어가게
            Room room = roomManager.getRoom(roomName);
            if (room != null && room.addClient(this)) {
                currentRoom = room;
                out.println("Joined room '" + roomName + "'.");
            }

        } else if (command.startsWith("/join ")) {
            String roomName = command.substring(6).trim();
            if (currentRoom != null) {
                out.println("Already in a room. Please leave current room first.");
                return;
            }
            Room room = roomManager.getRoom(roomName);
            if (room != null && room.addClient(this)) {
                currentRoom = room;
                out.println("Joined room '" + roomName + "'.");
            } else {
                out.println("Room doesn't exist or is full.");
            }

        } else if (command.equals("/list")) {
            listRooms();

        } else if (command.equals("/leave")) {
            leaveRoom();
        }
    }

    private void listRooms() {
        List<Room> rooms = roomManager.listRooms();
        if (rooms.isEmpty()) {
            out.println("No rooms available.");
        } else {
            out.println("Available rooms:");
            for (Room room : rooms) {
                out.println("- " + room.getName() +
                        " (Players: " + room.getClients().size() + "/" + room.getNumberOfPiece() +
                        ", Turn Time: " + room.getTurnTime() + "s)");
            }
        }
    }

    private void leaveRoom() {
        if (currentRoom != null) {
            currentRoom.removeClient(this);
            out.println("Left room '" + currentRoom.getName() + "'.");
            if (currentRoom.isEmpty()) {
                roomManager.removeRoom(currentRoom.getName());
                out.println("Room '" + currentRoom.getName() + "' has been removed.");
            }
            currentRoom = null;
        } else {
            out.println("Not in any room.");
        }
    }

    private void handleResetCommand() {
        resetGameState();
        broadcastToRoom("RESET_STATE Game state has been reset.");
    }

    private void updateGameState(String player, String piece, int steps) {
        synchronized (gameState) {
            gameState.put(player + "-" + piece, steps);
        }
    }

    private void resetGameState() {
        synchronized (gameState) {
            gameState.clear();
        }
    }

    private String formatGameState() {
        StringBuilder sb = new StringBuilder();
        synchronized (gameState) {
            for (Map.Entry<String, Integer> entry : gameState.entrySet()) {
                sb.append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append(" | ");
            }
        }
        return sb.toString().trim();
    }

    private void broadcastToRoom(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    private void cleanup() {
        try {
            leaveRoom();
            synchronized (clientWriters) {
                clientWriters.remove(out);
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}