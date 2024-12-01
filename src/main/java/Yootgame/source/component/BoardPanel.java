package Yootgame.source.component;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private JButton[][] boardButtons;
    private JLabel line;
    private int windowSizeX = 1000;
    private int windowSizeY = 700;
    private int buttonSizeX = windowSizeX/20;
    private int buttonSizeY = buttonSizeX;

    public BoardPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        initializeBoard();
    }

    private void initializeBoard() {
        boardButtons = new JButton[3][21];





        createBoard();

        // 선 이미지 추가
        line = new JLabel(new ImageIcon("src/main/java/Yootgame/img/line.png"));
        line.setBounds(84, 60, 430, 430);
        add(line);

        // 배경 이미지 설정
        ImageIcon backgroundImage = new ImageIcon("src/main/java/Yootgame/img/backgroundFicture.png");
        Image scaledImage = backgroundImage.getImage().getScaledInstance((int)(windowSizeX * 0.7), windowSizeY, Image.SCALE_SMOOTH);
        JLabel backgroundLabel = new JLabel(new ImageIcon(scaledImage));
        backgroundLabel.setBounds(0, 0, (int)(windowSizeX * 0.7), windowSizeY);
        add(backgroundLabel);
    }

    private void createBoard() {
        int startX = 80;
        int startY = 60;
        double buttonInterval = buttonSizeX * 1.25;

        // 메인 경로
        int xpos = startX + (buttonSizeX * 7);
        int ypos = startY + (buttonSizeY * 7);

        // 외곽 경로 버튼 생성
        for(int i = 1; i < 21; i++) {
            if(i < 6) ypos -= buttonInterval;
            else if(i < 11) xpos -= buttonInterval;
            else if(i < 16) ypos += buttonInterval;
            else xpos += buttonInterval;

            if(i == 5 || i == 10 || i == 15) {
                boardButtons[0][i] = createBoardButton("src/main/java/Yootgame/img/bigcircle.jpg", xpos, ypos);
            } else if(i == 20) {
                boardButtons[0][i] = createBoardButton("src/main/java/Yootgame/img/startcircle.jpg", xpos, ypos);
            } else {
                boardButtons[0][i] = createBoardButton("src/main/java/Yootgame/img/circle.jpg", xpos, ypos);
            }
            add(boardButtons[0][i]);
        }

        // 왼쪽 대각선 경로
        xpos = startX + (buttonSizeX * 7) - 10;
        ypos = startY + buttonSizeY - 10;
        for(int p = 0; p < 6; p++) {
            if(p != 0 && p != 3) {
                boardButtons[1][p] = createBoardButton("src/main/java/Yootgame/img/circle.jpg", xpos, ypos);
                add(boardButtons[1][p]);
            }
            xpos -= buttonSizeX;
            ypos += buttonSizeY;
        }

        // 오른쪽 대각선 경로
        xpos = startX + buttonSizeX - 10;
        ypos = startY + buttonSizeY - 10;
        for(int p = 0; p < 6; p++) {
            if(p != 0) {
                String imagePath = (p == 3) ? "src/main/java/Yootgame/img/bigcircle.jpg" : "src/main/java/Yootgame/img/circle.jpg";
                boardButtons[2][p] = createBoardButton(imagePath, xpos, ypos);
                add(boardButtons[2][p]);
            }
            xpos += buttonSizeX;
            ypos += buttonSizeY;
        }
    }

    private JButton createBoardButton(String imagePath, int x, int y) {
        JButton btn = new JButton(new ImageIcon(imagePath));
        btn.setSize(buttonSizeX, buttonSizeY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setLocation(x, y);
        return btn;
    }

    public JButton[][] getBoardButtons() {
        return boardButtons;
    }

    public void updatePiecePosition(int x, int y, String color, int num) {
        String imagePath;
        if(num != 0) {
            if((x == 0 && y == 5) || y == 10 || y == 15) {
                imagePath = "src/main/java/Yootgame/img/big" + color + num + ".jpg";
            } else {
                imagePath = "src/main/java/Yootgame/img/" + color + num + ".jpg";
            }
        } else {
            if((x == 0 && y == 5) || y == 10 || y == 15) {
                imagePath = "src/main/java/Yootgame/img/bigcircle.jpg";
            } else {
                imagePath = "src/main/java/Yootgame/img/circle.jpg";
            }
        }
        boardButtons[x][y].setIcon(new ImageIcon(imagePath));
    }
}