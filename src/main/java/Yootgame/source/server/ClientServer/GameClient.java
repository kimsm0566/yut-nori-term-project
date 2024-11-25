package Yootgame.source.server.clientserver;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // 서버 주소
    private static final int SERVER_PORT = 12345; // 서버 포트
    private BufferedReader serverInput; // 서버로부터 입력을 받는 스트림
    private PrintWriter serverOutput; // 서버로 데이터를 보내는 스트림
    private Socket socket; // 서버와의 연결을 관리하는 소켓
    private boolean running = true; // 클라이언트 실행 상태

    public static void main(String[] args) {
        // GameClient 인스턴스 생성 및 시작
        GameClient client = new GameClient();
        client.start();
    }

    public void start() {
        try {
            // 서버와 연결 설정
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutput = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + SERVER_PORT);

            // 서버로부터 오는 메시지를 처리하는 스레드 실행
            listenForUpdates();

            // 사용자 입력을 처리하는 메소드 호출
            handleUserInput();

        } catch (IOException e) {
            // 연결 오류 처리
            System.out.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 종료 시 자원 해제
            closeConnection();
        }
    }

    // 서버로 메시지를 보내는 메소드
    private void sendMessage(String message) {
        serverOutput.println(message); // 서버로 메시지 전송
    }

    // 서버로부터 메시지를 수신하고 처리하는 메소드
    private void listenForUpdates() {
        new Thread(() -> {
            try {
                String message;
                while ((message = serverInput.readLine()) != null) {
                    if (message.startsWith("UPDATE_STATE")) {
                        // 서버로부터 게임 상태 업데이트를 받았을 때
                        System.out.println("Game State Updated: " + message.substring(12));
                        // TODO: 받은 게임 상태를 실제 UI나 게임 로직에 반영
                    } else if (message.startsWith("RESET_STATE")) {
                        // 서버로부터 게임 상태 초기화 메시지를 받았을 때
                        System.out.println("Game state has been reset.");
                    } else {
                        // 기타 메시지 처리
                        System.out.println("Message from server: " + message);
                    }
                }
            } catch (IOException e) {
                // 네트워크 오류 처리
                System.out.println("Connection lost or error reading data: " + e.getMessage());
            } finally {
                // 연결 종료 시 클라이언트 종료
                running = false;
                closeConnection();
            }
        }).start();
    }

    // 사용자 입력을 처리하여 서버로 명령을 보내는 메소드
    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter commands (e.g., MOVE player1 piece1 3, RESET_STATE):");

        while (running) {
            try {
                String userInput = scanner.nextLine(); // 사용자로부터 명령 입력 받기

                // 종료 명령 처리
                if ("exit".equalsIgnoreCase(userInput)) {
                    System.out.println("Exiting client...");
                    running = false;
                    break;
                }

                // 서버로 사용자 명령 전송
                sendMessage(userInput);

            } catch (Exception e) {
                // 사용자 입력 오류 처리
                System.out.println("Error processing input: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // 연결을 종료하고 자원을 해제하는 메소드
    private void closeConnection() {
        try {
            if (serverInput != null) serverInput.close();
            if (serverOutput != null) serverOutput.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Connection to server closed.");
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
