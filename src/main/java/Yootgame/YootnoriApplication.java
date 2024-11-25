package Yootgame;

import Yootgame.source.FirstPage;
import Yootgame.source.ui.GamePage;
import Yootgame.source.ui.RoomPage;
import Yootgame.source.ui.robbyPage;

import javax.swing.*;

public class YootnoriApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RoomPage().setVisible(true);
        });
    }
}