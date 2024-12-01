package Yootgame.source.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class robbyPage extends JFrame {
    private JPanel roomsPanel;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String UserName;

    public robbyPage(String username, String ip_addr, String port_no) {
        this.UserName = username;
        try {
            this.socket = new Socket(ip_addr, Integer.parseInt(port_no));
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            // 먼저 메시지 리스너 시작
            startMessageListener();
            // UI 초기화
            initializeUI();
            // 그 다음 방 목록 요청
            requestRoomList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setTitle("윷놀이");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 메인 패널
        JPanel mainPanel = new JPanel(new GridBagLayout());

//        // 왼쪽 이미지 패널
//        JPanel imagePanel = new JPanel(new BorderLayout());
//        ImageIcon originalIcon = new ImageIcon("src/main/java/Yootgame/img/backgroundFicture.png");
//        JLabel imageLabel = new JLabel() {
//            @Override
//            protected void paintComponent(Graphics g) {
//                Graphics2D g2d = (Graphics2D) g;
//                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                Image img = originalIcon.getImage();
//                g2d.drawImage(img, 0, 0, getWidth(), getHeight(), null);
//            }
//        };
//        imagePanel.add(imageLabel);

        // 오른쪽 방 목록 패널
        JPanel roomListPanel = new JPanel(new BorderLayout());

        // 상단 제목
        JLabel titleLabel = new JLabel("로비", SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(255, 255, 255));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setPreferredSize(new Dimension(0, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 방 목록 패널
        roomsPanel = new JPanel();
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(roomsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 방 만들기 버튼
        JButton createRoomButton = new JButton("방 만들기");
        createRoomButton.setPreferredSize(new Dimension(0, 40));
        createRoomButton.addActionListener(e -> createRoom());

        roomListPanel.add(titleLabel, BorderLayout.NORTH);
        roomListPanel.add(scrollPane, BorderLayout.CENTER);
        roomListPanel.add(createRoomButton, BorderLayout.SOUTH);

        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.67;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
//        mainPanel.add(imagePanel, gbc);

        gbc.weightx = 0.33;
        gbc.gridx = 1;
        mainPanel.add(roomListPanel, gbc);

        add(mainPanel);
    }

    private void createRoom() {
        String roomName = JOptionPane.showInputDialog(this, "방 제목을 입력하세요:", "방 만들기", JOptionPane.PLAIN_MESSAGE);
        if (roomName != null && !roomName.trim().isEmpty()) {
            try {
                ChatMsg createRoomMsg = new ChatMsg(UserName, "201", roomName);
                oos.writeObject(createRoomMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        requestRoomList();
    }

    private void requestRoomList() {
        try {
            ChatMsg roomListReq = new ChatMsg(UserName, "202", "");
            oos.writeObject(roomListReq);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMessageListener() {
        new Thread(() -> {
            try {
                while (true) {
                    Object obj = ois.readObject();
                    if (obj instanceof ChatMsg) {
                        ChatMsg msg = (ChatMsg) obj;
                        handleMessage(msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleMessage(ChatMsg msg) {
        switch (msg.code) {
            case "202":  // 방 목록 응답
                updateRoomList(msg.data);
                break;
            case "203":  // 방 생성 성공
                enterRoom(msg.data);
                break;
            case "204":  // 방 입장 성공
                enterRoom(msg.data);
                break;
        }
    }

    private void updateRoomList(String roomData) {
        System.out.println("방 목록 데이터 수신: " + roomData);
        SwingUtilities.invokeLater(() -> {
            roomsPanel.removeAll();
            String[] rooms = roomData.split(";");
            System.out.println("방 개수: " + rooms.length);

            for (String room : rooms) {
                if (!room.isEmpty()) {  // 빈 문자열 체크 추가
                    String[] roomInfo = room.split(",");
                    JPanel roomPanel = createRoomPanel(roomInfo[1],
                            roomInfo[2] + "/4명", roomInfo[0]);
                    roomsPanel.add(roomPanel);
                }
            }

            // UI 갱신 호출 추가
            roomsPanel.revalidate();
            roomsPanel.repaint();
            System.out.println("방 목록 업데이트: " + roomData);  // 디버깅용
        });
    }

    private void enterRoom(String roomId) {
        // 소켓에서 IP 주소와 포트 정보 가져오기
        String ip_addr = socket.getInetAddress().getHostAddress();
        String port_no = String.valueOf(socket.getPort());
        // 게임 화면으로 전환
        YutGameClientView gameView = new YutGameClientView(UserName, ip_addr, port_no);        gameView.setVisible(true);
        this.dispose();
    }

    private JPanel createRoomPanel(String title, String players, String roomId) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 5)); // 2행 2열의 그리드 레이아웃
        panel.setBorder(BorderFactory.createLineBorder(Color.white));
        panel.setPreferredSize(new Dimension(0, 80)); // 높이를 늘림
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // 테두리 설정 - 회색 라인과 패딩을 함께 적용
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255), 1), // 바깥쪽 테두리
                BorderFactory.createEmptyBorder(15, 15, 15, 15)  // 안쪽 패딩
        ));

        // 배경색 설정
        Color defaultBackground = new Color(255, 255, 255);
        panel.setBackground(defaultBackground);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 왼쪽 상단: 방 제목
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        // 오른쪽 상단: 인원 수
        JLabel playersLabel = new JLabel(players);
        playersLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // 왼쪽 하단: 말 개수
        JLabel pieceLabel = new JLabel("말 개수: 4개");
        pieceLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        pieceLabel.setForeground(new Color(100, 100, 100));

        // 오른쪽 하단: 턴 시간
        JLabel turnTimeLabel = new JLabel("턴 시간: 30초");
        turnTimeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        turnTimeLabel.setForeground(new Color(100, 100, 100));

        // 컴포넌트 추가
        panel.add(titleLabel);
        panel.add(playersLabel);
        panel.add(pieceLabel);
        panel.add(turnTimeLabel);

        // 마우스 이벤트
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(defaultBackground);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ChatMsg enterRoomMsg = new ChatMsg(UserName, "203", roomId);
                    oos.writeObject(enterRoomMsg);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "방 입장 중 오류가 발생했습니다.",
                            "오류",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }
}