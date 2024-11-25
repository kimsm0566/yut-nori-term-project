package Yootgame.source.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RoomPage extends JFrame {
    private JPanel rightPanel;
    private JPanel leftPanel;
    private int windowSizeX = 1000;
    private int windowSizeY = 700;
    public RoomPage() {
        setTitle("윷놀이");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 메인 패널
        JPanel mainPanel = new JPanel(new GridBagLayout());

        // 왼쪽 이미지 패널
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
        imagePanel.setPreferredSize(new Dimension((int)(windowSizeX * 0.75), windowSizeY));


        // 오른쪽 패널 생성
        rightPanel = createRightPanel();
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black, 1),
                BorderFactory.createEmptyBorder(5, 10, 10, 10) // 안쪽 여백 추가
        ));
        rightPanel.setPreferredSize(new Dimension((int)(windowSizeX * 0.25), windowSizeY));

        // 방 만들기 버튼
        JButton createRoomButton = new JButton("방 만들기");
        createRoomButton.setPreferredSize(new Dimension(0, 40));


        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(imagePanel, gbc);

        gbc.weightx = 0.2;
        gbc.gridx = 1;
        mainPanel.add(rightPanel, gbc);

        add(mainPanel);
    }
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 방 제목 패널
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.white);
        titlePanel.setPreferredSize(new Dimension(250, 40));
        titlePanel.setMaximumSize(new Dimension(250, 40));
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
        JLabel titleLabel = new JLabel("방 제목");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        titlePanel.add(titleLabel);

// 방장 패널
        JPanel hostPanel = new JPanel(null); // absolute positioning 사용
        hostPanel.setBackground(Color.white);
        hostPanel.setPreferredSize(new Dimension(250, 80));
        hostPanel.setMaximumSize(new Dimension(250, 80));
        hostPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel hostLabel = new JLabel(" 방장 닉네임");
        hostLabel.setBounds(10, 10, 100, 20); // x, y, width, height

        JButton hostReadyButton = new JButton("준비");
        hostReadyButton.setBounds(170, 30, 70, 20); // 오른쪽 아래에 위치하도록 조정
        hostReadyButton.setBackground(Color.WHITE);

        hostPanel.add(hostLabel);
        hostPanel.add(hostReadyButton);

// 상대방 패널도 동일하게 수정
        JPanel guestPanel = new JPanel(null);
        guestPanel.setBackground(Color.white);
        guestPanel.setPreferredSize(new Dimension(250, 80));
        guestPanel.setMaximumSize(new Dimension(250, 80));
        guestPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JLabel guestLabel = new JLabel(" 상대 닉네임");
        guestLabel.setBounds(10, 10, 100, 20);

        JButton guestReadyButton = new JButton("준비");
        guestReadyButton.setBounds(170, 30, 70, 20);
        guestReadyButton.setBackground(Color.WHITE);

        guestPanel.add(guestLabel);
        guestPanel.add(guestReadyButton);

        // 방 설정 패널
        JPanel settingPanel = new JPanel();
        settingPanel.setBackground(Color.WHITE);
        settingPanel.setPreferredSize(new Dimension(250, 100));
        settingPanel.setMaximumSize(new Dimension(250, 100));
        settingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

// 방 설정 정보 표시
        JLabel settingLabel = new JLabel("<html>말 개수 : ?<br>제한 시간: 30초</html>");
        settingLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        settingPanel.add(settingLabel);

// 카운트다운 패널을 로그창으로 변경
        JPanel countdownPanel = new JPanel(new BorderLayout());
        countdownPanel.setBackground(Color.WHITE);
        countdownPanel.setPreferredSize(new Dimension(250, 200)); // 100에서 200으로 증가
        countdownPanel.setMaximumSize(new Dimension(250, 200));   // 100에서 200으로 증가
        countdownPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

// 로그 영역 생성
        JTextArea countdownLog = new JTextArea();
        countdownLog.setEditable(false);
        countdownLog.setBackground(Color.WHITE);
        countdownLog.setFont(new Font("맑은 고딕", Font.PLAIN, 12)); // 폰트 설정 추가
        JScrollPane scrollPane = new JScrollPane(countdownLog);

        countdownPanel.add(scrollPane);

        countdownPanel.add(scrollPane);

// 준비 상태 변수와 리스너 수정
        final boolean[] hostReady = {false};
        final boolean[] guestReady = {false};

        hostReadyButton.addActionListener(e -> {
            hostReady[0] = !hostReady[0];
            hostReadyButton.setText(hostReady[0] ? "준비 완료" : "준비");
            if (hostReady[0]) {
                hostPanel.setBackground(new Color(144, 238, 144)); // 초록색으로 변경
                countdownLog.append("방장이 준비를 완료했습니다.\n");
            } else {
                hostPanel.setBackground(Color.LIGHT_GRAY); // 준비 해제시 원래 색으로
            }
            checkBothReady(hostReady[0], guestReady[0], countdownLog);
        });

        guestReadyButton.addActionListener(e -> {
            guestReady[0] = !guestReady[0];
            guestReadyButton.setText(guestReady[0] ? "준비 완료" : "준비");
            if (guestReady[0]) {
                guestPanel.setBackground(new Color(144, 238, 144)); // 초록색으로 변경
                countdownLog.append("상대방이 준비를 완료했습니다.\n");
            } else {
                guestPanel.setBackground(Color.LIGHT_GRAY); // 준비 해제시 원래 색으로
            }
            checkBothReady(hostReady[0], guestReady[0], countdownLog);
        });



        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setMaximumSize(new Dimension(250, 40));
        buttonPanel.setBackground(Color.WHITE);

        JButton exitButton = new JButton("나가기");
        exitButton.setPreferredSize(new Dimension(120, 40));
        exitButton.setMaximumSize(new Dimension(120, 40));

        JButton timeButton = new JButton("게임 시간");
        timeButton.setPreferredSize(new Dimension(120, 40));
        timeButton.setMaximumSize(new Dimension(120, 40));

        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(timeButton);

// 컴포넌트 추가 순서 수정
        panel.add(titlePanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(hostPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(guestPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(settingPanel);      // 방 설정 패널 추가
        panel.add(Box.createVerticalStrut(20));
        panel.add(countdownPanel);
        panel.add(Box.createVerticalGlue());
        panel.add(buttonPanel);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    // 카운트다운 체크 메소드 수정
    private void checkBothReady(boolean hostReady, boolean guestReady, JTextArea log) {
        if (hostReady && guestReady) {
            Timer timer = new Timer(1000, new ActionListener() {
                int count = 5;
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (count > 0) {
                        log.append(count + "초 후에 게임이 시작됩니다.\n");
                        count--;
                    } else {
                        ((Timer)e.getSource()).stop();
                        log.append("게임이 시작되었습니다!\n");
                        // 게임 시작 로직 추가
                    }
                }
            });
            timer.start();
        }
    }}