package Yootgame.source.ui;

import Yootgame.source.backend.Client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RoomConfigPage extends JFrame {
    private JTextField roomNameField;
    private JSpinner pieceCountSpinner;
    private JSpinner turnTimeSpinner;
    private Client client;  // Client 객체 추가

    public RoomConfigPage(JFrame parentFrame, Client client) {  // 생성자 수정
        this.client = client;
        setTitle("방 설정");
        setSize(1000, 700);
        setLocationRelativeTo(parentFrame);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 방 제목 설정
        JPanel namePanel = createSettingPanel("방 제목");
        roomNameField = new JTextField(15);
        namePanel.add(roomNameField);

        // 말 개수 설정
        JPanel piecePanel = createSettingPanel("말 개수");
        SpinnerModel pieceModel = new SpinnerNumberModel(4, 2, 4, 1);
        pieceCountSpinner = new JSpinner(pieceModel);
        piecePanel.add(pieceCountSpinner);

        // 턴 시간 설정
        JPanel timePanel = createSettingPanel("턴 시간 (초)");
        SpinnerModel timeModel = new SpinnerNumberModel(30, 10, 60, 5);
        turnTimeSpinner = new JSpinner(timeModel);
        timePanel.add(turnTimeSpinner);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton createButton = new JButton("방 만들기");
        JButton cancelButton = new JButton("취소");

        createButton.addActionListener(e -> {
            if (validateInput()) {
                createRoom();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        // 패널 추가
        mainPanel.add(namePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(piecePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(timePanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private JPanel createSettingPanel(String labelText) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setMaximumSize(new Dimension(350, 40));

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, 25));
        panel.add(label);

        return panel;
    }

    private boolean validateInput() {
        String roomName = roomNameField.getText().trim();
        if (roomName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "방 제목을 입력해주세요.");
            return false;
        }
        return true;
    }
    private void createRoom() {
        String roomName = roomNameField.getText().trim();
        int pieceCount = (Integer) pieceCountSpinner.getValue();
        int turnTime = (Integer) turnTimeSpinner.getValue();

        client.sendMessage("/create " + roomName + " " + turnTime + " " + pieceCount);
        // 창은 서버 응답을 받은 후에 Client의 handleServerResponse에서 처리
        setVisible(false);
    }

}