package Yootgame.source.backend.multiroom;

public class RoomState {
    private Room currentRoom;
    private String nickname;
    private boolean hostReady;
    private boolean guestReady;
    private String hostName;
    private String guestName;
    private boolean isGameStarted;

    public RoomState(Room room, String nickname) {
        this.currentRoom = room;
        this.nickname = nickname;
        this.hostReady = false;
        this.guestReady = false;
        this.isGameStarted = false;

        if (room != null) {
            if (room.getClients().isEmpty() || room.getClientCount() == 1) {
                // 첫 번째 플레이어이거나 방이 비어있는 경우 방장으로 설정
                this.hostName = nickname;
                this.guestName = "대기 중...";
            } else {
                // 두 번째 플레이어인 경우
                this.guestName = nickname;
                // hostName은 updateHostName을 통해 설정됨
            }
        }
    }

    public boolean isRoomFull() {
        return hostName != null && guestName != null && !guestName.equals("대기 중...");
    }

    public boolean isPlayerInRoom(String playerNickname) {
        return playerNickname.equals(hostName) || playerNickname.equals(guestName);
    }

    public String getOtherPlayerName(String playerNickname) {
        if (playerNickname.equals(hostName)) {
            return guestName;
        } else if (playerNickname.equals(guestName)) {
            return hostName;
        }
        return null;
    }
    public void resetRoom() {
        hostReady = false;
        guestReady = false;
        isGameStarted = false;
        guestName = "대기 중...";
    }

    // Getters and Setters
    public Room getCurrentRoom() {
        return currentRoom;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isHostReady() {
        return hostReady;
    }

    public void setHostReady(boolean ready) {
        this.hostReady = ready;
    }

    public boolean isGuestReady() {
        return guestReady;
    }

    public void setGuestReady(boolean ready) {
        this.guestReady = ready;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    // 게임 상태 관리 메소드
    public void startGame() {
        isGameStarted = true;
    }

    public void endGame() {
        isGameStarted = false;
        resetReadyState();
    }

    public void resetReadyState() {
        hostReady = false;
        guestReady = false;
    }

    public boolean isPlayerHost() {
        return nickname.equals(hostName);
    }

    public boolean areBothPlayersReady() {
        return hostReady && guestReady;
    }

    public void updatePlayerJoined(String newPlayerNickname) {
        if (guestName == null || guestName.equals("대기 중...")) {
            guestName = newPlayerNickname;
        }
    }

    public void updatePlayerLeft(String leftPlayerNickname) {
        if (leftPlayerNickname.equals(guestName)) {
            guestName = "대기 중...";
            guestReady = false;
        }
    }
}