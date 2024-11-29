package Yootgame.source.backend.Client;

import Yootgame.source.ui.GamePage;
import Yootgame.source.ui.RoomPage;
import Yootgame.source.ui.robbyPage;

import javax.swing.*;

public class UIManager {
    private JFrame currentFrame;
    private final Client client;

    public UIManager(Client client) {
        this.client = client;
    }

    public void switchToLobby() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new robbyPage(client);
        currentFrame.setVisible(true);
        client.getConnectionManager().sendMessage("/list");
    }

    public void switchToRoomPage(boolean isHost) {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new RoomPage(client.getCurrentRoom(), client.getNickname(), isHost, client);
        currentFrame.setVisible(true);
    }

    public void switchToGame() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new GamePage();
        currentFrame.setVisible(true);
    }

    public JFrame getCurrentFrame() {
        return currentFrame;
    }
}