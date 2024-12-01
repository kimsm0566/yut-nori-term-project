package Yootgame.source.backend.gamelogic;

import Yootgame.source.backend.gamelogic.*;
import Yootgame.source.backend.gamelogic.Yoot;
import Yootgame.source.backend.Client.Client;
import Yootgame.source.backend.multiroom.Room;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayGame implements ActionListener{
	private Player[]players;
	private YotBoard board;
	private int pieceNum;
	private int playerNum;
	private int turn=0;
	private int winner=-1;
	public JButton[] testButton;
	public int result=0;
	private Player nowPlayer;
	private int control=1;
	private Client client;  // Client 필드 추가
	private Room currentRoom;  // Room 필드 추가

	public PlayGame(int people, int mal, Client client, Room room) {  // Room 매개변수 추가
		this.client = client;
		this.currentRoom = room;
		players = new Player[people];
		for(int i=0;i<people;i++) {
			players[i] = new Player(i, mal, client);  // Client 객체 전달
		}
		playerNum = people;
		pieceNum = mal;

		// YotBoard 생성 시 Room 객체도 전달
		board = new YotBoard(this, client, room);
		client.getMessageHandler().setBoard(board);

		for(int i=0; i<playerNum; i++) {
			board.setplayerInfo(i, players[i].playerPiece());
		}
	}
	public int getPlayerNum() {

		return playerNum;
	}
	public YotBoard getBoard() {
		return board;
	}

	int checkFinish()
	{
		for(int i=0;i<players.length;i++)
		{
			if(players[i].getPoint() == pieceNum)
			{
				players = null;
				board.finishMessage(i);
				return i;//i번째 플레이어 승리
			}
		}
		return -1;//게임 아직 안끝남
	}
	int checkCatch(int index)//지금 플레이어 인덱스값
	{
		Player catcher = players[index];//catcher는 지금의 플레이어
		int posx,posy;
		for(int q=0;q<catcher.getPieces().size();q++)//현재 플레이어의 모든 말
		{
			posx = catcher.getPieces().get(q).getX();
			posy = catcher.getPieces().get(q).getY();//현재 플레이어의 q번째 말 위치
			for(int i=0;i<players.length;i++)
			{
				if(i!=index)
				{
					if(players[i].checkCatch(posx,posy)==1)//i번째 Player의 말들과 비교해서 같으면 없앰
					{
						board.message("P"+index+"가 P"+i+"의 말을 잡았다");
						return 1;//한칸에 서로 다른 플레이어의 말이 겹쳐있지 않으므로 그냥 만나자 마자 종료
					}
				}
			}
		}
		return 0;
	}
	public void boardMessage(String s) {
		board.message(s);  // 직접 YotBoard의 logArea에 메시지 추가
	}

	void boardRefreashFrame() {
		board.refreashFrame();
	}

	public void phaze1ThrowYot() {
		if(control==1) {
			result = Yoot.throwing();
			board.printResult(result);

			// 현재 플레이어 정보와 함께 결과 전송
			boolean isHost = (turn == 0);
			String nickname = isHost ? currentRoom.getHostNickname() : currentRoom.getGuestNickname();
			client.sendMessage("/yut_result " + turn + " " + result + " " + nickname);

			boardMessage("Player " + nickname + "의 차례입니다");
			control = 3;
		}
	}

	public void phaze2PutOnBoard() {
		if(control==2) {
			if(nowPlayer.createPiece()==1) {
				nowPlayer.move(0, 0, result);

				// 말 생성 및 이동 정보 전송
				client.sendMessage("/create_piece " + turn + " " + result);

				for(int i=0; i<playerNum; i++) {
					board.setplayerInfo(i, players[i].playerPiece());
				}
				board.printPiece(turn,0,result,nowPlayer.getPieceUpdaNum(0,result));
				phaze2changeBtncolor();

				if(checkCatch(turn)==1 || result == 4 || result ==5) {
					control=1;
					phaze1ThrowYot();
				} else {
					phaze4NextTurn();
				}
			} else {
				boardMessage("더 이상 말을 생성 할 수 없습니다.");
				control=3;
			}
		}
	}

	public void phaze3Pieceact(int posx, int posy) {
		int index;
		int x, y, point;

		if(control==3) {
			// 판 위에 말이 없고 대기중인 말이 있다면 0,0에 새로 만들고
			if(nowPlayer.getPieces().size()==0 && nowPlayer.getPieceNum()>0) {
				control=2;
				phaze2PutOnBoard();
			} else {
				// 해당 버튼에 말이 있는지 확인, 있으면 말 배열에 인덱스 반환
				index = nowPlayer.checkEnable(posx, posy);
				if(index!=-1) {
					//말이 있으면 Player에서 알아서 찾고 도개걸 결과로 이동함
					board.printPiece(4, posx, posy, 0);//가기전에 흰색으로 원상 복구 후 이동

					if(nowPlayer.move(posx, posy, result)==1) //여기서 알아서 업어가는지 판단해줌
					{//들어가거나 겹쳐졌을때 화면에 표시를 안한다 이것때문에 자꾸 오류가 난다.
						boardMessage("P "+turn+" 말 하나가 업혔습니다");

						// 말 위치 조정
						if(posx == 0 && posy == 5) {
							posx = 1;
							posy = 0;
						} else if(posx == 0 && posy==10){
							posx=2;
							posy=0;
						}
						posy=posy+result;

						if(result > 0) {
							if(posx == 1 && posy==3) {
								posx=2;
								posy=3;
							} else if(posx==1 && posy>5){
								posx=0;
								posy+=9;
							} else if(posx==2 && posy>5) {
								posx=0;
								posy+=14;
							}
						} else {
							if(posx==1 && posy<1) {
								posx=0;
								posy=5+posy;
							} else if(posx==1 && posy==3) {
								posx=2;
								posy=3;
							} else if(posx==2 && posy<1) {
								posx=0;
								posy=10+posy;
							} else if(posx==0 && posy<1) {
								posy=20+posy;
							}
						}

						index = nowPlayer.checkEnable(posx, posy);
						x = nowPlayer.getPieces().get(index).getX();
						y = nowPlayer.getPieces().get(index).getY();
						point = nowPlayer.getPieces().get(index).getPoint();
						board.printPiece(turn,x,y,point);

						// 말 업기 상태를 서버에 전송
						client.sendMessage("/move_piece " + turn + " " + x + " " + y + " " + point + " up");

					} else if(nowPlayer.checkPiecein() ==1) {
						boardMessage("P "+turn+" 말 하나가 골인했습니다");
						// 골인 상태를 서버에 전송
						client.sendMessage("/piece_goal " + turn);
					} else {
						x = nowPlayer.getPieces().get(index).getX();
						y = nowPlayer.getPieces().get(index).getY();
						point = nowPlayer.getPieces().get(index).getPoint();
						board.printPiece(turn,x,y,point);//플레이어, 이동 이후 좌표

						// 일반 이동 상태를 서버에 전송
						client.sendMessage("/move_piece " + turn + " " + x + " " + y + " " + point);
					}

					for(int i=0; i<playerNum; i++) {
						board.setplayerInfo(i, players[i].playerPiece());
						// 플레이어 상태 정보 전송
						client.sendMessage("/player_info " + i + " " + players[i].playerPiece());
					}

					phaze2changeBtncolor();//UI 버튼 색깔 변경

					if(nowPlayer.getPieceNum()<=0 && nowPlayer.getPieces().size()<=0) {//대기중인 말과 판위에 말이 없으면
						control=-1;//경기 종료
						System.out.println("경기 종료");
						phaze4NextTurn();//해당 플레이어 턴 종료
					} else {//게임이 끝나지 않았다면 (이렇게 해놔야 자바 익셉션 안뜸)
						if(checkCatch(turn)==1 || result == 4 || result ==5) {//다시 윷 던지기 조건
							control=1;
							phaze1ThrowYot();
						} else {
							phaze4NextTurn();//해당 플레이어 턴 종료
						}
					}
				} else {
					boardMessage("엉뚱한 버튼 클릭함"+posx +" , "+posy);
				}
			}
		}
	}

	private void phaze4NextTurn() {
		winner = checkFinish();
		if(winner != -1) {
			control = 5;
			System.out.println(winner + " 번째 플레이어가 승리하였습니다.");
			// 승리 정보 전송
			client.sendMessage("/game_win " + winner);
		} else {
			turn++;
			if(turn >= playerNum) {
				turn = 0;
			}

			// 턴 변경 정보 전송
			boolean isNextPlayerHost = (turn == 0);
			String nextPlayerNickname = isNextPlayerHost ?
					currentRoom.getHostNickname() : currentRoom.getGuestNickname();
			client.sendMessage("/change_turn " + turn + " " + nextPlayerNickname);

			boardMessage("P " + turn + " 차례");
			boardRefreashFrame();
			initBtncolor();
			control = 1;
		}
	}


	void phaze1changeBtncolor() {
		board.buttonColor("throwBtnOFF");
		if(nowPlayer.getPieceNum()>0)//대기중인 말이 있다면
		{
			board.buttonColor("newPieceBtnON");//새로운 말 버튼 활성화
		}
		if(nowPlayer.getPieces().size()>0)//판에 말이 있다면
		{
			board.buttonColor("clickBoardON");//판 클릭 버튼 활성화
		}
	}

	void phaze2changeBtncolor() {
		if(nowPlayer.getPieces().size()>0)
		{
			board.buttonColor("clickBoardON");
		}
		else
		{
			board.buttonColor("clickBoardOFF");
		}
		if(nowPlayer.getPieceNum()>0)
		{
			board.buttonColor("newPieceBtnON");
		}
		else
		{
			board.buttonColor("newPieceBtnOFF");
		}
	}

	void initBtncolor() {
		board.buttonColor("throwBtnON");
		board.buttonColor("newPieceBtnOFF");
		board.buttonColor("clickBoardOFF");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Action source: " + e.getSource());
		System.out.println("Control value: " + control);

		if(e.getSource() == board.getThrowButton() && control == 1) {
			System.out.println("Throwing yut...");
			phaze1ThrowYot();
		}

		if(e.getSource() == board.getNewPieceButton() && control == 3) {
			control=2;
			phaze2PutOnBoard();
		}
		if(control==3) {
			for(int i=1;i<21;i++) {
				if(e.getSource()==board.panButton[0][i]) {
					phaze3Pieceact(0,i);
				}
			}
			for(int p=1;p<6;p++) {
				if(e.getSource()==board.panButton[1][p]) {
					phaze3Pieceact(1,p);
				}
			}
			for(int q=1;q<6;q++) {
				if(e.getSource()==board.panButton[2][q]) {
					phaze3Pieceact(2,q);
				}
			}
		}
		for(int r=0;r<6;r++) {
			if(e.getSource()==board.getTestButtons()[r] && control == 1) {
				phaze1ThrowYot();
				r--;
				if(r==0) {
					result = 5;
				} else {
					result = r;
				}
				board.printResult(result);
			}
		}
	}
}
