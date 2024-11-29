package Yootgame.source.ui;

import javax.swing.*;
import java.awt.*;

public class NicknameInputPage extends JFrame {
    private JTextField nicknameField;
    private boolean isConfirmed = false;
    private String nickname;

    public NicknameInputPage() {
        setTitle("닉네임 설정");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 안내 메시지
        JLabel messageLabel = new JLabel("닉네임을 입력해주세요");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        // 닉네임 입력 필드
        nicknameField = new JTextField(15);
        nicknameField.setMaximumSize(new Dimension(300, 30));
        nicknameField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        // 확인 버튼
        JButton confirmButton = new JButton("확인");
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.setMaximumSize(new Dimension(100, 30));
        confirmButton.addActionListener(e -> handleConfirm());

        // 컴포넌트 추가
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(nicknameField);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(confirmButton);

        add(mainPanel);
    }

    private void handleConfirm() {
        String input = nicknameField.getText().trim();
        if (isValidNickname(input)) {
            nickname = input;
            isConfirmed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "닉네임은 2-12자의 한글, 영문, 숫자만 가능합니다.",
                    "잘못된 닉네임",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isValidNickname(String nickname) {
        return nickname != null &&
                !nickname.isEmpty() &&
                nickname.length() >= 2 &&
                nickname.length() <= 12 &&
                nickname.matches("^[a-zA-Z0-9_가-힣]+$");
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }
}