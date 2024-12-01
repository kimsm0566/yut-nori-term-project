package Yootgame.source.backend.Client;

import Yootgame.source.backend.gamelogic.YotBoard;
import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.ui.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MessageHandler {
    private final Client client;
    private final UIManager uiManager;
    private YotBoard board;
    private GamePage gamePage;  // GamePage 인스턴스 추가

    public MessageHandler(Client client, UIManager uiManager, YotBoard board) {
        this.client = client;
        this.uiManager = uiManager;
        this.board = board;

    }
    public void setBoard(YotBoard board) {
        this.board = board;
    }

    public void handleMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message.startsWith("/room_list_update")) {
                handleRoomListUpdate(message);
            } else if (message.startsWith("/create")) {
                // /create [roomName] [nickname] [turnTime] [numberOfPiece]
                handleRoomCreation(message);
            } else if (message.startsWith("/join")) {
                // /join [roomName] [nickname]
                handleRoomJoin(message);
            } else if (message.startsWith("/hostInfo")) {
                handleHostInfo(message);
            } else if (message.startsWith("/guestInfo")) {
                handleGuestInfo(message);
            } else if (message.startsWith("/ready")) {
                handleReadyState(message);
            } else if (message.equals("/startCountdown")) {
                handleStartCountdown();
            } else if (message.startsWith("/countdown")) {
                handleCountdown(message);
            } else if (message.equals("/countdown_cancel")) {
                handleCountdownCancel();
            } else if (message.equals("/startGame")) {
                handleGameStart();
            } else if (message.startsWith("/leaveRoom")) {
                String[] parts = message.split(" ");
                if (parts.length > 1) {
                    // 다른 사람이 나간 경우
                    String leftNickname = parts[1];
                    if (!leftNickname.equals(client.getNickname())) {
                        // 상대방이 나갔다는 처리
                        if (uiManager.getCurrentFrame() instanceof RoomPage) {
                            RoomPage roomPage = (RoomPage)uiManager.getCurrentFrame();
                            roomPage.addLogMessage(leftNickname + "님이 방을 나갔습니다.");
                            roomPage.updatePlayerLeft();
                        }
                    } else {
                        // 자신이 나간 경우
                        handleLeaveRoom();  // 로비로 전환
                    }
                } else {
                    // 단순 /leaveRoom 메시지인 경우
                    handleLeaveRoom();  // 로비로 전환
                }
            } else if (message.startsWith("/change_turn")) {
                String[] parts = message.split(" ");
                int newTurn = Integer.parseInt(parts[1]);
                String nextPlayerNickname = parts[2];
                boolean isHost = (newTurn == 0);
                board.changePlayer(nextPlayerNickname, isHost);
                board.refreashFrame();
            } else if (message.startsWith("/game_win")) {
                int winner = Integer.parseInt(message.split(" ")[1]);
                board.finishMessage(winner);
            } if (message.startsWith("/yut_result")) {
                String[] parts = message.split(" ");
                int playerTurn = Integer.parseInt(parts[1]);
                int result = Integer.parseInt(parts[2]);
                String nickname = parts[3];

                SwingUtilities.invokeLater(() -> {
                    board.printResult(result);
                    board.changePlayer(nickname, playerTurn == 0);
                    board.message(nickname + "님이 윷을 던졌습니다: " + result);
                });
            } else if (message.startsWith("/move_piece")) {
                String[] parts = message.split(" ");
                int playerTurn = Integer.parseInt(parts[1]);
                int x = Integer.parseInt(parts[2]);
                int y = Integer.parseInt(parts[3]);
                int point = Integer.parseInt(parts[4]);
                board.printPiece(playerTurn, x, y, point);
                if (parts.length > 5 && parts[5].equals("up")) {
                    board.message("P " + playerTurn + " 말 하나가 업혔습니다");
                }
                SwingUtilities.invokeLater(() -> {
                    board.printPiece(playerTurn, x, y, point);
                    gamePage.repaint();  // GamePage 갱신
                    System.out.println("/move_piece");
                });
            }
            else if (message.startsWith("/piece_goal")) {
                int playerTurn = Integer.parseInt(message.split(" ")[1]);
                board.message("P " + playerTurn + " 말 하나가 골인했습니다");
            }
            else if (message.startsWith("/player_info")) {
                String[] parts = message.split(" ");
                int playerIndex = Integer.parseInt(parts[1]);
                String playerInfo = parts[2];
                board.setplayerInfo(playerIndex, playerInfo);
            }
        });
    }




    private void handleRoomCreation(String message) {
        try {
            // "/create roomName nickname turnTime numberOfPiece" 형식
            String[] parts = message.split(" ");
            if (parts.length >= 5) {  // 최소 5개의 파라미터가 필요
                String roomName = parts[1];
                // nickname은 parts[2]
                int turnTime = Integer.parseInt(parts[3]);
                int numberOfPiece = Integer.parseInt(parts[4]);

                Room room = new Room(roomName, turnTime, numberOfPiece);
                room.setClientCount(1);
                client.setCurrentRoom(room);
                client.setHost(true);
                uiManager.switchToRoomPage(true);
            }
        } catch (Exception e) {
            System.out.println("Error creating room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGameStart() {
        if (uiManager.getCurrentFrame() instanceof RoomPage) {
            RoomPage roomPage = (RoomPage)uiManager.getCurrentFrame();
            Room currentRoom = client.getCurrentRoom();  // client에서 Room 정보 가져오기
            roomPage.addLogMessage("게임을 시작하겠습니다!");
            GamePage gamePage = uiManager.switchToGame(currentRoom);
            gamePage.updateCurrentPlayer(client.getNickname(), client.isHost());
        }
    }

    private void handleRoomJoin(String message) {
        try {
            // "/join roomName nickname turnTime numberOfPiece" 형식
            String[] parts = message.split(" ");
            if (parts.length >= 4) {
                String roomName = parts[1];
                String joinedNickname = parts[2];
                int turnTime = Integer.parseInt(parts[3]);
                int numberOfPiece = Integer.parseInt(parts[4]);

                // 자신이 참가한 경우에만 화면 전환
                if (joinedNickname.equals(client.getNickname())) {
                    Room room = new Room(roomName, turnTime, numberOfPiece);
                    room.setClientCount(2);
                    client.setCurrentRoom(room);
                    client.setHost(false);
                    uiManager.switchToRoomPage(false);
                    client.getConnectionManager().sendMessage("/guestInfo " + client.getNickname());
                }
            }
        } catch (Exception e) {
            System.out.println("Error joining room: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void handleRoomListUpdate(String message) {
        String roomListStr = message.substring("/room_list_update ".length());
        List<Room> rooms = parseRoomList(roomListStr);
        if (uiManager.getCurrentFrame() instanceof robbyPage) {
            ((robbyPage) uiManager.getCurrentFrame()).updateRoomList(rooms);
        }
    }

    private List<Room> parseRoomList(String roomListStr) {
        List<Room> rooms = new ArrayList<>();
        String[] roomStrings = roomListStr.split(";");
        for (String roomStr : roomStrings) {
            try {
                if (!roomStr.trim().isEmpty()) {
                    String[] parts = roomStr.trim().split(" ");
                    if (parts.length >= 4) {
                        String name = parts[0];
                        // 숫자 변환 전에 유효성 검사
                        if (parts[1].matches("\\d+") && parts[2].matches("\\d+") && parts[3].matches("\\d+")) {
                            int clientCount = Integer.parseInt(parts[1]);
                            int numPieces = Integer.parseInt(parts[2]);
                            int turnTime = Integer.parseInt(parts[3]);
                            Room room = new Room(name, turnTime, numPieces);
                            room.setClientCount(clientCount);
                            rooms.add(room);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error parsing room: " + roomStr + " - " + e.getMessage());
            }
        }
        return rooms;
    }
    private void handleHostInfo(String message) {
        if (!client.isHost() && uiManager.getCurrentFrame() instanceof RoomPage) {
            String hostNickname = message.substring(10);
            ((RoomPage)uiManager.getCurrentFrame()).updateHostName(hostNickname);
        }
    }

    private void handleGuestInfo(String message) {
        if (client.isHost() && uiManager.getCurrentFrame() instanceof RoomPage) {
            String guestNickname = message.substring(11);
            ((RoomPage)uiManager.getCurrentFrame()).updateGuestName(guestNickname);
        }
    }

    private void handleReadyState(String message) {
        try {
            String[] parts = message.split(" ");
            String playerNickname = parts[1];
            boolean isReady = Boolean.parseBoolean(parts[2]);

            if (uiManager.getCurrentFrame() instanceof RoomPage) {
                RoomPage roomPage = (RoomPage) uiManager.getCurrentFrame();
                if (!playerNickname.equals(client.getNickname())) {
                    roomPage.updateReadyState(client.isHost(), isReady);
                    // 단순히 모든 플레이어의 준비 상태만 확인
                    if (client.getCurrentRoom().areAllPlayersReady()) {
                        roomPage.startGameCountdown();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error handling ready state: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleStartCountdown() {
        if (uiManager.getCurrentFrame() instanceof RoomPage) {
            ((RoomPage)uiManager.getCurrentFrame()).startGameCountdown();
        }
    }
    private void handleCountdown(String message) {
        if (uiManager.getCurrentFrame() instanceof RoomPage) {
            try {
                String[] parts = message.split(" ");
                if (parts.length > 1 && uiManager.getCurrentFrame() instanceof RoomPage) {
                    int count = Integer.parseInt(parts[1]);
                    ((RoomPage)uiManager.getCurrentFrame()).addLogMessage(count + "초 후에 게임이 시작됩니다.");
                }
            } catch (Exception e) {
                System.out.println("Error handling countdown: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleCountdownCancel() {
        if (uiManager.getCurrentFrame() instanceof RoomPage) {
            ((RoomPage)uiManager.getCurrentFrame()).addLogMessage("준비 상태가 해제되어 게임 시작이 취소되었습니다.");
        }
    }



    private void handleLeaveRoom() {
        // 방을 나갈 때 클라이언트의 상태 초기화
        client.setCurrentRoom(null);
        client.setHost(false);  // 호스트 상태 초기화
        uiManager.switchToLobby();
    }
}