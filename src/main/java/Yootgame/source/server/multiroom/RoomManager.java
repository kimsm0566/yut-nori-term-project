package Yootgame.source.server.multiroom;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//
public class RoomManager {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public synchronized void createRoom(String roomName, int turnTime, int maxPlayers) {
        rooms.putIfAbsent(roomName, new Room(roomName, turnTime, maxPlayers));
    }

    public synchronized Room getRoom(String roomName) {
        return rooms.get(roomName);
    }

    public synchronized void removeRoom(String roomName) {
        rooms.remove(roomName);
    }

    public synchronized List<Room> listRooms() {
        return new ArrayList<>(rooms.values()); // Room 객체들의 리스트 반환
    }
    }


