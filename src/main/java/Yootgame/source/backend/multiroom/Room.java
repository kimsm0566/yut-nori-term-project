package Yootgame.source.backend.multiroom;

import Yootgame.source.backend.Handler.RoomConnectionHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//
public class Room {
    private final String name; // 방 이름
    private final Set<RoomConnectionHandler> clients; // 방 참여 클라이언트 목록
    private int turnTime; // 턴 시간 (초 단위)
    private int numberOfPiece; // 윷놀이 말 개수
    private int maxPlayers = 2; // 최대 플레이어 수

    public Room(String name, int turnTime, int maxPlayers) {
        this.name = name;
        this.turnTime = turnTime;
        this.numberOfPiece = maxPlayers;
        this.clients = ConcurrentHashMap.newKeySet(); // 스레드 안전한 클라이언트 집합
    }

    public String getName() {
        return name;
    }

    public int getTurnTime() {
        return turnTime;
    }

    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    public int getNumberOfPiece() {
        return numberOfPiece;
    }

    public void setNumberOfPiece(int numberOfPiece) {
        this.numberOfPiece = numberOfPiece;
    }

    public synchronized boolean addClient(RoomConnectionHandler client) {
        if (clients.size() >= maxPlayers) {
            return false; // 방이 가득 찬 경우
        }
        return clients.add(client);
    }

    public synchronized void removeClient(RoomConnectionHandler client) {
        clients.remove(client);
    }

    public Set<RoomConnectionHandler> getClients() {
        return clients;
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }
}
