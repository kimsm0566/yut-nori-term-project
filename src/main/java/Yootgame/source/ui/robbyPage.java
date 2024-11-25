package Yootgame.source.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class robbyPage extends JFrame {
    public robbyPage() {
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

        // 오른쪽 방 목록 패널
        JPanel roomListPanel = new JPanel(new BorderLayout());

        // 상단 제목
        JLabel titleLabel = new JLabel("로비", SwingConstants.CENTER);
        titleLabel.setOpaque(true); // 배경색이 보이도록 설정
        titleLabel.setBackground(new Color(255, 255, 255)); // 배경색 설정
        titleLabel.setForeground(new Color(60, 60, 60)); // 글자색 설정
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setPreferredSize(new Dimension(0, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // 여백 추가

        // 방 목록을 보여줄 스크롤 패널
        JPanel roomsPanel = new JPanel();
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));


        // 각 방을 개별 패널로 생성
        for(int i = 1; i <= 10; i++) {
            JPanel roomPanel = createRoomPanel("방 제목 " + i, i + "/4명");
            roomsPanel.add(roomPanel);
        }

        JScrollPane scrollPane = new JScrollPane(roomsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
//        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // 테두리 제거

        // 방 만들기 버튼
        JButton createRoomButton = new JButton("방 만들기");
        createRoomButton.setPreferredSize(new Dimension(0, 40));

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
        mainPanel.add(imagePanel, gbc);

        gbc.weightx = 0.33;
        gbc.gridx = 1;
        mainPanel.add(roomListPanel, gbc);

        add(mainPanel);
    }

    private JPanel createRoomPanel(String title, String players) {
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

        // 오른쪽 상단: 인원 수 (1:1)
        JLabel playersLabel = new JLabel("");
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
                JOptionPane.showMessageDialog(null,
                        title + "\n" +
                                "1:1 매치\n" +
                                "말 개수: 4개\n" +
                                "턴 시간: 30초");
            }
        });

        return panel;
    }
}