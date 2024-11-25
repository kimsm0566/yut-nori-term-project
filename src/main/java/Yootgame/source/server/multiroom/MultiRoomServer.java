package Yootgame.source.server.multiroom;

import java.io.*;
import java.net.*;

// 멀티룸 서버를 관리하는 클래스
// 서버 소켓을 생성하여 클라이언트의 연결을 수락하고, 클라이언트마다 별도의 스레드를 관리함
public class MultiRoomServer {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Start multi-room server...\n");

        // 서버 소켓을 생성하고 클라이언트 연결 대기
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            RoomManager roomManager = new RoomManager(); // RoomManager 클래스 생성

            while (true) {
                Socket socket = serverSocket.accept(); //클라이언트 연결 수락
                System.out.println("Client Connected:\n " + socket.getInetAddress());

                // 새로운 스레드 생성하여 클라이언트 요청 처리
                new ClientHandler(socket, roomManager).start();
            }
        } catch (IOException e) {
            e.printStackTrace(); //실행 중 예외 처리
        }
    }
}
