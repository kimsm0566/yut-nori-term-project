package Yootgame.source.backend.multiroom;

import Yootgame.source.backend.Handler.RoomConnectionHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//
public class Room {
    private final String name;
    private final Set<RoomConnectionHandler> clients;
    private int turnTime;
    private int numberOfPiece;
    private final int maxPlayers = 2;
    private int clientCount;
    private String hostNickname;  // 방장 닉네임 추가
    private String guestNickname; // 게스트 닉네임 추가
    private boolean hostReady = false;
    private boolean guestReady = false;

    public Room(String name, int turnTime, int numberOfPiece) {
        this.name = name;
        this.turnTime = turnTime;
        this.numberOfPiece = numberOfPiece;
        this.clients = ConcurrentHashMap.newKeySet();
        this.hostNickname = null;
        this.guestNickname = null;
    }
    // 플레이어의 준비 상태 설정
    public void setPlayerReady(boolean isHost, boolean ready) {
        if (isHost) {
            hostReady = ready;
        } else {
            guestReady = ready;
        }
    }

    // 모든 플레이어가 준비되었는지 확인
    public boolean areAllPlayersReady() {
        return hostReady && guestReady && clients.size() == 2;
    }

    // 준비 상태 초기화
    public void resetReadyState() {
        hostReady = false;
        guestReady = false;
    }

    // 닉네임 관련 메소드 추가
    public void setHostNickname(String nickname) {
        this.hostNickname = nickname;
    }

    public void setGuestNickname(String nickname) {
        this.guestNickname = nickname;
    }

    public String getHostNickname() {
        return hostNickname;
    }

    public String getGuestNickname() {
        return guestNickname;
    }

    public void setClientCount(int count) {
        this.clientCount = count;
    }
    public int getClientCount() {
        return clientCount;
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
