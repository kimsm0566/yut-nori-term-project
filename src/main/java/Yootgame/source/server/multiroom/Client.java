package Yootgame.source.server.multiroom;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; // 서버 주소
    private static final int SERVER_PORT = 12345; // 서버 포트 번호
    private static Socket socket; //서버 연걸 위한 소켓 객체
    private static BufferedReader in; //서버로부터 데이터를 읽어오는 입력 스트림
    private static PrintWriter out; //서버에게 데이터를 전송하는 출력 스트림

    public static void main(String[] args) {
        try {
            // 서버와의 연결을 위해 소켓 생성사고 서버 주소와 포트 지정하여 연결
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //서버로부터 메세지 읽기위한 입력 스트림
            out = new PrintWriter(socket.getOutputStream(), true); // 서버로 메시지를 보내기 위한 출력 스트림
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in)); //유저로부터 입력 받기위한 입력 스트림

            // 서버로부터의 메시지 읽는 스레드
            Thread messageReader = new Thread(new MessageReader());
            messageReader.start();

            // 사용자 입력을 받아 서버에 명령어 전송하는 반복문
            String command;
            while (true) {
                command = userInput.readLine(); //유저로부터 명령어 입력받음
                if (command != null) {
                    out.println(command); //입력된 명령어 서버로 전송
                    if (command.equals("/quit")) {
                        break; // /quit 명령어로 연결 종료
                    }
                }
            }

            socket.close(); // 서버와의 연결 종료
        } catch (IOException e) {
            e.printStackTrace(); //에러 처리
        }
    }

    // 서버로부터의 메시지를 출력하는 스레드
    static class MessageReader implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;

                // 서버로부터 메세지 읽는 반복문
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage); //서버로부터 받은 메세지 콘솔에 출력
                }
            } catch (IOException e) {
                e.printStackTrace(); //예외처리
            }
        }
    }
}
