package Yootgame.source.server.multiroom;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final RoomManager roomManager;
    private Room currentRoom; // 현재 참여 중인 방
    private PrintWriter out;

    public ClientHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.out = out;
            out.println("The connection to the server was successful.");

            String command;
            while ((command = in.readLine()) != null) {
                processCommand(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            leaveRoom();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processCommand(String command) {
        if (command.startsWith("/create ")) {
            String[] params = command.substring(8).trim().split(" ");
            String roomName = params[0];
            int turnTime = params.length > 1 ? Integer.parseInt(params[1]) : 30;
            int maxPlayers = params.length > 2 ? Integer.parseInt(params[2]) : 4;
            roomManager.createRoom(roomName, turnTime, maxPlayers);
            out.println("Room '" + roomName + "' created.");
        } else if (command.startsWith("/join ")) {
            String roomName = command.substring(6).trim();
            if (currentRoom != null) {
                out.println("You are already in a room. Leave the current room first with /leave.");
                return;
            }
            Room room = roomManager.getRoom(roomName);
            if (room != null && room.addClient(this)) {
                currentRoom = room;
                out.println("Joined room '" + roomName + "'.");
            } else {
                out.println("Room does not exist or is full.");
            }
        } else if (command.equals("/list")) {
            listRooms();
        } else if (command.equals("/leave")) {
            leaveRoom();
        } else if (command.equals("/quit")) {
            out.println("Terminate the connection to the server.");
            leaveRoom();
            interrupt();
        } else {
            out.println("Unknown command.");
        }
    }

    private void listRooms() {
        var roomList = roomManager.listRooms();
        if (roomList.isEmpty()) {
            out.println("Currently no rooms created.");
        } else {
            out.println("Currently created rooms:");
            for (Room room : roomList) {
                out.println("- " + room.getName() +
                        " (Turn Time: " + room.getTurnTime() + "s, Max Players: " + room.getMaxPlayers() + ")");
            }
        }
    }


    private void leaveRoom() {
        if (currentRoom != null) {
            currentRoom.removeClient(this);
            out.println("Left room '" + currentRoom.getName() + "'.");
            if (currentRoom.isEmpty()) {
                roomManager.removeRoom(currentRoom.getName());
            }
            currentRoom = null;
        } else {
            out.println("You are not in any room.");
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
