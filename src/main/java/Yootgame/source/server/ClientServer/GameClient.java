package Yootgame.source.server.clientserver;

import java.io.*;
import java.net.*;

public class GameClient {
    // 서버의 IP 주소와 포트 번호를 상수로 정의
    private static final String SERVER_ADDRESS = "127.0.0.1";  // 로컬 서버
    private static final int SERVER_PORT = 12345;  // 서버의 포트 번호
    private BufferedReader serverInput;  // 서버로부터 입력을 받을 BufferedReader
    private PrintWriter serverOutput;  // 서버로 데이터를 보낼 PrintWriter
    private Socket socket;  // 서버와의 연결을 관리할 Socket 객체

    public static void main(String[] args) {
        // GameClient 객체 생성 후 start 메소드 호출하여 클라이언트 시작
        GameClient client = new GameClient();
        client.start();
    }

    public void start() {
        try {
            // 서버와의 연결을 위한 소켓 생성
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            // 서버에서 데이터를 읽기 위한 입력 스트림 생성
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 서버로 데이터를 보낼 출력 스트림 생성
            serverOutput = new PrintWriter(socket.getOutputStream(), true);

            // 서버로 메시지 보내기 (예: 플레이어의 말을 이동시키는 명령)
            sendMessage("MOVE player1 piece1 3");

            // 서버로부터 오는 메시지를 수신하여 처리하는 메소드 실행
            listenForUpdates();

        } catch (IOException e) {
            // 소켓 연결이나 입출력에 오류가 발생하면 예외 처리
            e.printStackTrace();
        }
    }

    // 서버로 메시지를 보내는 메소드
    private void sendMessage(String message) {
        // 서버로 메시지를 전송 (PrintWriter의 println 메소드를 사용)
        serverOutput.println(message);
    }

    // 서버로부터 오는 메시지를 실시간으로 듣고 처리하는 메소드
    private void listenForUpdates() {
        // 별도의 스레드를 생성하여 서버로부터의 메시지를 수신
        new Thread(() -> {
            try {
                String message;
                // 서버로부터 메시지를 한 줄씩 읽음
                while ((message = serverInput.readLine()) != null) {
                    // 서버로부터 받은 메시지가 "UPDATE_STATE"로 시작하는 경우
                    if (message.startsWith("UPDATE_STATE")) {
                        // 게임 상태 업데이트 메시지 처리
                        System.out.println("Game State Updated: " + message.substring(12));
                        // TODO: UI 또는 게임 상태를 반영하는 로직 추가
                    } else {
                        // "UPDATE_STATE" 외의 다른 메시지 처리
                        System.out.println("Message from server: " + message);
                    }
                }
            } catch (IOException e) {
                // 네트워크 오류나 연결 종료로 메시지를 더 이상 읽을 수 없을 때 예외 처리
                System.out.println("Connection lost or error reading data: " + e.getMessage());
            } finally {
                // 예외 발생 후 또는 연결 종료 후 소켓을 닫음
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();  // 연결이 끝난 후 소켓을 닫음
                    }
                } catch (IOException e) {
                    // 소켓을 닫을 때 발생할 수 있는 예외 처리
                    e.printStackTrace();
                }
            }
        }).start();  // 새로운 스레드를 시작하여 서버 메시지를 비동기적으로 처리
    }
}
