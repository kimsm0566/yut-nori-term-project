package Yootgame.source.ui;

import Yootgame.source.backend.Client.Client;
import Yootgame.source.backend.multiroom.Room;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class robbyPage extends JFrame {
    private JPanel roomsPanel;
    private JScrollPane scrollPane;
    private JButton createRoomButton;
    private Client client;  // Client 인스턴스를 저장할 필드 추가

    public robbyPage(Client client) {
        this.client = client;
        initializeFrame();
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
        setVisible(true);
        // 초기 방 목록 요청
        client.sendMessage("/list");
    }

    private void initializeFrame() {
        setTitle("윷놀이");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());

        // 왼쪽 이미지 패널
        JPanel imagePanel = createImagePanel();

        // 오른쪽 방 목록 패널
        JPanel roomListPanel = createRoomListPanel();

        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 왼쪽 패널 추가
        gbc.weightx = 0.67;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(imagePanel, gbc);

        // 오른쪽 패널 추가
        gbc.weightx = 0.33;
        gbc.gridx = 1;
        mainPanel.add(roomListPanel, gbc);

        return mainPanel;
    }

    private JPanel createImagePanel() {
        JPanel imagePanel = new JPanel(new BorderLayout());
        ImageIcon originalIcon = new ImageIcon("src/main/java/Yootgame/img/backgroundFicture.png");
        JLabel imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                Image img = originalIcon.getImage();
                g2d.drawImage(img, 0, 0, getWidth(), getHeight(), null);
            }
        };
        imagePanel.add(imageLabel);
        return imagePanel;
    }

    private JPanel createRoomListPanel() {
        JPanel roomListPanel = new JPanel(new BorderLayout());

        // 상단 패널 (제목과 새로고침 버튼을 포함)
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = createTitleLabel();
        JButton refreshButton = createRefreshButton();

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);

        // 방 목록 패널
        roomsPanel = new JPanel();
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));

        // 스크롤 패널
        scrollPane = new JScrollPane(roomsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 방 만들기 버튼
        createRoomButton = new JButton("방 만들기");
        createRoomButton.setPreferredSize(new Dimension(0, 40));
        createRoomButton.addActionListener(e -> handleCreateRoom());

        roomListPanel.add(topPanel, BorderLayout.NORTH);
        roomListPanel.add(scrollPane, BorderLayout.CENTER);
        roomListPanel.add(createRoomButton, BorderLayout.SOUTH);

        return roomListPanel;
    }
    private JButton createRefreshButton() {
        JButton refreshButton = new JButton("새로고침");
        refreshButton.setPreferredSize(new Dimension(100, 40));
        refreshButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshRoomList());
        return refreshButton;
    }
    private void refreshRoomList() {
        client.sendMessage("/list");  // 서버에 방 목록 요청
    }
    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("로비", SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(255, 255, 255));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setPreferredSize(new Dimension(0, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return titleLabel;
    }

    private JPanel createRoomPanel(String title, String players, String pieces, String turnTime) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 5));
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        Color defaultBackground = new Color(255, 255, 255);
        panel.setBackground(defaultBackground);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 라벨 생성 및 설정
        JLabel titleLabel = new JLabel(title);
        JLabel playersLabel = new JLabel(players);
        JLabel pieceLabel = new JLabel(pieces);
        JLabel turnTimeLabel = new JLabel(turnTime);

        // 폰트 및 스타일 설정
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        playersLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        pieceLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        turnTimeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        // 컴포넌트 추가
        panel.add(titleLabel);
        panel.add(playersLabel);
        panel.add(pieceLabel);
        panel.add(turnTimeLabel);

        // 마우스 이벤트 추가
        addMouseListeners(panel, title, players, pieces, turnTime, defaultBackground);

        return panel;
    }

    private void addMouseListeners(JPanel panel, String title, String players,
                                   String pieces, String turnTime, Color defaultBackground) {
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
                showJoinRoomDialog(title, players, pieces, turnTime);
            }
        });
    }

    private void showJoinRoomDialog(String title, String players, String pieces, String turnTime) {
        // 현재 인원 수 확인
        String[] playerCount = players.split("/");
        int currentPlayers = Integer.parseInt(playerCount[0]);
        int maxPlayers = Integer.parseInt(playerCount[1]);

        if (currentPlayers >= maxPlayers) {
            JOptionPane.showMessageDialog(
                    null,
                    "방이 가득 찼습니다.",
                    "입장 불가",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 방이 가득 차지 않은 경우 기존 다이얼로그 표시
        int option = JOptionPane.showConfirmDialog(
                null,
                title + "\n" +
                        "플레이어: " + players + "\n" +
                        pieces + "\n" +
                        turnTime + "\n\n" +
                        "입장하시겠습니까?",
                "방 입장",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            sendJoinRequest(title);
        }
    }

    public void updateRoomList(List<Room> rooms) {
        roomsPanel.removeAll();
        if (rooms.isEmpty()) {
            JLabel emptyLabel = new JLabel("생성된 방이 없습니다.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roomsPanel.add(emptyLabel);
        } else {
            for (Room room : rooms) {
                JPanel roomPanel = createRoomPanel(
                        room.getName(),
                        room.getClientCount() + "/2",  // getClients().size() 대신 getClientCount() 사용
                        "말 개수: " + room.getNumberOfPiece() + "개",
                        "턴 시간: " + room.getTurnTime() + "초"
                );
                roomsPanel.add(roomPanel);
            }
        }
        roomsPanel.revalidate();
        roomsPanel.repaint();
    }
    public void handleRefreshButtonClick() {
        client.getConnectionManager().sendMessage("/list");
    }

    private void handleCreateRoom() {
        RoomConfigPage configPage = new RoomConfigPage(this, client);
        configPage.setVisible(true);
        // 방 목록 갱신 요청
        client.sendMessage("/list");
    }
    private void sendJoinRequest(String roomName) {
        // 서버에 방 입장 요청 전송
        client.sendMessage("/join " + roomName);
        // 실제 구현시에는 Client 인스턴스를 필드로 가지고 있어야 합니다
    }
}