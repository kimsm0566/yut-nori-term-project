package Yootgame.source.backend.Handler;

import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.backend.multiroom.RoomManager;

import java.io.*;
import java.net.*;

public class RoomConnectionHandler extends Thread {
    private final Socket socket;
    private final RoomManager roomManager;
    private Room currentRoom;
    protected PrintWriter out;
    protected String nickname;  // 닉네임 필드 추가

    public RoomConnectionHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    public String getNickname() {  // getNickname() 메소드 추가
        return nickname;
    }

    protected void processCommand(String command) {
        if (command.startsWith("/nickname ")) {
            // 닉네임 설정 처리
            this.nickname = command.substring(10).trim();
            out.println("Nickname set to: " + nickname);
        } else if (command.startsWith("/create ")) {
            String[] params = command.substring(8).trim().split(" ");
            String roomName = params[0];
            int turnTime = params.length > 1 ? Integer.parseInt(params[1]) : 30;
            int maxPlayers = params.length > 2 ? Integer.parseInt(params[2]) : 4;
            roomManager.createRoom(roomName, turnTime, maxPlayers);
            out.println("Room '" + roomName + "' created by " + nickname);
        } else if (command.startsWith("/join ")) {
            String roomName = command.substring(6).trim();
            if (currentRoom != null) {
                out.println("You are already in a room. Leave the current room first with /leave.");
                return;
            }
            Room room = roomManager.getRoom(roomName);
            if (room != null && room.addClient(this)) {
                currentRoom = room;
                out.println(nickname + " joined room '" + roomName + "'.");
            } else {
                out.println("Room does not exist or is full.");
            }
        } else if (command.startsWith("/hostInfo ") || command.startsWith("/guestInfo ")) {
            // 닉네임 정보 교환 메시지 처리
            if (currentRoom != null) {
                for (RoomConnectionHandler client : currentRoom.getClients()) {
                    if (client != this) {
                        client.sendMessage(command);
                    }
                }
            }
        }
    }

        protected void listRooms() {
        var roomList = roomManager.listRooms();
        if (roomList.isEmpty()) {
            out.println("Currently no rooms created.");
        } else {
            out.println("Currently created rooms:");
            for (Room room : roomList) {
                out.println("- " + room.getName() +
                        " (Turn Time: " + room.getTurnTime() + "s, Number of pieces: " + room.getNumberOfPiece() + ")");
            }
        }
    }


    protected void leaveRoom() {
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
