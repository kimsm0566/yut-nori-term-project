package Yootgame.source.component;

import Yootgame.source.backend.gamelogic.*;
import Yootgame.source.backend.multiroom.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InfoPanel extends JPanel {
    private JLabel turnLabel;          // 현재 턴 표시
    private JLabel timerLabel;         // 타이머 표시
    private JProgressBar powerBar;     // 파워 게이지
    private JLabel yotResultLabel;     // 윷 결과 레이블
    private JLabel resultImageLabel;   // 윷 결과 이미지
    private JTextArea logArea;         // 게임 로그
    private JScrollPane logScroll;     // 로그 스크롤
    private JButton throwButton;       // 윷 던지기 버튼
    private JButton newPieceButton;    // 새 말 버튼
    private Timer powerTimer;          // 파워 게이지 타이머
    private Timer gameTimer;           // 게임 타이머
    private int remainingTime;         // 남은 시간
    private JLabel yotResult;          // 윷 결과 텍스트
    private JLabel resultLabel;        // 결과 텍스트
    private int power = 0;            // 현재 파워 값
    private boolean increasing = true; // 파워 증가/감소 상태
    private Room currentRoom;
    private JLabel redPieceLabel;     // 레드팀 말 개수 표시
    private JLabel bluePieceLabel;    // 블루팀 말 개수 표시
    private JButton[] testButton = new JButton[7];
    private ActionListener actionListener;

    public InfoPanel(Room room, ActionListener actionListener) {
        this.currentRoom = room;
        this.actionListener = actionListener;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        initializeComponents();
        // Room에서 설정된 턴 시간으로 타이머 시작
        startTimer(currentRoom.getTurnTime());
    }
    // getter 메소드 추가
    public JButton[] getTestButtons() {
        return testButton;
    }
    private void initializeComponents() {
        // 현재 턴 표시 패널
        createTurnPanel();
        add(Box.createVerticalStrut(15));

        // 플레이어 현황 패널
        createPlayerStatusPanel();
        add(Box.createVerticalStrut(20));

        // 윷 결과 표시 영역
        createResultPanel();
        add(Box.createVerticalStrut(20));

        // 게임 정보 패널 (결과 + 타이머)
        createGameInfoPanel();
        add(Box.createVerticalStrut(20));

        // 파워 게이지
        createPowerBar();
        add(Box.createVerticalStrut(20));

        // 컨트롤 버튼
        createControlButtons();

    }

    private void createTurnPanel() {
        JPanel turnPanel = new JPanel();
        turnPanel.setBackground(Color.WHITE);
        turnPanel.setMaximumSize(new Dimension(400, 60));
        turnPanel.setBorder(BorderFactory.createTitledBorder("현재 턴"));
        turnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        turnLabel = new JLabel("게임 시작 대기중...");
        turnLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        turnPanel.add(turnLabel);
        add(turnPanel);
    }

    public void updatePieceCount(boolean isHost, int count) {
        if (isHost) {
            redPieceLabel.setText("남은 말: " + count);
        } else {
            bluePieceLabel.setText("남은 말: " + count);
        }
    }

    private void createPlayerStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(Color.white);
        statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 실제 Room의 말 개수를 가져와서 표시
        redPieceLabel = new JLabel("레드팀 남은 말: " + currentRoom.getNumberOfPiece());
        bluePieceLabel = new JLabel("블루팀 남은 말: " + currentRoom.getNumberOfPiece());
        redPieceLabel.setForeground(Color.RED);
        bluePieceLabel.setForeground(Color.BLUE);
        redPieceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bluePieceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusPanel.add(redPieceLabel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(bluePieceLabel);
        add(statusPanel);
    }

    private void createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(Color.white);
        resultPanel.setPreferredSize(new Dimension(200, 200));
        resultPanel.setMaximumSize(new Dimension(200, 200));

        resultLabel = new JLabel("");
        resultLabel.setHorizontalAlignment(JLabel.CENTER);
        resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        resultImageLabel = new JLabel();

        resultPanel.add(resultImageLabel, BorderLayout.CENTER);
        resultPanel.add(resultLabel, BorderLayout.SOUTH);
        add(resultPanel);
    }

    private void createGameInfoPanel() {
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 50, 0));
        infoPanel.setBackground(Color.white);
        infoPanel.setMaximumSize(new Dimension(200, 50));

        yotResultLabel = new JLabel("결과");
        yotResultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        timerLabel = new JLabel("30초");
        timerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JPanel resultTextPanel = new JPanel();
        resultTextPanel.setBackground(Color.WHITE);
        resultTextPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
        resultTextPanel.add(yotResultLabel);

        JPanel timerPanel = new JPanel();
        timerPanel.setBackground(Color.WHITE);
        timerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
        timerPanel.add(timerLabel);

        infoPanel.add(resultTextPanel);
        infoPanel.add(timerPanel);
        add(infoPanel);
    }
    public JButton getThrowButton() {
        return throwButton;
    }

    public JButton getNewPieceButton() {
        return newPieceButton;
    }

    private void createPowerBar() {
        powerBar = new JProgressBar(0, 100);
        powerBar.setPreferredSize(new Dimension(150, 20));
        powerBar.setMaximumSize(new Dimension(150, 20));
        powerBar.setStringPainted(true);
        powerBar.setForeground(new Color(255, 200, 0));
        powerBar.setBackground(Color.black);
        powerBar.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
        add(powerBar);
    }

    private void createControlButtons() {
        // 윷 던지기 버튼
        throwButton = new JButton("윷 던지기");
        throwButton.setPreferredSize(new Dimension(300, 50));
        throwButton.setMaximumSize(new Dimension(300, 50));
        throwButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        throwButton.addActionListener(actionListener);  // ActionListener 연결

        // 새로운 말 버튼
        newPieceButton = new JButton("새로운 말 꺼내기");
        newPieceButton.setPreferredSize(new Dimension(300, 50));
        newPieceButton.setMaximumSize(new Dimension(300, 50));
        newPieceButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        newPieceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPieceButton.addActionListener(actionListener);  // ActionListener 연결

        add(throwButton);
        add(Box.createVerticalStrut(10));
        add(newPieceButton);
    }

    public void updateYotResult(int result) {
        String resultText;
        String imagePath;
        switch(result) {
            case Yoot.BACKDO:
                resultText = "빽도";
                imagePath = "src/main/java/Yootgame/img/backDO.png";
                break;
            case Yoot.DO:
                resultText = "도";
                imagePath = "src/main/java/Yootgame/img/DO.png";
                break;
            case Yoot.GAE:
                resultText = "개";
                imagePath = "src/main/java/Yootgame/img/GAE.png";
                break;
            case Yoot.GUL:
                resultText = "걸";
                imagePath = "src/main/java/Yootgame/img/GEOL.png";
                break;
            case Yoot.YOOT:
                resultText = "윷";
                imagePath = "src/main/java/Yootgame/img/YUT.png";
                break;
            case Yoot.MO:
                resultText = "모";
                imagePath = "src/main/java/Yootgame/img/MO.png";
                break;
            default:
                resultText = "";
                imagePath = "";
        }
        yotResultLabel.setText(resultText);
        // 이미지 업데이트
        resultImageLabel.setIcon(new ImageIcon(imagePath));

        // 윷이나 모가 아닌 경우 버튼 비활성화
        if (result != Yoot.YOOT && result != Yoot.MO) {
            throwButton.setEnabled(false);
        }
        startTimer(30);
    }

    // 상대 말을 잡았을 때 호출할 메소드 추가
    public void enableThrowButton() {
        throwButton.setEnabled(true);
    }
    public void message(String s) {
        if (logArea != null) {
            logArea.append(s + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    public void startTimer(int seconds) {
        remainingTime = seconds;
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new Timer(1000, e -> {
            remainingTime--;
            timerLabel.setText(remainingTime + "초");
            if (remainingTime <= 0) {
                ((Timer)e.getSource()).stop();
            }
        });
        gameTimer.start();
    }

    public void updateTurn(String nickname, String playerType, boolean isCurrentTurn) {
        turnLabel.setText(playerType + " (" + nickname + ") 차례입니다");
        turnLabel.setForeground(playerType.equals("레드팀") ? Color.RED : Color.BLUE);
        throwButton.setEnabled(isCurrentTurn);  // 현재 턴인 경우만 버튼 활성화
        newPieceButton.setEnabled(isCurrentTurn);  // 현재 턴인 경우만 버튼 활성화
    }

    public void updateTimer(int seconds) {
        timerLabel.setText(seconds + "초");
    }

}