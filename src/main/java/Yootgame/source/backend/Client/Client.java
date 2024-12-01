package Yootgame.source.backend.Client;


import Yootgame.source.backend.gamelogic.YotBoard;
import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.ui.NicknameInputPage;
import Yootgame.source.backend.Client.*;

import javax.swing.*;
import java.io.IOException;

public class Client {
    private final ConnectionManager connectionManager;
    private final MessageHandler messageHandler;
    private final UIManager uiManager;
    private Room currentRoom;
    private String nickname;
    private boolean isHost;
    private boolean running = true;
    private YotBoard board;  // YotBoard 필드 추가

    public Client() {
        this.connectionManager = new ConnectionManager();
        this.uiManager = new UIManager(this);
        this.messageHandler = new MessageHandler(this, this.uiManager, board);  // UIManager 전달
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
    // YotBoard 설정 메소드 추가
    public void setBoard(YotBoard board) {
        this.board = board;
    }
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void start() {
        try {
            // 닉네임 입력 UI 실행
            NicknameInputPage nicknameInput = new NicknameInputPage();
            nicknameInput.setVisible(true);

            // 닉네임 입력 완료까지 대기
            while (!nicknameInput.isConfirmed()) {
                Thread.sleep(100);
            }

            // 연결 설정 및 초기화
            this.nickname = nicknameInput.getNickname();
            connectionManager.connect();
            connectionManager.sendMessage("/nickname " + nickname);

            // UI 초기화
            SwingUtilities.invokeLater(() -> {
                uiManager.switchToLobby();
            });

            // 메시지 수신 시작
            listenForUpdates();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listenForUpdates() {
        new Thread(() -> {
            try {
                String message;
                while ((message = connectionManager.readMessage()) != null && running) {
                    messageHandler.handleMessage(message);
                }
            } catch (IOException e) {
                if (running) {
                    System.out.println("Connection error: " + e.getMessage());
                }
            } finally {
                running = false;
                connectionManager.disconnect();
            }
        }).start();
    }

    // Getter/Setter methods
    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    public String getNickname() { return nickname; }
    public boolean isHost() { return isHost; }
    public void setHost(boolean host) { isHost = host; }
    public ConnectionManager getConnectionManager() { return connectionManager; }
    public UIManager getUIManager() { return uiManager; }

    public void sendMessage(String message) {
        connectionManager.sendMessage(message);
    }
    public boolean isRunning() {
        return running;
    }
    public void shutdown() {
        running = false;
        connectionManager.disconnect();
    }
}