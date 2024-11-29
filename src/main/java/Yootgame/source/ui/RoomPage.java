package Yootgame.source.ui;

import javax.swing.*;
import java.awt.*;

import Yootgame.source.backend.Client.Client;
import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.component.*;

public class RoomPage extends JFrame {
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;

    private Room currentRoom;
    private String nickname;
    private PlayerPanel hostPanel;
    private PlayerPanel guestPanel;
    private SettingPanel settingPanel;
    private LogPanel logPanel;
    private TitlePanel titlePanel;
    private boolean isHost;
    private Client client;  // Client 참조 추가
    private Timer countdownTimer;  // 타이머 변수 추가

    public RoomPage(Room room, String nickname, boolean isHost, Client client) {  // Client 매개변수 추가
        this.currentRoom = room;
        this.nickname = nickname;
        this.isHost = isHost;
        this.client = client;  // Client 참조 저장


        initializeFrame();
        createAndShowGUI();
        updateRoomInfo();

        // 게스트인 경우 방장 정보 요청
        if (!isHost) {
            client.sendMessage("/requestHostInfo");
        }
    }


    private void initializeFrame() {
        setTitle("윷놀이");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void createAndShowGUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());

        // 왼쪽 패널 (게임 화면)
        JPanel gamePanel = createGamePanel();

        // 오른쪽 패널 (정보 패널)
        JPanel infoPanel = createInfoPanel();

        // 레이아웃 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 게임 패널 추가
        gbc.weightx = 0.75;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        mainPanel.add(gamePanel, gbc);

        // 정보 패널 추가
        gbc.weightx = 0.25;
        gbc.gridx = 1;
        mainPanel.add(infoPanel, gbc);

        add(mainPanel);
    }

    private JPanel createGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        ImageIcon icon = new ImageIcon("src/main/java/Yootgame/img/backgroundFicture.png");
        JLabel background = new JLabel(icon);
        gamePanel.add(background);
        return gamePanel;
    }

    public void updatePlayerLeft() {
        if (isHost) {
            // 게스트가 나간 경우
            guestPanel.setPlayerName("대기중...");
            guestPanel.setReadyState(false);  // 준비 상태 초기화
            guestPanel.setReadyButtonEnabled(false);  // 버튼 비활성화
            guestPanel.revalidate();
            guestPanel.repaint();
        }
        // 방장이 나간 경우는 처리하지 않음 (게스트도 함께 나가기 때문)
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setBackground(Color.WHITE);

        titlePanel = new TitlePanel();

        if (isHost) {
            hostPanel = new PlayerPanel(true, nickname, client);  // client 객체 전달
            guestPanel = new PlayerPanel(false, "대기 중...", client);
        } else {
            hostPanel = new PlayerPanel(true, "상대방", client);  // client 객체 전달
            guestPanel = new PlayerPanel(false, nickname, client);
        }

        settingPanel = new SettingPanel(currentRoom);
        logPanel = new LogPanel();
        ButtonPanel buttonPanel = new ButtonPanel(this);

        // 컴포넌트 추가
        infoPanel.add(titlePanel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(hostPanel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(guestPanel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(settingPanel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(logPanel);
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(buttonPanel);

        return infoPanel;
    }


    public void updateRoomInfo() {
        if (currentRoom != null) {
            titlePanel.updateTitle(currentRoom.getName());

            // 방 설정값 디버그 출력 추가
            System.out.println("Debug - Room Settings: " +
                    "Name=" + currentRoom.getName() +
                    ", TurnTime=" + currentRoom.getTurnTime() +
                    ", NumberOfPiece=" + currentRoom.getNumberOfPiece());

            settingPanel.updateSettings(currentRoom);

            if (isHost) {
                // 방장인 경우
                hostPanel.setPlayerName(nickname);
                hostPanel.setReadyButtonEnabled(true);
                guestPanel.setReadyButtonEnabled(false);

                if (currentRoom.getClientCount() == 1) {
                    guestPanel.setPlayerName("대기 중...");
                }
            } else {
                // 게스트인 경우
                guestPanel.setPlayerName(nickname);
                guestPanel.setReadyButtonEnabled(true);
                hostPanel.setReadyButtonEnabled(false);
                hostPanel.setPlayerName("상대방");
            }

            addLogMessage(nickname + "님이 입장하셨습니다.");
        }
    }

    public void updateReadyState(boolean isGuest, boolean ready) {
        if (isGuest) {
            guestPanel.setReadyState(ready);
        } else {
            hostPanel.setReadyState(ready);
        }

        // 양쪽 모두 준비되었는지 확인
        if (hostPanel.isPlayerReady() && guestPanel.isPlayerReady()) {
            client.sendMessage("/startCountdown");
        }
    }


    public void startGameCountdown() {
        final int[] count = {5};
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        countdownTimer = new Timer(1000, e -> {
            if (!hostPanel.isPlayerReady() || !guestPanel.isPlayerReady()) {
                ((Timer)e.getSource()).stop();
                addLogMessage("준비 상태가 해제되어 게임 시작이 취소되었습니다.");
                client.sendMessage("/countdown_cancel");
                return;
            }
            if (count[0] > 0) {
                addLogMessage(count[0] + "초 후에 게임이 시작됩니다.");
                count[0]--;
            } else {
                ((Timer)e.getSource()).stop();
                addLogMessage("게임이 시작되었습니다!");
                client.sendMessage("/startGame");
            }
        });
        countdownTimer.start();
    }



    public void addLogMessage(String message) {
        logPanel.addMessage(message);
    }

    public JButton getHostReadyButton() {
        return hostPanel.getReadyButton();
    }

    public JButton getGuestReadyButton() {
        return guestPanel.getReadyButton();
    }

    public void updateHostName(String name) {
        hostPanel.setPlayerName(name);
    }

    public void updateGuestName(String name) {
        guestPanel.setPlayerName(name);
    }
    public Client getClient() {
        return this.client;
    }
}