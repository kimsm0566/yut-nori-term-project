package Yootgame.source.component;

import javax.swing.*;
import java.awt.*;

public class GameLogPanel extends JPanel {
    private JTextArea logArea;
    private JScrollPane logScroll;

    public GameLogPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.white, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        initializeComponents();
    }

    private void initializeComponents() {
        // 게임 로그 텍스트 영역 생성
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.WHITE);
        logArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        // 스크롤 패널 생성
        logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(250, 150));
        logScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // 패널에 스크롤 패널 추가
        add(logScroll, BorderLayout.CENTER);
    }

    // 로그 추가 메소드
    public void addLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // 로그 초기화 메소드
    public void clearLog() {
        logArea.setText("");
    }

    // 로그 영역 활성화/비활성화
    public void setLogEnabled(boolean enabled) {
        logArea.setEnabled(enabled);
    }
}