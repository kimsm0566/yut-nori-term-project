package Yootgame.source.ui;

import Yootgame.source.Player;
import Yootgame.source.Yoot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePage extends JFrame {
    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JButton[][] boardButtons;
    private int windowSizeX = 1000;
    private int windowSizeY = 700;
    private int buttonSizeX = windowSizeX/20;
    private int buttonSizeY = buttonSizeX;
    private JLabel line;
    private JLabel resultLabel;
    private JLabel resultImageLabel;

    public GamePage() {
        setTitle("윷놀이");
        setSize(windowSizeX, windowSizeY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 메인 패널 설정
        mainPanel = new JPanel(new GridBagLayout());

        // 왼쪽 패널 설정
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        boardButtons = new JButton[3][21];

        // 보드를 포함할 패널
        JPanel boardPanel = new JPanel(null);
        boardPanel.setBackground(Color.WHITE);

        // 배경 이미지 설정
        ImageIcon backgroundImage = new ImageIcon("src/main/java/Yootgame/img/backgroundFicture.png");
        Image scaledImage = backgroundImage.getImage().getScaledInstance((int)(windowSizeX * 0.7), windowSizeY, Image.SCALE_SMOOTH);
        ImageIcon scaledBackgroundIcon = new ImageIcon(scaledImage);
        JLabel backgroundLabel = new JLabel(scaledBackgroundIcon);
        backgroundLabel.setBounds(0, 0, (int)(windowSizeX * 0.8), windowSizeY);


        // 선 이미지 추가
        line = new JLabel(new ImageIcon("src/main/java/Yootgame/img/line.png"));
        line.setBounds(84, 60, 430, 430);

        // 보드 그리기
        drawBoard();

        // 선 이미지를 마지막에 추가
        boardPanel.add(line);
        boardPanel.add(backgroundLabel);

    // 게임 로그 패널 생성
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setPreferredSize(new Dimension((int)(windowSizeX), 100)); // 가로 길이를 줄임
        logPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.white, 1),
                BorderFactory.createEmptyBorder(10, 70, 10, 70) // 안쪽 여백 추가
        ));
        logPanel.setBackground(Color.WHITE);


// 게임 로그 표시 영역
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false); // 편집 불가능하도록 설정
        logArea.setBackground(Color.white);
        JScrollPane scrollPane = new JScrollPane(logArea);
        logArea.append("레드팀이 도가 나왔습니다.\n");
        logArea.append("레드팀이 말을 이동했습니다.\n");
        logArea.append("블루팀의 차례입니다.\n");
        logArea.append("블루팀이 윷이 나왔습니다.\n");
        logArea.append("블루팀이 말을 이동했습니다.\n");

// 로그 패널에 스크롤 패널 추가
        logPanel.add(scrollPane, BorderLayout.CENTER);

// 왼쪽 패널에 보드와 로그 추가
        leftPanel.add(boardPanel, BorderLayout.CENTER);
        leftPanel.add(logPanel, BorderLayout.SOUTH);

        // 오른쪽 패널 생성
        rightPanel = createRightPanel();
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // 안쪽 여백 추가
        ));

        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.7;  // 왼쪽 패널 70%
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(leftPanel, gbc);

        gbc.weightx = 0.3;  // 오른쪽 패널 30%
        gbc.gridx = 1;
        mainPanel.add(rightPanel, gbc);

        add(mainPanel);
    }
    private void drawBoard() {
        int startX = 80;  // 보드 시작 X 좌표
        int startY = 60;  // 보드 시작 Y 좌표
        double buttonInterval = buttonSizeX * 1.25;

        // 메인 경로
        int xpos = startX + (buttonSizeX * 7);
        int ypos = startY + (buttonSizeY * 7);

        for(int i = 1; i < 21; i++) {
            if(i < 6) ypos -= buttonInterval;
            else if(i < 11) xpos -= buttonInterval;
            else if(i < 16) ypos += buttonInterval;
            else xpos += buttonInterval;

            if(i == 5 || i == 10 || i == 15) {
                boardButtons[0][i] = createBoardButton("src/main/java/Yootgame/img/bigcircle.jpg", xpos, ypos);
            } else if(i == 20) {
                boardButtons[0][i] = createBoardButton("src/main/java/Yootgame/img/startcircle.jpg", xpos, ypos);
            } else {
                boardButtons[0][i] = createBoardButton("src/main/java/Yootgame/img/circle.jpg", xpos, ypos);
            }
            leftPanel.add(boardButtons[0][i]);
        }

        // 대각선 경로
        // 왼쪽 대각선
        xpos = startX + (buttonSizeX * 7) - 10;
        ypos = startY + buttonSizeY - 10;

        for(int p = 0; p < 6; p++) {
            if(p != 0 && p != 3) {
                boardButtons[1][p] = createBoardButton("src/main/java/Yootgame/img/circle.jpg", xpos, ypos);
                leftPanel.add(boardButtons[1][p]);
            }
            xpos -= buttonSizeX;
            ypos += buttonSizeY;
        }

        // 오른쪽 대각선
        xpos = startX + buttonSizeX - 10;
        ypos = startY + buttonSizeY - 10;
        for(int p = 0; p < 6; p++) {
            if(p != 0) {
                String imagePath = (p == 3) ?
                        "src/main/java/Yootgame/img/bigcircle.jpg" :
                        "src/main/java/Yootgame/img/circle.jpg";
                boardButtons[2][p] = createBoardButton(imagePath, xpos, ypos);
                leftPanel.add(boardButtons[2][p]);
            }
            xpos += buttonSizeX;
            ypos += buttonSizeY;
        }
    }

    private JButton createBoardButton(String imagePath, int x, int y) {
        JButton btn = new JButton(new ImageIcon(imagePath));
        btn.setSize(buttonSizeX, buttonSizeY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setLocation(x, y);
        return btn;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // 현재 턴 표시 패널
        JPanel turnPanel = new JPanel();
        turnPanel.setBackground(Color.WHITE);
        turnPanel.setPreferredSize(new Dimension(400, 70));
        turnPanel.setMaximumSize(new Dimension(400, 60));
        turnPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("현재 턴"),
                BorderFactory.createEmptyBorder(0, 5, 3, 5)
        ));
        turnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel turnLabel = new JLabel("레드팀 차례입니다");
        turnLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        turnLabel.setForeground(Color.RED);
        turnPanel.add(turnLabel);

        // 플레이어 현황 패널
        JPanel playerStatusPanel = new JPanel();
        playerStatusPanel.setLayout(new BoxLayout(playerStatusPanel, BoxLayout.Y_AXIS));
        playerStatusPanel.setBackground(Color.white);
        playerStatusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String redStatus = "<남은 말:4 포인트:0>";
        String blueStatus = "<남은 말:4 포인트:0>";
        JLabel redTeamStatus = new JLabel(redStatus);
        JLabel blueTeamStatus = new JLabel(blueStatus);
        redTeamStatus.setForeground(Color.RED);
        blueTeamStatus.setForeground(Color.BLUE);
        redTeamStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        blueTeamStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        playerStatusPanel.add(Box.createVerticalStrut(30));
        playerStatusPanel.add(redTeamStatus);
        playerStatusPanel.add(Box.createVerticalStrut(10));
        playerStatusPanel.add(blueTeamStatus);

