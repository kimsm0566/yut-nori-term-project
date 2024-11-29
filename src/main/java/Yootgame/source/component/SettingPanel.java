package Yootgame.source.component;

import javax.swing.*;
import java.awt.*;
import Yootgame.source.backend.multiroom.Room;

public class SettingPanel extends JPanel {
    private JLabel titleLabel;
    private JLabel settingLabel;

    public SettingPanel(Room room) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 100));
        setMaximumSize(new Dimension(250, 100));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // 제목 레이블
        titleLabel = new JLabel("방 설정");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 설정 정보 레이블
        settingLabel = new JLabel();
        settingLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        settingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingLabel.setHorizontalAlignment(SwingConstants.CENTER);  // 수평 가운데 정렬 추가

        // 여백 추가
        add(Box.createVerticalStrut(10));
        add(titleLabel);
        add(Box.createVerticalStrut(10));
        add(settingLabel);
        add(Box.createVerticalStrut(10));

        updateSettings(room);
    }

    public void updateSettings(Room room) {
        if (room != null) {
            String info = String.format("<html><div style='text-align: center;'>말 개수: %d개<br>턴 시간: %d초</div></html>",
                    room.getNumberOfPiece(),
                    room.getTurnTime());
            settingLabel.setText(info);
            revalidate();
            repaint();
        }
    }
}