package Yootgame.source.server.GameServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345; // 서버가 사용할 포트 번호

    // 게임 상태를 저장하는 Map 객체 (Key: "player-piece", Value: 위치/상태)
    private static Map<String, Integer> gameState = new HashMap<>();

    // 연결된 클라이언트의 출력 스트림을 저장하는 리스트
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Game Server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // 서버 소켓 생성
            while (true) {
                // 클라이언트의 연결을 기다림 (blocking call)
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // 클라이언트 처리를 위한 새로운 스레드를 생성하고 실행
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            // 서버 실행 중 발생한 예외를 처리
            e.printStackTrace();
        }
    }

    // 클라이언트 요청 처리를 담당하는 내부 클래스
    private static class ClientHandler implements Runnable {
        private Socket socket; // 클라이언트 소켓
        private PrintWriter out; // 클라이언트에게 메시지를 보낼 출력 스트림
        private BufferedReader in; // 클라이언트로부터 메시지를 받을 입력 스트림

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // 클라이언트와 데이터 송수신을 위한 스트림 초기화
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // 클라이언트의 출력 스트림을 리스트에 추가 (브로드캐스트에 사용)
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                // 클라이언트로부터 메시지를 읽고 처리
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    handleClientMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close(); // 클라이언트 연결 종료
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out); // 클라이언트가 연결 종료 시 리스트에서 제거
                }
            }
        }

        // 클라이언트 메시지를 처리하는 메소드
        private void handleClientMessage(String message) {
            if (message.startsWith("MOVE")) {
                // MOVE 명령어 처리 (형식: MOVE player piece steps)
                String[] parts = message.split(" ");
                String player = parts[1]; // 플레이어 ID
                String piece = parts[2];  // 이동할 말
                int steps = Integer.parseInt(parts[3]); // 이동 거리

                // 게임 상태 업데이트
                updateGameState(player, piece, steps);

                // 업데이트된 게임 상태를 모든 클라이언트에 브로드캐스트
                String formattedState = formatGameState(); // 게임 상태를 사람이 읽을 수 있는 형태로 변환
                broadcast("UPDATE_STATE " + formattedState);

                // 서버 콘솔에 업데이트된 게임 상태 출력
                System.out.println("Game State Updated: " + formattedState);

            } else if (message.startsWith("RESET_STATE")) {
                // 게임 상태 초기화 명령어 처리
                resetGameState();
                broadcast("RESET_STATE Game state has been reset."); // 클라이언트에 초기화 알림
                System.out.println("Game State has been reset.");
            } else {
                // 알 수 없는 명령 처리
                out.println("Unknown command: " + message);
            }
        }

        // 게임 상태를 업데이트하는 메소드
        private void updateGameState(String player, String piece, int steps) {
            String key = player + "-" + piece; // 플레이어와 말을 키로 사용
            gameState.put(key, steps); // 새로운 위치를 저장
        }
    }

    // 모든 연결된 클라이언트에게 메시지를 전송하는 메소드
    private static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message); // 메시지를 각 클라이언트에 전송
            }
        }
    }

    // 게임 상태를 사람이 읽을 수 있는 형태로 변환하는 메소드
    private static String formatGameState() {
        StringBuilder stateBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : gameState.entrySet()) {
            stateBuilder.append(entry.getKey()) // "player1-piece1"
                    .append(": ")
                    .append(entry.getValue()) // 위치
                    .append(" | ");
        }
        return stateBuilder.toString().trim(); // 마지막 " | " 제거
    }

    // 게임 상태를 초기화하는 메소드
    private static void resetGameState() {
        synchronized (gameState) {
            gameState.clear(); // 게임 상태를 초기화
        }
    }
}
