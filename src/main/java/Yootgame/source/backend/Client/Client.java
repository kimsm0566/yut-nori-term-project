package Yootgame.source.backend.Client;


import Yootgame.source.backend.multiroom.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private BufferedReader serverInput;
    private PrintWriter serverOutput;
    private Socket socket;
    private boolean running = true;
    private Room currentRoom;
    private String currentLocation = "Lobby"; // 현재 위치를 추적하는 필드 추가
    private String nickname;


    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        try {
            // 서버 연결 설정
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutput = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + SERVER_PORT);

            // 닉네임 입력 처리
            setNickname();

            // 서버 메시지 수신 스레드 시작
            listenForUpdates();

            // 사용자 입력 처리 시작
            handleUserInput();

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    private void setNickname() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your nickname: ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                sendMessage("/nickname " + input);
                try {
                    String response = serverInput.readLine();
                    if (response.startsWith("Nickname set:")) {
                        this.nickname = input;
                        System.out.println(response);
                        break;
                    } else {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading from server");
                }
            } else {
                System.out.println("Nickname cannot be empty. Please try again.");
            }
        }
    }

    private void sendMessage(String message) {
        serverOutput.println(message);
    }

    // 서버로부터 메시지를 받았을 때 위치 업데이트를 처리하는 부분도 수정
    private void listenForUpdates() {
        new Thread(() -> {
            try {
                String message;
                while ((message = serverInput.readLine()) != null) {
                    // 서버 응답에 따른 위치 업데이트
                    if (message.contains("Joined room")) {
                        String roomName = message.split("'")[1];
                        currentLocation = roomName;
                    } else if (message.contains("Left room")) {
                        currentLocation = "Lobby";
                    }
                    System.out.println(message);
                    // 프롬프트 다시 표시
                    System.out.print("[" + currentLocation + "] ");
                }
            } catch (IOException e) {
                if (running) {
                    System.out.println("Connection lost or error reading data: " + e.getMessage());
                }
            } finally {
                running = false;
                closeConnection();
            }
        }).start();
    }

    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);

        while (running) {
            try {
                // 프롬프트 표시
                System.out.print("[" + currentLocation + "] ");
                String userInput = scanner.nextLine();

                if (userInput.equals("/quit")) {
                    System.out.println("Exiting client...");
                    running = false;
                    sendMessage("/quit");
                    break;
                }

                // 명령어 처리 및 위치 업데이트
                if (userInput.startsWith("/")) {
                    processCommand(userInput);
                } else {
                    sendMessage(userInput);
                }

            } catch (Exception e) {
                System.out.println("Error processing input: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // processCommand 메소드 수정
    private void processCommand(String command) {
        if (command.startsWith("/create ")) {
            sendMessage(command);
            String roomName = command.split(" ")[1];
            currentLocation = roomName; // 방 생성 시 위치 업데이트
        }
        else if (command.startsWith("/join ")) {
            sendMessage(command);
            String roomName = command.split(" ")[1];
            currentLocation = roomName; // 방 참가 시 위치 업데이트
        }
        else if (command.startsWith("/leave")) {
            sendMessage(command);
            currentLocation = "Lobby"; // 방 퇴장 시 로비로 위치 업데이트
        }
        else if (command.startsWith("/list") || command.equals("/quit")) {
            sendMessage(command);
        }
        else {
            System.out.println("Unknown command. Available commands:");
            System.out.println("/create [roomName] [turnTime] [maxPlayers]");
            System.out.println("/join [roomName]");
            System.out.println("/list");
            System.out.println("/leave");
            System.out.println("/quit");
        }
    }

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