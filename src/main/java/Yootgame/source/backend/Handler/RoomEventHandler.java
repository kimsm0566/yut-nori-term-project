package Yootgame.source.backend.Handler;

import Yootgame.source.ui.RoomPage;
import Yootgame.source.backend.multiroom.RoomState;
import javax.swing.*;
import java.awt.*;

public class RoomEventHandler {
    private RoomState state;
    private RoomPage roomPage;
    private boolean hostReady = false;
    private boolean guestReady = false;

    public RoomEventHandler(RoomState state, RoomPage roomPage) {
        this.state = state;
        this.roomPage = roomPage;
    }

    public void handleHostReady() {
        hostReady = !hostReady;
        updateHostReadyButton(hostReady);
        checkBothReady();
    }

    public void handleGuestReady() {
        guestReady = !guestReady;
        updateGuestReadyButton(guestReady);
        checkBothReady();
    }

    private void updateHostReadyButton(boolean ready) {
        JButton hostReadyButton = roomPage.getHostReadyButton();
        hostReadyButton.setText(ready ? "준비 완료" : "준비");
        hostReadyButton.setBackground(ready ? Color.GREEN : Color.WHITE);
        roomPage.addLogMessage(state.getHostName() + (ready ? "님이 준비했습니다." : "님이 준비를 취소했습니다."));
    }

    private void updateGuestReadyButton(boolean ready) {
        JButton guestReadyButton = roomPage.getGuestReadyButton();
        guestReadyButton.setText(ready ? "준비 완료" : "준비");
        guestReadyButton.setBackground(ready ? Color.GREEN : Color.WHITE);
        roomPage.addLogMessage(state.getGuestName() + (ready ? "님이 준비했습니다." : "님이 준비를 취소했습니다."));
    }

    private void checkBothReady() {
        if (hostReady && guestReady) {
            startGameCountdown();
        }
    }

    private void startGameCountdown() {
        if (state.getGuestName().equals("대기 중...")) {
            roomPage.addLogMessage("상대방이 없어 게임을 시작할 수 없습니다.");
            resetReadyState();
            return;
        }

        final int[] count = {5};
        Timer timer = new Timer(1000, e -> {
            if (!hostReady || !guestReady) {
                ((Timer)e.getSource()).stop();
                roomPage.addLogMessage("준비 상태가 취소되어 게임 시작이 중단되었습니다.");
                return;
            }

            if (count[0] > 0) {
                roomPage.addLogMessage(count[0] + "초 후에 게임이 시작됩니다.");
                count[0]--;
            } else {
                ((Timer)e.getSource()).stop();
                roomPage.addLogMessage("게임이 시작되었습니다!");
                startGame();
            }
        });
        timer.start();
    }

    private void startGame() {
        state.startGame();
        // 서버에 게임 시작 알림
        // Client.sendMessage("/gameStart") 등의 메시지 전송 필요
    }

    public void handleGameStart() {
        roomPage.addLogMessage("게임이 시작되었습니다!");
        // 게임 화면으로 전환하는 로직 추가 필요
    }

    public void resetReadyState() {
        hostReady = false;
        guestReady = false;
        updateHostReadyButton(false);
        updateGuestReadyButton(false);
    }

    public boolean isHostReady() {
        return hostReady;
    }

    public boolean isGuestReady() {
        return guestReady;
    }
    public void handleReadyMessage(boolean isHost, boolean ready) {
        if (isHost) {
            hostReady = ready;
            updateHostReadyButton(ready);
        } else {
            guestReady = ready;
            updateGuestReadyButton(ready);
        }
        checkBothReady();
    }

}