// 윷 결과 표시 영역
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(Color.white);
        resultPanel.setMinimumSize(new Dimension(180, 180));
        resultPanel.setPreferredSize(new Dimension(180, 180));
        resultPanel.setMaximumSize(new Dimension(200, 200));
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black, 0),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));


// 결과 이미지 레이블을 resultPanel에 추가
        resultImageLabel = new JLabel();
        resultPanel.add(resultImageLabel, BorderLayout.CENTER);


        // 결과와 타이머를 포함할 패널
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 50, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setMaximumSize(new Dimension(200, 50));

// 결과 텍스트 패널
        JPanel resultTextPanel = new JPanel();
        resultTextPanel.setBackground(Color.WHITE);
        resultTextPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        resultLabel = new JLabel("도");
        resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16)); // 폰트 크기 증가

// 타이머 패널
        JPanel timerPanel = new JPanel();
        timerPanel.setBackground(Color.WHITE);
        timerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JLabel timerLabel = new JLabel("30초");
        timerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16)); // 폰트 크기 증가
        resultTextPanel.add(resultLabel);
        timerPanel.add(timerLabel);

        // 타이머 설정
        final int[] timeLeft = {30};
        Timer countdownTimer = new Timer(1000, e -> {
            timeLeft[0]--;
            if (timeLeft[0] >= 0) {
                timerLabel.setText(timeLeft[0] + "초");
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
        countdownTimer.start();

        infoPanel.add(resultTextPanel);
        infoPanel.add(timerPanel);

        // 윷 던지기 버튼
        JButton throwButton = new JButton("윷 던지기");
        throwButton.setPreferredSize(new Dimension(300, 50));
        throwButton.setMaximumSize(new Dimension(300, 50));
        throwButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        throwButton.setBackground(new Color(255, 255, 255));
        throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 게이지 바 추가
        JProgressBar powerBar = new JProgressBar(0, 100);
        powerBar.setPreferredSize(new Dimension(150, 20));
        powerBar.setStringPainted(true);
        powerBar.setForeground(new Color(255, 200, 0));
        powerBar.setBackground(Color.WHITE);
        powerBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // 최종 파워 표시 레이블
        JLabel finalPowerLabel = new JLabel("파워: 0%");
        finalPowerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 게이지 증가를 위한 타이머
        Timer powerTimer = new Timer(50, null);
        final int[] power = {0};
        final boolean[] increasing = {true};

        throwButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 버튼을 누르면 타이머 시작
                powerTimer.addActionListener(evt -> {
                    if (increasing[0]) {
                        power[0] += 2;
                        if (power[0] >= 100) {
                            increasing[0] = false;
                        }
                    } else {
                        power[0] -= 2;
                        if (power[0] <= 0) {
                            increasing[0] = true;
                        }
                    }
                    powerBar.setValue(power[0]);
                    powerBar.setString(power[0] + "%");
                });
                powerTimer.start();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                powerTimer.stop();
                int result = Yoot.throwing();
                updateResult(result);

                power[0] = 0;
                powerBar.setValue(0);
                powerBar.setString("0%");
                increasing[0] = true;
            }
        });

// 패널에 추가
        panel.add(turnPanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(playerStatusPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(resultPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(powerBar);
        panel.add(Box.createVerticalStrut(20));
        panel.add(throwButton);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }
    private void updateResult(int result) {
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

        resultLabel.setText(resultText);
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage();
        Image newImg = image.getScaledInstance(190, 190, Image.SCALE_SMOOTH); // 패널 크기에 맞게 조정
        resultImageLabel.setIcon(new ImageIcon(newImg));
    }
}