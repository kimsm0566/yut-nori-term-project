package Yootgame.source.backend.Handler;

import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.backend.multiroom.RoomManager;

import java.io.*;
import java.net.*;
import java.util.*;

public class YutGameSessionHandler extends RoomConnectionHandler {
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Room currentRoom;
    private final Map<String, Integer> gameState;
    private final List<PrintWriter> clientWriters;
    private final RoomManager roomManager;
    private String nickname;

    public YutGameSessionHandler(Socket socket, RoomManager roomManager, Map<String, Integer> gameState, List<PrintWriter> clientWriters) {
        super(socket, roomManager);
        this.socket = socket;
        this.roomManager = roomManager;
        this.gameState = gameState;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        try {
            setupStreams();
            handleNicknameSetup();
            handleClientCommunication();
        } catch (IOException e) {
            if (!socket.isClosed()) {
                e.printStackTrace();
            }
        } finally {
            cleanup();
        }
    }


    private void setupStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        super.out = out;  // 부모 클래스의 out 필드도 초기화
        synchronized (clientWriters) {
            clientWriters.add(out);
        }
    }

    private void handleNicknameSetup() throws IOException {
        String message;
        while ((message = in.readLine()) != null) {
            if (message.startsWith("/nickname ")) {
                String requestedNickname = message.substring(10).trim();
                if (isValidNickname(requestedNickname)) {
                    this.nickname = requestedNickname;
                    out.println("Nickname set: " + nickname);
                    return;
                } else {
                    out.println("Invalid nickname. Please try another one.");
                }
            } else {
                // 닉네임 설정 중에는 다른 메시지 무시
                continue;
            }
        }
    }

    private boolean isValidNickname(String nickname) {
        return nickname != null &&
                !nickname.trim().isEmpty() &&
                nickname.length() >= 2 &&
                nickname.length() <= 12 &&
                nickname.matches("^[a-zA-Z0-9_가-힣]+$");
    }


    private void handleClientCommunication() throws IOException {
        String message;
        try {
            while ((message = in.readLine()) != null && !Thread.currentThread().isInterrupted()) {
                System.out.println("Received: " + message);
                if (message.startsWith("/")) {
                    if (message.startsWith("/hostInfo ") || message.startsWith("/guestInfo ")) {
                        // 닉네임 정보를 다른 클라이언트에게 전달
                        broadcastToRoom(message);
                    } else {
                        handleRoomCommand(message);
                    }
                } else {
                    handleGameCommand(message);
                }
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                throw e;
            }
        }
    }

    private void handleRoomCommand(String command) {
        if (command.startsWith("/ready ")) {
            broadcastToRoom(command);
            // 모든 플레이어가 준비되었는지만 확인
            if (currentRoom != null && currentRoom.areAllPlayersReady()) {
                // 게임 시작 준비가 완료되었음을 알림
                broadcastToRoom("/startCountdown");
            }
        } else if (command.equals("/startCountdown")) {
            // 방장만 카운트다운 메시지를 보내도록 수정
            if (currentRoom != null && currentRoom.getClients().iterator().next() == this) {
                broadcastToRoom(command);
            }
        } else if (command.startsWith("/countdown ")) {
            // 방장만 카운트다운 숫자를 보내도록 수정
            if (currentRoom != null && currentRoom.getClients().iterator().next() == this) {
                broadcastToRoom(command);
            }
        } else if (command.equals("/countdown_cancel")) {
            // 방장만 취소 메시지를 보내도록 수정
            if (currentRoom != null && currentRoom.getClients().iterator().next() == this) {
                broadcastToRoom(command);
            }
        } else if (command.equals("/startGame")) {
            broadcastToRoom(command);
        } else if (command.startsWith("/create ")) {
            createRoom(command);
        } else if (command.startsWith("/join ")) {
            joinRoom(command);
        } else if (command.equals("/list")) {
            listRooms();
        } else if (command.equals("/leave")) {
            leaveRoom();
        } else {
            showAvailableCommands();
        }
    }

    private void showAvailableCommands() {
        out.println("Unknown command. Available commands:");
        out.println("/create [roomName] [turnTime] [maxPlayers]");
        out.println("/join [roomName]");
        out.println("/list");
        out.println("/leave");
    }

    private void createRoom(String command) {
        String[] params = command.substring(8).trim().split(" ");
        if (params.length >= 3) {  // 최소 3개 파라미터 필요
            String roomName = params[0];
            int turnTime = Integer.parseInt(params[1]);  // 두 번째 파라미터가 turnTime
            int numberOfPiece = Integer.parseInt(params[2]);  // 세 번째 파라미터가 numberOfPiece

            System.out.println("Debug - Creating Room: " +
                    "Name=" + roomName +
                    ", TurnTime=" + turnTime +
                    ", NumberOfPiece=" + numberOfPiece);

            roomManager.createRoom(roomName, turnTime, numberOfPiece);
            Room room = roomManager.getRoom(roomName);
            if (room != null && room.addClient(this)) {
                currentRoom = room;
                out.println("/create " + roomName + " " + nickname + " " + turnTime + " " + numberOfPiece);
                broadcastRoomListUpdate();
            }
        }
    }

    private void broadcastRoomListUpdate() {
        List<Room> rooms = roomManager.listRooms();
        StringBuilder message = new StringBuilder("/room_list_update ");  // 접두어는 한 번만 추가

        // 각 방의 정보를 세미콜론으로 구분하여 추가
        for (Room room : rooms) {
            message.append(room.getName()).append(" ")
                    .append(room.getClients().size()).append(" ")
                    .append(room.getNumberOfPiece()).append(" ")
                    .append(room.getTurnTime())
                    .append(";");
        }

        // 모든 클라이언트에게 방 목록 전송
        String roomListMessage = message.toString();
        synchronized (clientWriters) {
            System.out.println("Sending room list update: " + roomListMessage);  // 디버그용
            for (PrintWriter writer : clientWriters) {
                writer.println(roomListMessage);
            }
        }
    }

    @Override
    protected void leaveRoom() {
        if (currentRoom != null) {
            // 방장이 나가는 경우
            if (currentRoom.getClients().iterator().next() == this) {
                // 게스트가 있다면 게스트도 나가도록 처리
                for (RoomConnectionHandler client : currentRoom.getClients()) {
                    if (client != this) {
                        client.out.println("/leaveRoom");
                    }
                }
                // 방장이 나갈 때는 방 삭제
                roomManager.removeRoom(currentRoom.getName());
                currentRoom.removeClient(this);
                out.println("/leaveRoom");
                currentRoom = null;
                broadcastRoomListUpdate();
            }
            // 게스트가 나가는 경우
            else {
                currentRoom.removeClient(this);
                out.println("/leaveRoom");
                broadcastToRoom("/leaveRoom " + nickname);  // 방장에게 게스트가 나갔다고 알림
                currentRoom = null;
                broadcastRoomListUpdate();
            }
        }
    }
    private void joinRoom(String command) {
        String roomName = command.substring(6).trim();
        if (currentRoom != null) {
            out.println("Already in a room");
            return;
        }

        Room room = roomManager.getRoom(roomName);
        if (room != null && room.addClient(this)) {
            currentRoom = room;
            // 방 참가 메시지 전송
            out.println("/join " + roomName + " " + nickname + " " + room.getTurnTime() + " " + room.getNumberOfPiece());
            broadcastToRoom("/join " + roomName + " " + nickname + " " + room.getTurnTime() + " " + room.getNumberOfPiece());

            // 방장 정보를 새로 참가한 게스트에게 전송
            for (RoomConnectionHandler client : room.getClients()) {
                if (client != this) {  // 방장을 찾아서
                    out.println("/hostInfo " + client.getNickname());  // 게스트에게 방장 정보 전송
                    break;
                }
            }

            broadcastRoomListUpdate();
        } else {
            out.println("Room doesn't exist or is full.");
        }
    }
    private void broadcastToRoom(String message) {
        if (currentRoom != null) {
            // 카운트다운 관련 메시지는 방장만 전송하도록 수정
            if (message.startsWith("/countdown") || message.equals("/startCountdown")) {
                // 방장인 경우에만 메시지 전송
                if (currentRoom.getClients().iterator().next() == this) {
                    for (RoomConnectionHandler client : currentRoom.getClients()) {
                        client.sendMessage(message);
                    }
                }
            } else {
                // 다른 메시지는 기존대로 처리
                for (RoomConnectionHandler client : currentRoom.getClients()) {
                    client.sendMessage(message);
                }
            }
        }
    }

    @Override
    protected void listRooms() {
        // 방 목록 업데이트 메시지 전송
        List<Room> rooms = roomManager.listRooms();
        String roomListMessage = formatRoomList(rooms);
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(roomListMessage);  // "ROOM_LIST_UPDATE" 제거
                System.out.println("Debug - Sending room list update: " + roomListMessage);
            }
        }
    }

    private String formatRoomList(List<Room> rooms) {
        StringBuilder sb = new StringBuilder();
        for (Room room : rooms) {
            System.out.println("Debug - Room: " + room.getName() + ", Clients: " + room.getClients().size());
            sb.append("/room_list_update ")  // 각 방 정보마다 prefix 추가
                    .append(room.getName()).append(" ")
                    .append(room.getClients().size()).append(" ")
                    .append(room.getNumberOfPiece()).append(" ")
                    .append(room.getTurnTime())
                    .append(";");
        }
        return sb.toString();
    }


    private void handleGameCommand(String message) {
        if (currentRoom == null) {
            out.println("Please join a room first.");
            return;
        }
        if (message.startsWith("MOVE")) {
            handleMoveCommand(message);
        } else if (message.startsWith("RESET_STATE")) {
            handleResetCommand();
        }
    }

    private void handleMoveCommand(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 4) {
            String player = parts[1];
            String piece = parts[2];
            int steps = Integer.parseInt(parts[3]);
            updateGameState(player, piece, steps);
            broadcastToRoom("UPDATE_STATE " + formatGameState());
        }
    }

    public String getNickname() {
        return nickname;
    }

    private void handleResetCommand() {
        resetGameState();
        broadcastToRoom("RESET_STATE Game state has been reset.");
    }

    private void updateGameState(String player, String piece, int steps) {
        synchronized (gameState) {
            gameState.put(player + "-" + piece, steps);
        }
    }

    private void resetGameState() {
        synchronized (gameState) {
            gameState.clear();
        }
    }

    private String formatGameState() {
        StringBuilder sb = new StringBuilder();
        synchronized (gameState) {
            for (Map.Entry<String, Integer> entry : gameState.entrySet()) {
                sb.append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append(" | ");
            }
        }
        return sb.toString().trim();
    }

    private void cleanup() {
        try {
            if (out != null) {  // out이 null이 아닐 때만 leaveRoom과 clientWriters 처리
                if (!socket.isClosed()) {
                    leaveRoom();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
            // 스트림과 소켓 정리
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    @Override
    protected void processCommand(String command) {
        if (command.startsWith("/")) {
            handleRoomCommand(command);
        } else {
            handleGameCommand(command);
        }
    }


}