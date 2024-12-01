package Yootgame.source.backend.Client;

import Yootgame.source.backend.multiroom.Room;
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

    private void switchFrame(JFrame newFrame) {
        if (currentFrame != null) {
            currentFrame.dispose();  // 현재 프레임 닫기
        }
        currentFrame = newFrame;     // 새 프레임으로 교체
        currentFrame.setVisible(true);  // 새 프레임 표시
    }

    public GamePage switchToGame(Room room) {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        GamePage gamePage = new GamePage(room, client);  // Client 객체도 전달
        gamePage.setVisible(true);
        currentFrame = gamePage;
        return gamePage;
    }
    public JFrame getCurrentFrame() {
        return currentFrame;
    }
}