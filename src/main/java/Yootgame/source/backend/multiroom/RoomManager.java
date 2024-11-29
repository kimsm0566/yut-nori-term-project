package Yootgame.source.backend.multiroom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public synchronized void createRoom(String roomName, int turnTime, int numberOfPiece) {
        rooms.putIfAbsent(roomName, new Room(roomName, turnTime, numberOfPiece));
    }

    public synchronized Room getRoom(String roomName) {
        return rooms.get(roomName);
    }

    public synchronized void removeRoom(String roomName) {
        // 방을 제거할 때는 단순히 rooms에서 제거
        rooms.remove(roomName);
    }

    public synchronized List<Room> listRooms() {
        return new ArrayList<>(rooms.values());
    }
}