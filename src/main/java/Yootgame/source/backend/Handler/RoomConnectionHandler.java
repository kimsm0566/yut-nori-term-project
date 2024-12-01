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
