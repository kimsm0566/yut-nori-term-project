package Yootgame.source.backend.Server;

import Yootgame.source.backend.gamelogic.PlayGame;
import Yootgame.source.backend.Handler.RoomConnectionHandler;
import Yootgame.source.backend.multiroom.Room;

/**
 * ServerSideMovePeice 클래스는 서버에서 말 이동 업데이트를 처리하고
 * 해당 정보를 방의 모든 클라이언트에게 전송하는 역할을 담당합니다.
 */
public class ServerSideMovePeice {
    private Room currentRoom;
    private PlayGame playGame;  // 게임 로직 관리

    public ServerSideMovePeice(Room room, int playerCount, int pieceCount) {
        this.currentRoom = room;
        // 서버 측에서는 Client가 필요 없으므로 null 전달
        this.playGame = new PlayGame(playerCount, pieceCount, null, room);
    }

    // 클라이언트로부터 말 이동 요청을 받아 처리
    public void handleMoveRequest(String player, int x, int y) {
        // PlayGame의 phaze3Pieceact 호출
        playGame.phaze3Pieceact(x, y);

        // 이동 결과를 모든 클라이언트에게 전송
        String updateMessage = "MOVE_UPDATE " + player + " " + x + " " + y;
        for (RoomConnectionHandler client : currentRoom.getClients()) {
            client.sendMessage(updateMessage);
        }
    }

    // 윷 던지기 요청 처리
    public void handleThrowRequest(String player) {
        playGame.phaze1ThrowYot();

        // 결과를 모든 클라이언트에게 전송
        String resultMessage = "THROW_RESULT " + player + " " + playGame.result;
        for (RoomConnectionHandler client : currentRoom.getClients()) {
            client.sendMessage(resultMessage);
        }
    }
}