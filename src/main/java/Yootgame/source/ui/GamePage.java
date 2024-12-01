package Yootgame.source.ui;

import Yootgame.source.backend.Client.Client;
import Yootgame.source.backend.gamelogic.PlayGame;
import Yootgame.source.backend.gamelogic.YotBoard;
import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.component.*;

import javax.swing.*;
import java.awt.*;

public class GamePage extends JFrame {
    private YotBoard board;
    private PlayGame playGame;
    private final int windowSizeX = 1000;
    private final int windowSizeY = 700;
    private Room currentRoom;
    private Client client;

    public GamePage(Room room, Client client) {  // Client 매개변수 추가
        this.currentRoom = room;
        this.client = client;
        initializeFrame();
        createLayout();
    }

    private void initializeFrame() {
        setTitle("윷놀이");
        setSize(windowSizeX, windowSizeY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void createLayout() {
        // PlayGame 객체 생성 시 Room 객체도 전달
        playGame = new PlayGame(2, currentRoom.getNumberOfPiece(), client, currentRoom);

        // YotBoard는 PlayGame 내부에서 생성됨
        // board에 대한 참조 얻기
        board = playGame.getBoard();

        add(board.getPanelPan());  // YotBoard의 패널을 GamePage에 추가
    }

    // 기존 메소드들은 YotBoard의 메소드를 호출하도록 수정
    public void updateCurrentPlayer(String nickname, boolean isHost) {
        board.changePlayer(nickname, isHost);
    }

    public void updateYutResult(int result) {
        board.printResult(result);
    }

    public void updatePiecePosition(String player, int x, int y, int num) {
        board.printPiece(player.equals("red") ? 0 : 1, x, y, num);
    }
}