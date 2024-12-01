package Yootgame.source.backend.gamelogic;

import Yootgame.source.backend.Client.Client;
import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.component.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class YotBoard {
	private final JPanel panelPan;  // 메인 패널
	private final JPanel boardPanel;  // 왼쪽 보드 패널
	private final InfoPanel infoPanel;   // 오른쪽 정보 패널
	public JButton[][] panButton;
	private PlayGame play;
	private final int windowSizeX = 1000;
    private final int buttonSizeX = windowSizeX/20;
	private final int buttonSizeY = buttonSizeX;
	private Client client;
	private JLabel line;
	private JTextArea logArea;  // 게임 로그 영역
	private JScrollPane logScrollPane;  // 로그 스크롤
	private static final int LOG_AREA_ROWS = 5;
	private static final int LOG_AREA_COLS = 40;
	private static final int BOARD_SIZE = 21;

	public YotBoard(PlayGame playObject, Client client, Room room) {
		this.client = client;
		this.play = playObject;

		// 메인 패널을 BorderLayout으로 설정
		panelPan = new JPanel(new BorderLayout());
		panelPan.setBackground(Color.WHITE);

		// 왼쪽 보드 패널 생성
		boardPanel = new JPanel(new BorderLayout());
        int windowSizeY = 700;
        boardPanel.setPreferredSize(new Dimension((int)(windowSizeX * 0.7), windowSizeY));
		boardPanel.setBackground(Color.WHITE);

		// 게임 보드를 담을 패널
		JPanel gamePanel = new JPanel(null);
		gamePanel.setBackground(Color.WHITE);

		// 윷놀이 보드 초기화
		panButton = new JButton[3][21];

		// 보드 경로 생성
		createMainPath();
		createDiagonalPaths();

		// 선 이미지 추가
		line = new JLabel(new ImageIcon("src/main/java/Yootgame/img/line.png"));
		line.setBounds(150, 55, 430, 430);
		gamePanel.add(line);

		// 로그 영역 생성
		logArea = new JTextArea(5, 40);
		logArea.setEditable(false);
		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		logScrollPane = new JScrollPane(logArea);
		gamePanel.add(logArea);

		// 게임 패널과 로그 영역 배치
		boardPanel.add(gamePanel, BorderLayout.CENTER);

		// 오른쪽 InfoPanel 생성
// PlayGame 객체를 ActionListener로 전달
		infoPanel = new InfoPanel(room, playObject);  // playObject가 ActionListener
		// 패널들을 메인 패널에 추가
		panelPan.add(boardPanel, BorderLayout.WEST);
		panelPan.add(infoPanel, BorderLayout.EAST);
	}


	private JButton createBoardBtn(JButton btn, int x, int y, int width, int depth, boolean tf) {
		btn.setSize(width, depth);
		btn.setBorderPainted(tf);
		btn.setContentAreaFilled(tf);
		btn.setLocation(x, y);
		return btn;
	}


	private void createMainPath() {
		int xpos = buttonSizeX * 10;
		int ypos = buttonSizeY * 8;
		double buttonInterval = buttonSizeX * 1.25;

		for(int i = 1; i < 21; i++) {
			if(i < 6) ypos -= buttonInterval;
			else if(i < 11) xpos -= buttonInterval;
			else if(i < 16) ypos += buttonInterval;
			else xpos += buttonInterval;

			if(i == 5 || i == 10 || i == 15) {
				panButton[0][i] = new JButton(new ImageIcon("src/main/java/Yootgame/img/bigcircle.jpg"));
			} else if(i == 20) {
				panButton[0][i] = new JButton(new ImageIcon("src/main/java/Yootgame/img/startcircle.jpg"));
			} else {
				panButton[0][i] = new JButton(new ImageIcon("src/main/java/Yootgame/img/circle.jpg"));
			}
			panButton[0][i] = createBoardBtn(panButton[0][i], xpos, ypos, buttonSizeX, buttonSizeY, false);
			boardPanel.add(panButton[0][i]);
			panButton[0][i].addActionListener(play);
		}
	}

	private void createDiagonalPaths() {
		// 왼쪽 대각선
		int ypos = 145;
		int xpos = 445;
		for(int p = 0; p < 6; p++) {
			if(p == 3) {
				xpos -= buttonSizeX;
				ypos += buttonSizeY;
			} else if(p != 0) {
				panButton[1][p] = new JButton(new ImageIcon("src/main/java/Yootgame/img/circle.jpg"));
				panButton[1][p] = createBoardBtn(panButton[1][p], xpos, ypos, buttonSizeX, buttonSizeY, false);
				boardPanel.add(panButton[1][p]);
				panButton[1][p].addActionListener(play);
				xpos -= buttonSizeX;
				ypos += buttonSizeY;
			}
		}

		// 오른쪽 대각선
		xpos = 240;
		ypos = 145;
		for(int p = 0; p < 6; p++) {
			if(p > 0) {
				if(p == 3) {
					panButton[2][p] = new JButton(new ImageIcon("src/main/java/Yootgame/img/bigcircle.jpg"));
					JButton arrow = new JButton(new ImageIcon("src/main/java/Yootgame/img/leftup.jpg"));
					arrow = createBoardBtn(arrow, xpos-buttonSizeX, ypos, buttonSizeX/2, buttonSizeY/2, false);
					boardPanel.add(arrow);
				} else {
					panButton[2][p] = new JButton(new ImageIcon("src/main/java/Yootgame/img/circle.jpg"));
				}
				panButton[2][p] = createBoardBtn(panButton[2][p], xpos, ypos, buttonSizeX, buttonSizeY, false);
				boardPanel.add(panButton[2][p]);
				panButton[2][p].addActionListener(play);
				xpos += buttonSizeX;
				ypos += buttonSizeY;
			}
		}
	}

	public void printPiece(int player, int posx, int posy, int num) {
		if(posx==0 && posy==0) {
			posx=0;
			posy=20;
		} else if(posx==0 && posy==-1) {
			posx=0;
			posy=19;
		} else if(posx==1 && posy==3) {
			posx=2;
			posy=3;
		} else if(posy>20) {
			player = 9;
		}

		if(player == 0) {
			printBtn("red",posx,posy,num);
		} else if(player == 1) {
			printBtn("blue",posx,posy,num);
		} else if(player == 2) {
			printBtn("green",posx,posy,num);
		} else if(player == 3) {
			printBtn("yellow",posx,posy,num);
		} else if(player == 4) {
			printBtn("circle",posx,posy,0);
		}

		client.sendMessage("/move_piece " + player + " " + posx + " " + posy + " " + num);
	}

	private void printBtn(String color, int posx, int posy, int num) {
		String url="";
		if(num != 0) {
			if((posx==0 && posy==5) || posy==10 || posy == 15 || (posx==2 && posy==3)) {
				url = "src/main/java/Yootgame/img/big"+color+num+".jpg";
			} else if(posy==20) {
				url = "src/main/java/Yootgame/img/start"+color+num+".jpg";
			} else {
				url = "src/main/java/Yootgame/img/"+color+num+".jpg";
			}
		} else {
			if((posx==0 && posy==5) || posy==10 || posy == 15 || (posx==2 && posy==3)) {
				url = "src/main/java/Yootgame/img/bigcircle.jpg";
			} else if(posy==20) {
				url = "src/main/java/Yootgame/img/startcircle.jpg";
			} else {
				url = "src/main/java/Yootgame/img/circle.jpg";
			}
		}
		panButton[posx][posy].setIcon(new ImageIcon(url));
	}

	public void printResult(int i) {
		infoPanel.updateYotResult(i);
		client.sendMessage("/yut_result " + i);
	}

	// 로그 처리 최적화
	private final StringBuilder logBuilder = new StringBuilder();
	public void message(String s) {
		if (logArea != null) {
			logBuilder.setLength(0);
			logBuilder.append(s).append("\n");
			logArea.append(logBuilder.toString());
			logArea.setCaretPosition(logArea.getDocument().getLength());
		}
	}
	// 이미지 캐싱
	private static final Map<String, ImageIcon> imageCache = new HashMap<>();
	private ImageIcon getImageIcon(String path) {
		return imageCache.computeIfAbsent(path, ImageIcon::new);
	}

	public void changePlayer(String nickname, boolean isHost) {
		String playerType = isHost ? "레드팀" : "블루팀";
		infoPanel.updateTurn(nickname, playerType, isHost);
	}

	public void setplayerInfo(int i, String s) {
		boolean isHost = (i == 0);
		try {
			String[] parts = s.split(" ");
			String countStr = parts[0].split(":")[1];
			int count = Integer.parseInt(countStr);
			infoPanel.updatePieceCount(isHost, count);
		} catch (Exception e) {
			System.err.println("Error parsing player info: " + s);
		}
	}

	public void buttonColor(String s) {
		switch (s) {
			case "throwBtnOFF":
				infoPanel.getThrowButton().setEnabled(false);
				infoPanel.getThrowButton().setBackground(new Color(153,204,255));
				break;
			case "throwBtnON":
				infoPanel.getThrowButton().setEnabled(true);
				infoPanel.getThrowButton().setBackground(new Color(255,255,0));
				break;
			case "newPieceBtnON":
				infoPanel.getNewPieceButton().setEnabled(true);
				infoPanel.getNewPieceButton().setBackground(new Color(255,255,0));
				break;
			case "newPieceBtnOFF":
				infoPanel.getNewPieceButton().setEnabled(false);
				infoPanel.getNewPieceButton().setBackground(new Color(153,204,255));
				break;
		}
	}

	public void refreashFrame() {
		infoPanel.updateYotResult(0);
	}

	public void finishMessage(int winner) {
		// infoPanel.addGameLog("Player " + winner + " is win");  // 이 줄을 제거
		message("Player " + winner + " is win");  // YotBoard의 message 메소드 사용
	}

	public JButton getThrowButton() {
		return infoPanel.getThrowButton();
	}

	public JButton getNewPieceButton() {
		return infoPanel.getNewPieceButton();
	}

	public JButton[] getTestButtons() {
		return infoPanel.getTestButtons();
	}

	public JPanel getPanelPan() {
		return panelPan;
	}
}