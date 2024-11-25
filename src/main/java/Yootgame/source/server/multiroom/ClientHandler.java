package Yootgame.source.server.multiroom;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private final Socket socket; // 클라이언트와의 연결을 나타내는 소켓 객체
    private final RoomManager roomManager; // 룸 관리 객체
    private String currentRoom; // 클라이언트가 현재 참여 중인 방 이름
    private PrintWriter out; // 클라이언트로 메시지를 전송하기 위한 출력 스트림

    //생성자: 클라이언트와 연결된 소켓과 룸 관리 객체 초기화
    public ClientHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket; //소켓 객체 초기화
        this.roomManager = roomManager; //룸 관리 객체 초기화
    }

    @Override
    public void run() {
        try (
                // 클라이언트로부터 데이터를 읽어오는 BufferedReader
                // socket.getInputStream() << socket 객체에서 입력 스트림을 얻음 서버와 클라이언트 간의
                // 네트워크 연결을 통해 전송되는 데이터를 받는 역할
                // InputStreamReader()<< 는 바이트 기반의 입력 스트림(InputStream)을 문자 기반의 입력 스트림(Reader)으로 변환하는 역할
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // 클라이언트에게 데이터를 보내는 PrintWriter
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {

            //아웃 객체를 통해 서버 연결 클라이언트에게 알림
            this.out = out;
            out.println("The connection to the server was successful.");

            // 클라이언트 명령어 처리하는 반복문
            String command;
            while ((command = in.readLine()) != null) {
                processCommand(command); // 클라이언트로부터 받은 명령어 처리
            }
        } catch (IOException e) {
            e.printStackTrace(); // 소켓 통신 중 오류 발생 시 출력
        } finally {
            leaveRoom(); // 스레드 종료 시 클라이언트를 방에서 제거
            try {
                socket.close(); // 소켓 자원 해제
            } catch (IOException e) {
                e.printStackTrace(); // 소켓 통신 종료 중 오류 발생 시 출력
            }
        }
    }

    // 클라이언트의 명령어를 처리 메서드
    private void processCommand(String command) {
        if (command.startsWith("/create ")) {   // /create 명령어 처리
            String roomName = command.substring(8).trim(); // 룸 이름 추출
            roomManager.createRoom(roomName); // 룸 생성
            out.println("room '" + roomName + "'Generation completed."); // 룸 생성 완료 메세지 출력
        } else if (command.startsWith("/join ")) {
            String roomName = command.substring(6).trim();
            if (currentRoom != null) {
                out.println("You are already participating in another room. First, go to the /leave command.");
                return;
            }
            if (roomManager.joinRoom(roomName, this)) {
                currentRoom = roomName;
                out.println("'Participated in the " + roomName + "' room.");
            } else {
                out.println("Room does not exist.");
            }
        } else if (command.equals("/list")) {
            listRooms(); //현재 생성된 룸 목록 보여줌
        } else if (command.equals("/leave")) {
            leaveRoom(); //현재 참여 중인 룸에서 나감
        } else if (command.equals("/quit")) {
            out.println("Terminate the connection to the server.\n");
            leaveRoom(); //현재 참여 중인 룸에서 나감
            interrupt(); //스레드 종료
        } else {
            out.println("Unknown command."); //정의되지 않은 명령어 처리
        }
    }

    //현재 존재하는 방 목록 클라이언트에게 전송
    private void listRooms() {
        var roomList = roomManager.listRooms(); //룸 목록 roomManager 클래스에서 받아옴
        if (roomList.isEmpty()) {
            out.println("Currently no rooms created.");
        } else {
            out.println("Currently created rooms:");
            for (String room : roomList) {
                out.println("- " + room); // 방 이름 리스트 출력
            }
        }
    }


    private void leaveRoom() {
        if (currentRoom != null) {
            roomManager.leaveRoom(currentRoom, this);
            out.println("'" + currentRoom + "' leave room");
            currentRoom = null;
        } else {
            out.println("No rooms are participating.");
        }
    }
}