package Yootgame.source.server.clientserver;

import java.io.*;
import java.net.*;

public class GameClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private BufferedReader serverInput;
    private PrintWriter serverOutput;
    private Socket socket;

    public static void main(String[] args) {
        GameClient client = new GameClient();
        client.start();
    }

    public void start() {
        try {
            // 소켓을 생성하고 연결
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutput = new PrintWriter(socket.getOutputStream(), true);

            // 서버로 메시지 보내기 (예: 말 이동)
            sendMessage("MOVE player1 piece1 3");

            // 서버로부터 메시지 수신
            listenForUpdates();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        serverOutput.println(message);
    }

    private void listenForUpdates() {
        new Thread(() -> {
            try {
                String message;
                while ((message = serverInput.readLine()) != null) {
                    if (message.startsWith("UPDATE_STATE")) {
                        System.out.println("Game State Updated: " + message.substring(12));
                        // TODO: UI 또는 게임 상태 반영
                    } else {
                        System.out.println("Message from server: " + message);
                    }
                }
            } catch (IOException e) {
                // 예외가 발생하면 소켓이 닫혔거나 네트워크 오류가 발생한 경우이므로 이를 처리합니다.
                System.out.println("Connection lost or error reading data: " + e.getMessage());
            } finally {
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();  // 연결이 끝난 후 소켓을 닫습니다.
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
