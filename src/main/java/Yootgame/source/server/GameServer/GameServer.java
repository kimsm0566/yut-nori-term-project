package Yootgame.source.server.GameServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    // 서버가 사용할 포트 번호
    private static final int PORT = 12345;

    // 게임 상태를 저장할 Map 객체 (플레이어-말의 위치 등)
    private static Map<String, Object> gameState = new HashMap<>();

    // 연결된 클라이언트들의 출력 스트림을 저장할 리스트
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        // 서버가 시작되었음을 출력
        System.out.println("Game Server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {  // 서버 소켓 생성 (포트 12345)
            while (true) {
                // 클라이언트의 연결을 기다림
                Socket clientSocket = serverSocket.accept();
                // 클라이언트 연결 확인 메시지 출력
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                // 클라이언트와의 연결을 처리할 새 스레드 생성 후 실행
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            // 서버 소켓을 여는 동안 오류가 발생하면 출력
            e.printStackTrace();
        }
    }

    // 클라이언트의 연결을 처리하는 클래스 (Runnable을 구현하여 스레드로 실행)
    private static class ClientHandler implements Runnable {
        private Socket socket;  // 클라이언트와의 연결을 위한 소켓
        private PrintWriter out;  // 클라이언트에게 메시지를 보낼 출력 스트림
        private BufferedReader in;  // 클라이언트로부터 메시지를 받을 입력 스트림

        // 생성자: 소켓을 인자로 받아서 초기
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // 클라이언트 소켓에서 입력 스트림과 출력 스트림을 얻음
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // 클라이언트 출력 스트림을 리스트에 추가 (브로드캐스트를 위해)
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                // 클라이언트로부터 메시지를 읽음
                while ((message = in.readLine()) != null) {
                    // 받은 메시지를 출력
                    System.out.println("Received: " + message);
                    // 클라이언트 메시지를 처리
                    handleClientMessage(message);
                }
            } catch (IOException e) {
                // 예외가 발생하면 스택 트레이스를 출력
                e.printStackTrace();
            } finally {
                try {
                    // 클라이언트와의 연결을 종료
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 클라이언트 연결이 종료되면 출력 스트림을 리스트에서 제거
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }

        // 클라이언트로부터 받은 메시지를 처리하는 메소드
        private void handleClientMessage(String message) {
            // 클라이언트가 "MOVE" 명령을 보낸 경우
            if (message.startsWith("MOVE")) {
                // "MOVE player1 piece1 3" 형태로 메시지를 파싱
                String[] parts = message.split(" ");
                String player = parts[1];  // 플레이어 이름
                String piece = parts[2];   // 말 이름
                int steps = Integer.parseInt(parts[3]);  // 이동할 칸 수

                // 게임 상태 업데이트
                updateGameState(player, piece, steps);

                // 모든 클라이언트에 게임 상태 업데이트 메시지 브로드캐스트
                broadcast("UPDATE_STATE " + gameState);
            } else {
                // 알 수 없는 명령을 받으면 클라이언트에게 메시지를 전송
                out.println("Unknown command: " + message);
            }
        }

        // 게임 상태를 업데이트하는 메소드
        private void updateGameState(String player, String piece, int steps) {
            // 예시: 플레이어의 말을 이동시키는 방식으로 게임 상태를 업데이트
            gameState.put(player + "-" + piece, steps);  // "player1-piece1"을 키로 하고, 이동 칸 수를 값으로 설정
        }
    }

    // 모든 연결된 클라이언트에게 메시지를 브로드캐스트하는 메소드
    private static void broadcast(String message) {
        // 클라이언트들의 출력 스트림에 동기화하여 메시지를 보냄
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);  // 각 클라이언트에게 메시지 전송
            }
        }
    }
}
