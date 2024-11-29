package Yootgame.source.component;

import javax.swing.*;
import java.awt.*;
import Yootgame.source.ui.RoomPage;

public class ButtonPanel extends JPanel {
    private JButton exitButton;
    private JButton timeButton;
    private RoomPage roomPage;

    public ButtonPanel(RoomPage roomPage) {
        this.roomPage = roomPage;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setMaximumSize(new Dimension(250, 40));
        setBackground(Color.WHITE);

        exitButton = new JButton("나가기");
        exitButton.setPreferredSize(new Dimension(120, 40));
        exitButton.setMaximumSize(new Dimension(120, 40));
        exitButton.addActionListener(e -> handleExit());

        timeButton = new JButton("게임 시간");
        timeButton.setPreferredSize(new Dimension(120, 40));
        timeButton.setMaximumSize(new Dimension(120, 40));

        add(exitButton);
        add(Box.createHorizontalStrut(10));
        add(timeButton);
    }

    private void handleExit() {
        // 서버에 방 나가기 메시지 전송
        roomPage.getClient().sendMessage("/leave");
        roomPage.dispose();
    }
}