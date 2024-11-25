package Yootgame.source.server.gameserver;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345;
    private static Map<String, Object> gameState = new HashMap<>(); // 게임 상태 저장
    private static List<PrintWriter> clientWriters = new ArrayList<>(); // 연결된 클라이언트 출력 스트림

    public static void main(String[] args) {
        System.out.println("Game Server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    handleClientMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }

        private void handleClientMessage(String message) {
            // 클라이언트 메시지 처리
            if (message.startsWith("MOVE")) {
                // 예: MOVE player1 piece1 3 (말 이동 요청)
                String[] parts = message.split(" ");
                String player = parts[1];
                String piece = parts[2];
                int steps = Integer.parseInt(parts[3]);

                // 게임 상태 업데이트
                updateGameState(player, piece, steps);

                // 모든 클라이언트에 브로드캐스트
                broadcast("UPDATE_STATE " + gameState);
            } else {
                out.println("Unknown command: " + message);
            }
        }

        private void updateGameState(String player, String piece, int steps) {
            // 간단한 예제: 상태에 플레이어의 이동 업데이트
            gameState.put(player + "-" + piece, steps);
        }
    }

    private static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
