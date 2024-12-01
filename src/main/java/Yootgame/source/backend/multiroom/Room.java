package Yootgame.source.backend.multiroom;

import Yootgame.source.backend.Handler.RoomConnectionHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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
    private Socket hostSocket;
    private Socket guestSocket;
    private PrintWriter hostWriter;
    private PrintWriter guestWriter;

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

    // 소켓 설정 메소드 추가
    public void setHostSocket(Socket socket) throws IOException {
        this.hostSocket = socket;
        this.hostWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public void setGuestSocket(Socket socket) throws IOException {
        this.guestSocket = socket;
        this.guestWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    // 메시지 전송 최적화
    private final StringBuilder messageBuilder = new StringBuilder();
    public void broadcast(String message) {
        try {
            messageBuilder.setLength(0);
            messageBuilder.append(message).append("\n");
            String formattedMessage = messageBuilder.toString();

            if (hostWriter != null) hostWriter.print(formattedMessage);
            if (guestWriter != null) guestWriter.print(formattedMessage);
            if (hostWriter != null) hostWriter.flush();
            if (guestWriter != null) guestWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 게임 상태 업데이트 메소드 추가
    public void updateGameState(String state) {
        broadcast("/game_state " + state);
    }

    // 턴 변경 메소드 추가
    public void changeTurn(int turn) {
        String currentPlayer = (turn == 0) ? hostNickname : guestNickname;
        broadcast("/change_turn " + turn + " " + currentPlayer);
    }

    // 윷 던지기 결과 전송 메소드 추가
    public void sendYutResult(int turn, int result) {
        String player = (turn == 0) ? hostNickname : guestNickname;
        broadcast("/yut_result " + turn + " " + result + " " + player);
    }

    // 말 이동 전송 메소드 추가
    public void sendPieceMove(int turn, int x, int y, int point) {
        broadcast("/move_piece " + turn + " " + x + " " + y + " " + point);
    }

    // 게임 종료 전송 메소드 추가
    public void sendGameEnd(int winner) {
        String winnerNick = (winner == 0) ? hostNickname : guestNickname;
        broadcast("/game_end " + winner + " " + winnerNick);
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
