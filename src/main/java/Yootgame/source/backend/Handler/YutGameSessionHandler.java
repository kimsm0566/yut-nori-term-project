package Yootgame.source.backend.Handler;


import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.backend.multiroom.RoomManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class YutGameSessionHandler extends RoomConnectionHandler {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Room currentRoom;
    private Map<String, Integer> gameState;
    private List<PrintWriter> clientWriters;
    private RoomManager roomManager;  // RoomManager 필드 추가
    private String nickname;

    public YutGameSessionHandler(Socket socket, RoomManager roomManager,
                                 Map<String, Integer> gameState, List<PrintWriter> clientWriters) {
        super(socket, roomManager);
        this.socket = socket;
        this.gameState = gameState;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        try {
            setupStreams();
            // 닉네임 설정을 기다림
            if (!waitForNickname()) {
                return; // 닉네임 설정 실패 시 연결 종료
            }
            handleClientCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    private boolean waitForNickname() {
        try {
            while (true) {
                String message = in.readLine();
                if (message == null) {
                    return false;
                }
                if (message.startsWith("/nickname ")) {
                    String requestedNickname = message.substring(10).trim();
                    if (isValidNickname(requestedNickname)) {
                        this.nickname = requestedNickname;
                        out.println("Nickname set: " + nickname);
                        System.out.println("Client nickname set: " + nickname);
                        return true;
                    } else {
                        out.println("Invalid nickname. Please try another one.");
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
    private boolean isValidNickname(String nickname) {
        // 닉네임 유효성 검사
        // 1. 비어있지 않은지
        // 2. 적절한 길이인지 (예: 2-12자)
        // 3. 허용된 문자만 포함하는지
        // 4. 이미 사용 중인 닉네임이 아닌지
        if (nickname == null || nickname.trim().isEmpty()) {
            return false;
        }
        if (nickname.length() < 2 || nickname.length() > 12) {
            return false;
        }
        // 알파벳, 숫자, 언더스코어만 허용
        if (!nickname.matches("^[a-zA-Z0-9_]+$")) {
            return false;
        }
        // 다른 클라이언트와 중복되지 않는지 확인
        // (이 부분은 서버에서 닉네임 목록을 관리하는 방식에 따라 구현)
        return true;
    }


    private void setupStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        synchronized (clientWriters) {
            clientWriters.add(out);
        }
        sendWelcomeMessage();
    }

    private void sendWelcomeMessage() {
        out.println("Connected to server. Available commands:");
        out.println("/create [roomName] [turnTime] [maxPlayers] - Create a new room");
        out.println("/join [roomName] - Join a room");
        out.println("/list - List all rooms");
        out.println("/leave - Leave current room");
        out.println("MOVE [player] [piece] [steps] - Move a game piece");
        out.println("RESET_STATE - Reset the game state");
    }

    private void handleClientCommunication() throws IOException {
        String message;
        while ((message = in.readLine()) != null) {
            System.out.println("Received: " + message);

            if (message.startsWith("/")) {
                handleRoomCommand(message);
            } else {
                handleGameCommand(message);
            }
        }
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

    private void handleRoomCommand(String command) {
        if (command.startsWith("/create ")) {
            String[] params = command.substring(8).trim().split(" ");
            String roomName = params[0];
            int turnTime = params.length > 1 ? Integer.parseInt(params[1]) : 30;
            int maxPlayers = params.length > 2 ? Integer.parseInt(params[2]) : 4;
            roomManager.createRoom(roomName, turnTime, maxPlayers);
            out.println("Room '" + roomName + "' created by " + nickname);

            Room room = roomManager.getRoom(roomName);
            if (room != null && room.addClient(this)) {
                currentRoom = room;
                out.println(nickname + " joined room '" + roomName + "'.");
            }

        } else if (command.startsWith("/join ")) {
            String roomName = command.substring(6).trim();
            if (currentRoom != null) {
                out.println(nickname + " is already in a room. Please leave current room first.");
                return;
            }
            Room room = roomManager.getRoom(roomName);
            if (room != null && room.addClient(this)) {
                currentRoom = room;
                out.println(nickname + " joined room '" + roomName + "'.");
                // 방의 다른 사용자들에게 새로운 참가자 알림
                broadcastToRoom(nickname + " has joined the room.");
            } else {
                out.println("Room doesn't exist or is full.");
            }

        } else if (command.equals("/list")) {
            listRooms();

        } else if (command.equals("/leave")) {
            if (currentRoom != null) {
                String roomName = currentRoom.getName();
                currentRoom.removeClient(this);
                // 방의 다른 사용자들에게 퇴장 알림
                broadcastToRoom(nickname + " has left the room.");
                out.println(nickname + " left room '" + roomName + "'.");

                if (currentRoom.isEmpty()) {
                    roomManager.removeRoom(roomName);
                    out.println("Room '" + roomName + "' has been removed.");
                }
                currentRoom = null;
            } else {
                out.println(nickname + " is not in any room.");
            }
        } else {
            out.println("Unknown command. Available commands:");
            out.println("/create [roomName] [turnTime] [maxPlayers]");
            out.println("/join [roomName]");
            out.println("/list");
            out.println("/leave");
        }
    }

    private void listRooms() {
        List<Room> rooms = roomManager.listRooms();
        if (rooms.isEmpty()) {
            out.println("No rooms available.");
        } else {
            out.println("Available rooms:");
            for (Room room : rooms) {
                out.println("- " + room.getName() +
                        " (Piecese: " + room.getClients().size() + "/" + room.getNumberOfPiece() +
                        ", Turn Time: " + room.getTurnTime() + "s)");
            }
        }
    }

    private void broadcastToRoom(String message) {
        if (currentRoom != null) {
            for (RoomConnectionHandler client : currentRoom.getClients()) {
                if (client != this) {  // 자신을 제외한 방의 다른 클라이언트들에게 메시지 전송
                    client.sendMessage(message);
                }
            }
        }
    }

    private void leaveRoom() {
        if (currentRoom != null) {
            currentRoom.removeClient(this);
            out.println("Left room '" + currentRoom.getName() + "'.");
            if (currentRoom.isEmpty()) {
                roomManager.removeRoom(currentRoom.getName());
                out.println("Room '" + currentRoom.getName() + "' has been removed.");
            }
            currentRoom = null;
        } else {
            out.println("Not in any room.");
        }
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
            leaveRoom();
            synchronized (clientWriters) {
                clientWriters.remove(out);
            }
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
}