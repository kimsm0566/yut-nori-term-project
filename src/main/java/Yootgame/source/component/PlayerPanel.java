package Yootgame.source.component;

import Yootgame.source.backend.Client.Client;

import javax.swing.*;
import java.awt.*;

public class PlayerPanel extends JPanel {
    private JLabel nameLabel;
    private JButton readyButton;
    private boolean isReady = false;
    private String nickname;
    private Client client;  // Client 참조 추가


    public PlayerPanel(boolean isHost, String nickname, Client client) {  // Client 매개변수 추가
        this.nickname = nickname;
        this.client = client;
        setLayout(null);
        setBackground(Color.white);
        setPreferredSize(new Dimension(250, 80));
        setMaximumSize(new Dimension(250, 80));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        nameLabel = new JLabel(nickname);
        nameLabel.setBounds(20, 30, 150, 20);

        readyButton = new JButton("준비");
        readyButton.setBounds(160, 30, 70, 20);
        readyButton.setBackground(Color.WHITE);
        readyButton.addActionListener(e -> handleReady());
        readyButton.setEnabled(!isHost);

        add(nameLabel);
        add(readyButton);
    }
    public void setReadyState(boolean ready) {
        this.isReady = ready;
        readyButton.setText(ready ? "준비 완료" : "준비");
        readyButton.setBackground(ready ? Color.GREEN : Color.WHITE);
    }



    private void handleReady() {
        isReady = !isReady;
        readyButton.setText(isReady ? "준비 완료" : "준비");
        readyButton.setBackground(isReady ? Color.GREEN : Color.WHITE);
        // 서버로 준비 상태 전송
        client.sendMessage("/ready " + nickname + " " + isReady);
    }

    public void setPlayerName(String name) {
        nameLabel.setText(name);
    }

    public void setReadyButtonEnabled(boolean enabled) {
        readyButton.setEnabled(enabled);
    }

    public boolean isPlayerReady() {
        return isReady;
    }

    public JButton getReadyButton() {
        return readyButton;
    }
    public String getPlayerName() {
        return nameLabel.getText();
    }

    public void reset() {
        isReady = false;
        readyButton.setText("준비");
        readyButton.setBackground(Color.WHITE);
        nameLabel.setText("대기 중...");
    }
}