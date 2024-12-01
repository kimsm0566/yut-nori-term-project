package Yootgame.source.server;

import java.util.Vector;
import Yootgame.source.client.*;
import Yootgame.source.server.*;

public class GameRoom {
    private int roomId;
    private String roomName;
    private Vector<UserService> users = new Vector<>();
    private static final int MAX_USERS = 4;

    public GameRoom(int roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public boolean addUser(UserService user) {
        if (users.size() < MAX_USERS) {
            users.add(user);
            return true;
        }
        return false;
    }

    public void removeUser(UserService user) {
        users.remove(user);
    }

    public boolean isFull() {
        return users.size() >= MAX_USERS;
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }

    // Getter 메서드들
    public int getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public Vector<UserService> getUsers() { return users; }
    public int getUserCount(){
        return users.size();
    }
}