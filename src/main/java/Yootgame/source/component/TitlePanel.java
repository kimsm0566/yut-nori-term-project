package Yootgame.source.component;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
    private JLabel titleLabel;

    public TitlePanel() {
        setBackground(Color.white);
        setPreferredSize(new Dimension(250, 40));
        setMaximumSize(new Dimension(250, 40));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));

        titleLabel = new JLabel("방 제목");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        add(titleLabel);
    }

    public void updateTitle(String title) {
        titleLabel.setText(title);
    }
}