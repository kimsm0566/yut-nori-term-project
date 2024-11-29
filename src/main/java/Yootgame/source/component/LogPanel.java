package Yootgame.source.component;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private JTextArea logArea;

    public LogPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 200));
        setMaximumSize(new Dimension(250, 200));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.WHITE);
        logArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane);
    }

    public void addMessage(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}