package Yootgame.source.server.gameserver;

import java.net.Socket;
import java.util.*;

public class GameRoom {
    private String name;
    private List<Socket> players; // 게임 방에 참가한 플레이어 소켓

    public GameRoom(String name) {
        this.name = name;
        this.players = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public synchronized void addPlayer(Socket player) {
        players.add(player);
        System.out.println("Player added to room: " + name);
    }

    public synchronized void removePlayer(Socket player) {
        players.remove(player);
        System.out.println("Player removed from room: " + name);
    }

    public synchronized int getPlayerCount() {
        return players.size();
    }
}
