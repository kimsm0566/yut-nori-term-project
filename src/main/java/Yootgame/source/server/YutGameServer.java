package Yootgame.source.server;

//JavaObjServer.java ObjectStream 기반 채팅 Server

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.*;

public class YutGameServer extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    JTextArea textArea;
    private JTextField txtPortNumber;

    private ServerSocket socket; // 서버소켓
    private Socket client_socket; // accept() 에서 생성된 client 소켓
    private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    boolean[] userConnect = new boolean[4];
    private Map<Integer, GameRoom> rooms = new HashMap<>();
    private int roomCounter=0;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    YutGameServer frame = new YutGameServer();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // 새로운 방 생성
    public GameRoom createRoom(String roomName) {
        GameRoom room = new GameRoom(roomCounter, roomName);
        rooms.put(roomCounter, room);
        roomCounter++;
        return room;
    }

    // 방 입장 처리
    public boolean joinRoom(int roomId, UserService user) {
        GameRoom room = rooms.get(roomId);
        if (room != null && !room.isFull()) {
            room.addUser(user);
            return true;
        }
        return false;
    }

    // 방 나가기 처리
    public void leaveRoom(int roomId, UserService user) {
        GameRoom room = rooms.get(roomId);
        if (room != null) {
            room.removeUser(user);
            // 방이 비었다면 방 삭제
            if (room.isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }

    // YutGameServer.java에서
    public List<GameRoom> getRoomList() {
        System.out.println("현재 방 목록: " + rooms.size() + "개");
        return new ArrayList<>(rooms.values());
    }


    /**
     * Create the frame.
     */
    public YutGameServer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 338, 440);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 300, 298);
        contentPane.add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        JLabel lblNewLabel = new JLabel("Port Number");
        lblNewLabel.setBounds(13, 318, 87, 26);
        contentPane.add(lblNewLabel);

        txtPortNumber = new JTextField();
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setText("30000");
        txtPortNumber.setBounds(112, 318, 199, 26);
        contentPane.add(txtPortNumber);
        txtPortNumber.setColumns(10);

        JButton btnServerStart = new JButton("Server Start");
        btnServerStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
                } catch (NumberFormatException | IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                AppendText("Chat Server Running..");
                btnServerStart.setText("Chat Server Running..");
                btnServerStart.setEnabled(false);
                txtPortNumber.setEnabled(false);

                AcceptServer accept_server = new AcceptServer();
                accept_server.start();
            }
        });
        btnServerStart.setBounds(12, 356, 300, 35);
        contentPane.add(btnServerStart);
    }

    // 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
    class AcceptServer extends Thread {
        @SuppressWarnings("unchecked")
        public void run() {
            while (true) { // 사용자 접속을 계속해서 받기 위해 while문
                try {
                    AppendText("Waiting new clients ...");
                    client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
                    AppendText("새로운 참가자 from " + client_socket);
                    // User 당 하나씩 Thread 생성
                    UserService new_user = new UserService(client_socket, YutGameServer.this, UserVec);
                    UserVec.add(new_user); // 새로운 참가자 배열에 추가
                    new_user.start(); // 만든 객체의 스레드 실행
                    AppendText("현재 참가자 수 " + UserVec.size());
                    System.out.println("현재 참가자 수 출력: " + UserVec.size());
                } catch (IOException e) {
                    AppendText("accept() error");
                    // System.exit(0);
                }
            }
        }
    }

    public void AppendText(String str) {
        // textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    public void AppendObject(ChatMsg msg) {
        // textArea.append("사용자로부터 들어온 object : " + str+"\n");
        textArea.append("code = " + msg.code + "\n");
        textArea.append("id = " + msg.UserName + "\n");
        textArea.append("data = " + msg.data + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    // getter 메소드 추가
    public Vector<UserService> getUserVec() {
        return UserVec;
    }
}