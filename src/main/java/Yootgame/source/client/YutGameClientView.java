package Yootgame.source.client;
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class YutGameClientView extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 900;
    private JPanel contentPane;
    private JTextField txtInput;
    private String UserName;
    private JButton btnSend;
    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    private Socket socket; // 연결소켓
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private JLabel lblUserName;
    // private JTextArea textArea;
    private JTextPane textArea;

    private Frame frame;
    private FileDialog fd;
    private JButton imgBtn;
    private JButton btnGameStart;
    private JButton btnRollYut;

    private final Image mainBackground = new ImageIcon("src/main/java/Yootgame/source/client/src/main/java/Yootgame/source/client/images/bg_main.jpg").getImage();
    private final Image yutBackground = new ImageIcon("src/main/java/Yootgame/source/client/images/bg_yut.png").getImage();
    private final Image charBackgraoud = new ImageIcon("src/main/java/Yootgame/source/client/images/bg_character.png").getImage();
    private final Image background = new ImageIcon("src/main/java/Yootgame/source/client/images/bg_gamepanel.png").getImage();
    private final ImageIcon imgGameSmallSpot = new ImageIcon("src/main/java/Yootgame/source/client/images/ic_game_small_spot.png");
    private final ImageIcon imgGameBigSpot = new ImageIcon("src/main/java/Yootgame/source/client/images/ic_game_big_spot.png");
    private final ImageIcon character1 = new ImageIcon("src/main/java/Yootgame/source/client/images/character1.png");
    private final ImageIcon character2 = new ImageIcon("src/main/java/Yootgame/source/client/images/character2.png");
    private final ImageIcon character3 = new ImageIcon("src/main/java/Yootgame/source/client/images/character3.png");
    private final ImageIcon character4 = new ImageIcon("src/main/java/Yootgame/source/client/images/character4.png");

    private final ImageIcon gameStartImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_game_start.png");
    private final ImageIcon gameStartHoverImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_game_start_hover.png");
    private final ImageIcon gameReadyImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_game_ready.png");
    private final ImageIcon gameReadyHoverImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_game_ready_hover.png");
    private final ImageIcon gameReadyFinishImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_game_ready_finish.png");
    private final ImageIcon gameReadyFinishHoverImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_game_ready_finish_hover.png");
    private final ImageIcon userReadyImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_user_ready.png");
    private final ImageIcon rollImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_roll.png");
    private final ImageIcon rollHoverImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_roll_hover.png");
    private final ImageIcon yutFrontImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_yut_front.png");
    private final ImageIcon yutBackImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_yut_back.png");
    private final ImageIcon yutSpecialImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_yut_special.png");
    private final ImageIcon crownImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_crown.png");
    private final ImageIcon arrowImage = new ImageIcon("src/main/java/Yootgame/source/client/images/img_arrow.png");

    private ImageIcon gameSmallSpot = new ImageIcon(
            imgGameSmallSpot.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
    private ImageIcon gameBigSpot = new ImageIcon(
            imgGameBigSpot.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));

    private ImageIcon img_gameStart = new ImageIcon(
            gameStartImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
    private ImageIcon img_gameStartHover = new ImageIcon(
            gameStartHoverImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
    private ImageIcon img_gameReady = new ImageIcon(
            gameReadyImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
    private ImageIcon img_gameReadyHover = new ImageIcon(
            gameReadyHoverImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
    private ImageIcon img_gameReadyFinish = new ImageIcon(
            gameReadyFinishImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
    private ImageIcon img_gameReadyFinishHover = new ImageIcon(
            gameReadyFinishHoverImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
    private ImageIcon img_userReady = new ImageIcon(
            userReadyImage.getImage().getScaledInstance(135, 30, Image.SCALE_SMOOTH));

    private ImageIcon img_crown = new ImageIcon(crownImage.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
    private ImageIcon img_arrow = new ImageIcon(arrowImage.getImage().getScaledInstance(45, 25, Image.SCALE_SMOOTH));

    private ImageIcon img_roll = new ImageIcon(rollImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
    private ImageIcon img_rollHover = new ImageIcon(
            rollHoverImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
    private ImageIcon img_yutFront = new ImageIcon(
            yutFrontImage.getImage().getScaledInstance(70, 280, Image.SCALE_SMOOTH));
    private ImageIcon img_yutBack = new ImageIcon(
            yutBackImage.getImage().getScaledInstance(70, 280, Image.SCALE_SMOOTH));
    private ImageIcon img_yutSpecial = new ImageIcon(
            yutSpecialImage.getImage().getScaledInstance(70, 280, Image.SCALE_SMOOTH));

    private JLabel[] gameSpot = new JLabel[29];

    private JPanel[] userPanel = new JPanel[4];
    private JLabel[] userImageLabel = new JLabel[4];
    private JLabel[] userObjectLabel = new JLabel[4];

    private JLabel[] userObjectCntLabel = new JLabel[4];
    private JLabel[] userNameText = new JLabel[4];
    private int[] userObjectCnt = new int[] { 4, 4, 4, 4 };
    private JLabel[] yutObjectLabel = new JLabel[4];
    private JLabel yutResultBgLabel = new JLabel();
    private JLabel yutResultTextLabel = new JLabel();
    private JLabel[] userReadyLabel = new JLabel[4];
    private JLabel[] crownLabel = new JLabel[4];

    private JLabel winLabel = new JLabel();
    private JLabel myTurnLabel = new JLabel();

    private int userIdx = 3;

    private UserData[] userList = new UserData[4];
    private boolean Owner = true;
    private boolean[] userReadyList = new boolean[4];
    private int rollAvailableCnt = 0;
    private String rollUserName = "";
    private boolean isPlaying = false;
    private int playTurnIdx = 0;
    private int restUserObjectCnt = 4;
    private int[] userMoveYutCase;
    private ArrayList<JLabel> tempArrowList = new ArrayList();
    private int[] userObjectPos = new int[] { -1, -1, -1, -1 };
    private JLabel clickObjectLabel;
    private String userClickObjectName = "";
    private List<JLabel> gameObjectList = new ArrayList();

    private int[][] spotPos = new int[][] { { 670, 450, 60, 60, 0 }, { 670, 350, 60, 60, 0 }, { 670, 250, 60, 60, 0 },
            { 670, 150, 60, 60, 0 }, { 660, 30, 80, 80, 1 }, { 540, 40, 60, 60, 0 }, { 420, 40, 60, 60, 0 },
            { 300, 40, 60, 60, 0 }, { 180, 40, 60, 60, 0 }, { 40, 30, 80, 80, 1 }, { 50, 150, 60, 60, 0 },
            { 50, 250, 60, 60, 0 }, { 50, 350, 60, 60, 0 }, { 50, 450, 60, 60, 0 }, { 40, 550, 80, 80, 1 },
            { 180, 560, 60, 60, 0 }, { 300, 560, 60, 60, 0 }, { 420, 560, 60, 60, 0 }, { 540, 560, 60, 60, 0 },
            { 660, 550, 80, 80, 1 }, { 170, 140, 60, 60, 0 }, { 260, 210, 60, 60, 0 }, { 350, 280, 80, 80, 1 },
            { 460, 370, 60, 60, 0 }, { 550, 450, 60, 60, 0 }, { 550, 140, 60, 60, 0 }, { 460, 210, 60, 60, 0 },
            { 260, 370, 60, 60, 0 }, { 170, 450, 60, 60, 0 }, { 740, 600, 80, 80, 1 }};

    private JLabel[] arrowLabel = new JLabel[spotPos.length];

    /**
     * Create the frame.
     */
    public YutGameClientView(String username, String ip_addr, String port_no) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, SCREEN_WIDTH, SCREEN_HEIGHT);
        contentPane = new JPanel() {
            public void paintComponent(Graphics g) {
                g.drawImage(mainBackground, 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        for (int i = 0; i < arrowLabel.length; i++) {
            arrowLabel[i] = new JLabel();
            arrowLabel[i].setIcon(img_arrow);
            arrowLabel[i].setName("arrow " + i);
            arrowLabel[i].setVisible(false);
            if (spotPos[i][4] == 1)
                arrowLabel[i].setBounds(spotPos[i][0] + 17, spotPos[i][1] - 25, 45, 25);
            else
                arrowLabel[i].setBounds(spotPos[i][0] + 8, spotPos[i][1] - 25, 45, 25);
            arrowLabel[i].addMouseListener(new MyMouseAdapter());
            contentPane.add(arrowLabel[i]);
        }

        for (int i = 0; i < 29; i++) {
            gameSpot[i] = new JLabel();
            gameSpot[i].setBounds(spotPos[i][0], spotPos[i][1], spotPos[i][2], spotPos[i][3]);
            if (spotPos[i][4] == 1)
                gameSpot[i].setIcon(gameBigSpot);
            else
                gameSpot[i].setIcon(gameSmallSpot);
            gameSpot[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { // 마우스로 클릭했을 때

                }
            });
            contentPane.add(gameSpot[i]);
        }

        JPanel panel = new JPanel() {
            public void paintComponent(Graphics g) {
                g.drawImage(background, 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        panel.setBounds(24, 10, 780, 650);
        contentPane.add(panel);

        for (int i = 0; i < 4; i++) {
            userReadyLabel[i] = new JLabel();
            userReadyLabel[i].setIcon(img_userReady);
            if (i == 0) {
                userReadyLabel[i].setBounds(24, 800, 135, 30);
            } else if (i == 1) {
                userReadyLabel[i].setBounds(243, 800, 135, 30);
            } else if (i == 2) {
                userReadyLabel[i].setBounds(457, 800, 135, 30);
            } else {
                userReadyLabel[i].setBounds(669, 800, 135, 30);
            }
            userReadyLabel[i].setVisible(userReadyList[i]);
            contentPane.add(userReadyLabel[i]);
        }

        for (int i = 0; i < 4; i++) {
            crownLabel[i] = new JLabel();
            crownLabel[i].setIcon(img_crown);
            if (i == 0) {
                crownLabel[i].setBounds(115, 810, 40, 40);
            } else if (i == 1) {
                crownLabel[i].setBounds(334, 810, 40, 40);
            } else if (i == 2) {
                crownLabel[i].setBounds(548, 810, 40, 40);
            } else {
                crownLabel[i].setBounds(760, 810, 40, 40);
            }
            crownLabel[i].setVisible(true);
            contentPane.add(crownLabel[i]);
        }

        for (int i = 0; i < 4; i++) {
            userList[i] = new UserData(-1, "");
            userPanel[i] = new JPanel() {
                public void paintComponent(Graphics g) {
                    g.drawImage(charBackgraoud, 0, 0, null);
                    setOpaque(false);
                    super.paintComponent(g);
                }
            };
            userImageLabel[i] = new JLabel();
            userNameText[i] = new JLabel();
            userObjectCntLabel[i] = new JLabel();
            for (int j = 0; j < 4; j++)
                userObjectLabel[i] = new JLabel();
            if (i == 0) {
                userPanel[i].setBounds(24, 680, 135, 169);
                userImageLabel[i].setBounds(28, 720, 80, 125);
                userImageLabel[i]
                        .setIcon(new ImageIcon(character1.getImage().getScaledInstance(80, 125, Image.SCALE_SMOOTH)));
                userNameText[i].setBounds(30, 685, 135, 20);
                userNameText[i].setText("Username1");
                userObjectLabel[i].setBounds(115, 705, 20, 30);
                userObjectLabel[i]
                        .setIcon(new ImageIcon(character1.getImage().getScaledInstance(20, 30, Image.SCALE_SMOOTH)));
                userObjectCntLabel[i].setBounds(140, 710, 20, 25);
                userObjectCntLabel[i].setFont(new Font("bold", Font.PLAIN, 30));
            } else if (i == 1) {
                userPanel[i].setBounds(243, 680, 135, 169);
                userImageLabel[i].setBounds(248, 720, 80, 125);
                userImageLabel[i]
                        .setIcon(new ImageIcon(character2.getImage().getScaledInstance(80, 125, Image.SCALE_SMOOTH)));
                userNameText[i].setBounds(254, 685, 135, 20);
                userNameText[i].setText("Username2");
                userObjectLabel[i].setBounds(334, 705, 20, 30);
                userObjectLabel[i]
                        .setIcon(new ImageIcon(character2.getImage().getScaledInstance(20, 30, Image.SCALE_SMOOTH)));
                userObjectCntLabel[i].setBounds(359, 710, 20, 25);
                userObjectCntLabel[i].setFont(new Font("bold", Font.PLAIN, 30));
            } else if (i == 2) {
                userPanel[i].setBounds(458, 680, 135, 169);
                userImageLabel[i].setBounds(462, 720, 80, 125);
                userImageLabel[i]
                        .setIcon(new ImageIcon(character3.getImage().getScaledInstance(80, 125, Image.SCALE_SMOOTH)));
                userNameText[i].setBounds(464, 685, 135, 20);
                userNameText[i].setText("Username3");
                userObjectLabel[i].setBounds(549, 705, 20, 30);
                userObjectLabel[i]
                        .setIcon(new ImageIcon(character3.getImage().getScaledInstance(20, 30, Image.SCALE_SMOOTH)));
                userObjectCntLabel[i].setBounds(574, 710, 20, 25);
                userObjectCntLabel[i].setFont(new Font("bold", Font.PLAIN, 30));
            } else {
                userPanel[i].setBounds(669, 680, 135, 169);
                userImageLabel[i].setBounds(674, 720, 80, 125);
                userImageLabel[i]
                        .setIcon(new ImageIcon(character4.getImage().getScaledInstance(80, 125, Image.SCALE_SMOOTH)));
                userNameText[i].setBounds(676, 685, 135, 20);
                userNameText[i].setText("Username4");
                userObjectLabel[i].setBounds(760, 705, 20, 30);
                userObjectLabel[i]
                        .setIcon(new ImageIcon(character4.getImage().getScaledInstance(20, 30, Image.SCALE_SMOOTH)));
                userObjectCntLabel[i].setBounds(785, 710, 20, 25);
                userObjectCntLabel[i].setFont(new Font("bold", Font.PLAIN, 30));
            }
            userObjectCntLabel[i].setText(Integer.toString(userObjectCnt[i]));
            userObjectLabel[i].setName("userObjectStart " + i);
            userObjectLabel[i].addMouseListener(new MyMouseAdapter());

            userNameText[i].setVisible(false);
            userImageLabel[i].setVisible(false);
            userObjectLabel[i].setVisible(false);
            userObjectCntLabel[i].setVisible(false);
            contentPane.add(userObjectCntLabel[i]);
            contentPane.add(userObjectLabel[i]);
            contentPane.add(userImageLabel[i]);
            contentPane.add(userNameText[i]);
            contentPane.add(userPanel[i]);
        }

        /*
         * userGameObject[objectIdx].addMouseListener(new MouseAdapter() {
         *
         * @Override public void mouseClicked(MouseEvent e) { // 마우스로 클릭했을 때
         * System.out.println(((JLabel) e.getSource()).getName()); } });
         */
        for (int i = 0; i < 4; i++) {
            yutObjectLabel[i] = new JLabel();
            yutObjectLabel[i].setBounds(840 + 100 * i, 60, 70, 280);
            yutObjectLabel[i].setIcon(img_yutBack);
            contentPane.add(yutObjectLabel[i]);
        }

        ImageIcon test = new ImageIcon(
                imgGameBigSpot.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH));
        yutResultBgLabel.setBounds(880, 100, 300, 200);
        yutResultBgLabel.setIcon(test);
        yutResultBgLabel.setVisible(false);
        contentPane.add(yutResultBgLabel);
        contentPane.setComponentZOrder(yutResultBgLabel, 2);

        yutResultTextLabel.setBounds(1000, 100, 300, 200);
        yutResultTextLabel.setText("걸");
        yutResultTextLabel.setFont(new Font("Serif", Font.BOLD, 50));
        yutResultTextLabel.setVisible(false);
        contentPane.add(yutResultTextLabel);
        contentPane.setComponentZOrder(yutResultTextLabel, 1);

        winLabel.setBounds(950, 100, 300, 200);
        winLabel.setText("승리!!");
        winLabel.setFont(new Font("Serif", Font.BOLD, 50));
        winLabel.setVisible(false);
        contentPane.add(winLabel);
        contentPane.setComponentZOrder(winLabel, 1);

        myTurnLabel.setBounds(932, 100, 400, 200);
        myTurnLabel.setText("윷을 굴려주세요");
        myTurnLabel.setFont(new Font("Serif", Font.BOLD, 25));
        myTurnLabel.setVisible(false);
        contentPane.add(myTurnLabel);
        contentPane.setComponentZOrder(myTurnLabel, 1);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(828, 538, 424, 181);
        contentPane.add(scrollPane);

        textArea = new JTextPane();
        scrollPane.setViewportView(textArea);
        textArea.setEditable(true);
        textArea.setFont(new Font("굴림체", Font.PLAIN, 14));



        lblUserName = new JLabel("Name");
        lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
        lblUserName.setBackground(Color.WHITE);
        lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
        lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserName.setBounds(828, 778, 62, 40);
        contentPane.add(lblUserName);
        setVisible(true);

        // AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
        UserName = username;
        lblUserName.setText(username);

        JButton btnNewButton = new JButton("종 료");
        btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
                SendChatMsg(msg);
                System.exit(0);
            }
        });
        btnNewButton.setBounds(1183, 778, 69, 40);
        contentPane.add(btnNewButton);

        JPanel yutPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                g.drawImage(yutBackground, 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        yutPanel.setBounds(829, 22, 423, 364);
        contentPane.add(yutPanel);

        if (Owner) {
            btnGameStart = new JButton(img_gameStart);
        } else if (!userReadyList[userIdx]) {
            btnGameStart = new JButton(img_gameReady);
        } else {
            btnGameStart = new JButton(img_gameReadyFinish);
        }

        btnGameStart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { // 마우스가 올라갔을 때
                if (Owner) {
                    btnGameStart.setIcon(img_gameStartHover);
                } else if (!userReadyList[userIdx]) {
                    btnGameStart.setIcon(img_gameReadyHover);
                } else {
                    btnGameStart.setIcon(img_gameReadyFinishHover);
                }
                btnGameStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) { // 마우스가 눌렸을 때
                if (Owner) {
                    btnGameStart.setIcon(img_gameStartHover);
                } else if (!userReadyList[userIdx]) {
                    btnGameStart.setIcon(img_gameReadyHover);
                } else {
                    btnGameStart.setIcon(img_gameReadyFinishHover);
                }
                btnGameStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) { // 마우스로 클릭했을 때
                System.out.println(userIdx + " Owner: " + Owner);
                if (Owner) {
                    // gamestart
                    ChatMsg obcm = new ChatMsg(UserName, "104", userIdx + " IGameStart"); // 로그인
                    SendChatMsg(obcm);
                } else {
                    ChatMsg obcm = new ChatMsg(UserName, "103", userIdx + " readyChange"); // 로그인
                    SendChatMsg(obcm);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) { // 마우스가 나갔을 때 원상복귀
                if (Owner) {
                    btnGameStart.setIcon(img_gameStart);
                } else if (!userReadyList[userIdx]) {
                    btnGameStart.setIcon(img_gameReady);
                } else {
                    btnGameStart.setIcon(img_gameReadyFinish);
                }
                btnGameStart.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        btnGameStart.setBounds(890, 400, 280, 70);
        btnGameStart.setBorderPainted(false); // 버튼 테두리 설정
        btnGameStart.setContentAreaFilled(false); // 버튼 영역 배경 표시 설정
        btnGameStart.setFocusPainted(false); // 포커스 표시 설정
        btnGameStart.setVisible(true);
        contentPane.add(btnGameStart);

        btnRollYut = new JButton(img_roll);
        btnRollYut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { // 마우스가 올라갔을 때
                btnRollYut.setIcon(img_rollHover);
                btnRollYut.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) { // 마우스가 눌렸을 때
                btnRollYut.setIcon(img_rollHover);
                btnRollYut.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) { // 마우스로 클릭했을 때
                if (userIdx != playTurnIdx) {
                    AppendText("아직 플레이어님의 턴이 아닙니다");
                } else {
                    if (rollAvailableCnt != 0) {
                        rollAvailableCnt -= 1;
                        ChatMsg obcm = new ChatMsg(UserName, "501", "rollBtnClick");
                        SendChatMsg(obcm);
                        repaint();
                    } else {
                        AppendText("잠시만 기다려주세요~");
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) { // 마우스가 나갔을 때 원상복귀
                btnRollYut.setIcon(img_roll);
                btnRollYut.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        btnRollYut.setBounds(890, 400, 280, 70);
        btnRollYut.setBorderPainted(false); // 버튼 테두리 설정
        btnRollYut.setContentAreaFilled(false); // 버튼 영역 배경 표시 설정
        btnRollYut.setFocusPainted(false); // 포커스 표시 설정
        btnRollYut.setVisible(false);
        contentPane.add(btnRollYut);

        // 자동진행 + rollback
        /*
         * JCheckBox autoPlayCheck = new JCheckBox("Auto Play");
         * autoPlayCheck.setBounds(828, 487, 115, 23); contentPane.add(autoPlayCheck);
         *
         * JButton btnRollBack = new JButton("RollBack"); btnRollBack.setBounds(1115,
         * 487, 97, 23); contentPane.add(btnRollBack);
         */

        try {
            socket = new Socket(ip_addr, Integer.parseInt(port_no));

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            ChatMsg obcm = new ChatMsg(UserName, "100", "Hello"); // 로그인
            SendChatMsg(obcm);

            ListenNetwork net = new ListenNetwork();
            net.start();
            TextSendAction action = new TextSendAction();
            btnSend.addActionListener(action);
            txtInput.addActionListener(action);
            txtInput.requestFocus();
            ImageSendAction action2 = new ImageSendAction();
            imgBtn.addActionListener(action2);

        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AppendText("connect error");
        }
    }

    public ChatMsg ReadChatMsg() {
        Object obj = null;
        String msg = null;
        ChatMsg cm = new ChatMsg("", "", "");
        // Android와 호환성을 위해 각각의 Field를 따로따로 읽는다.

        try {
            obj = ois.readObject();
            cm.code = (String) obj;
            obj = ois.readObject();
            cm.UserName = (String) obj;
            obj = ois.readObject();
            cm.data = (String) obj;
            if (cm.code.equals("300")) {
                obj = ois.readObject();
                cm.imgbytes = (byte[]) obj;
            }
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            AppendText("ReadChatMsg Error");
            e.printStackTrace();
            try {
                oos.close();
                socket.close();
                ois.close();
                socket = null;
                return null;
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                try {
                    oos.close();
                    socket.close();
                    ois.close();
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                socket = null;
                return null;
            }

            // textArea.append("메세지 송신 에러!!\n");
            // System.exit(0);
        }
        return cm;
    }

    // Server Message를 수신해서 화면에 표시
    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                ChatMsg cm = ReadChatMsg();
                if (cm == null) {
                    System.out.println("cm이 null");
                    break;
                }
                if (socket == null) {
                    System.out.println("socket이 null");
                    break;
                }
                switch (cm.code) {
                    case "100":
                        System.out.println(cm.code + " " + cm.UserName + " " + cm.data);
                        break;
                    case "101":
                        userIdx = Integer.parseInt(cm.data);
                        repaint();
                        break;
                    case "102":
                        for (int i = 0; i < 4; i++) {
                            userNameText[i].setVisible(false);
                            userImageLabel[i].setVisible(false);
                            userObjectLabel[i].setVisible(false);
                            userObjectCntLabel[i].setVisible(false);
                            userReadyLabel[i].setVisible(false);
                            crownLabel[i].setVisible(false);
                        }
                        Owner = false;

                        String[] userInfoData = cm.data.split(" ");
                        for (int i = 0; i < userInfoData.length; i += 4) {
                            int idx = Integer.parseInt(userInfoData[i]);
                            String name = userInfoData[i + 1];
                            boolean isOwner = Boolean.parseBoolean(userInfoData[i + 2]);
                            boolean isReady = Boolean.parseBoolean(userInfoData[i + 3]);
                            System.out.println("isOwner: " + isOwner);
                            userList[idx].setIdx(idx);
                            userList[idx].setUsername(name);
                            userNameText[idx].setText(name);
                            userNameText[idx].setVisible(true);
                            userImageLabel[idx].setVisible(true);
                            userObjectLabel[idx].setVisible(true);
                            userObjectCntLabel[idx].setVisible(true);
                            userReadyLabel[idx].setVisible(isReady);
                            crownLabel[idx].setVisible(isOwner);
                            if (userIdx == idx && isOwner)
                                Owner = true;
                        }

                        if (Owner) {
                            btnGameStart.setIcon(img_gameStart);
                        } else if (!userReadyList[userIdx]) {
                            btnGameStart.setIcon(img_gameReady);
                        } else {
                            btnGameStart.setIcon(img_gameReadyFinish);
                        }
                        repaint();
                        break;
                    case "105":
                        String[] serverGameStartResponse = cm.data.split(" ");
                        boolean serverGameStartAccept = Boolean.parseBoolean(serverGameStartResponse[0]);
                        if (serverGameStartAccept) {
                            AppendText("☆☆☆☆☆ 게임을 시작합니다 ☆☆☆☆☆");
                            btnGameStart.setVisible(false);
                            btnRollYut.setVisible(true);
                            for (int i = 0; i < 4; i++)
                                userReadyLabel[i].setVisible(false);

                        } else {
                            if (serverGameStartResponse[1].equals("NoUser"))
                                AppendText("아직 유저가 아무도 없습니다.");
                            else
                                AppendText("모든 유저들이 아직 준비를 하지 않았습니다");
                        }
                        break;
                    case "200": // chat message
                        String msg;
                        msg = String.format("[%s] %s", cm.UserName, cm.data);
                        System.out.println("msg: " + msg);
                        AppendText(msg);
                        break;
                    case "300": // Image 첨부
                        AppendText("[" + cm.UserName + "]" + " " + cm.data);
                        // AppendImage(cm.img);
                        AppendImageBytes(cm.imgbytes);
                        break;
                    case "400":
                        System.out.println(cm.code + " " + cm.UserName + " " + cm.data);
                        break;
                    case "500":
                        isPlaying = true;
                        System.out.println("500 턴체인지");
                        rollAvailableCnt = 1;
                        String[] userTurn = cm.data.split(" ");
                        System.out.println(userTurn[0] + "이 윷을 던짐");
                        rollUserName = userTurn[0];
                        playTurnIdx = Integer.parseInt(userTurn[1]);
                        AppendText("================================");
                        AppendText(userTurn[0] + "의 차례입니다!!!!");
                        if(playTurnIdx == userIdx) {
                            try {
                                myTurnLabel.setVisible(true);
                                yutResultBgLabel.setVisible(true);
                                sleep(1000);
                                myTurnLabel.setVisible(false);
                                yutResultBgLabel.setVisible(false);
                            }catch(InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        repaint();
                        break;
                    case "501":
                        System.out.println("501 들어옴");

                        //윷이벤트
                        Random random = new Random();
                        random.setSeed(System.currentTimeMillis());
                        int special = random.nextInt(100);
                        int specialPos = random.nextInt(4);

                        try {
                            for(int i=0; i<4; i++) {
                                int[] yutList = new int[4];
                                // yutList -> 1 = 앞면 / 0 = 뒷면
                                for (int j = 0; j < 4; j++) {
                                    int num = random.nextInt(2);
                                    if (num == 0)
                                        yutList[j] = 1;
                                    else
                                        yutList[j] = 0;
                                }

                                boolean isHasBack = false;
                                if (special < 25 && yutList[specialPos] == 0) {
                                    yutList[specialPos] = -1;
                                    isHasBack = true;
                                }

                                for (int j = 0; j < 4; j++) {
                                    if (yutList[j] == 1) {
                                        yutObjectLabel[j].setIcon(img_yutFront);
                                    } else if (yutList[j] == 0) {
                                        yutObjectLabel[j].setIcon(img_yutBack);
                                    } else {
                                        yutObjectLabel[j].setIcon(img_yutSpecial);
                                    }
                                }
                                sleep(500);
                            }
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        //제대로된 윷
                        String[] yutRollResult = cm.data.split(" ");

                        for (int i = 0; i < 4; i++) {
                            int yut = Integer.parseInt(yutRollResult[i]);
                            if (yut == 1) {
                                yutObjectLabel[i].setIcon(img_yutFront);
                            } else if (yut == 0) {
                                yutObjectLabel[i].setIcon(img_yutBack);
                            } else {
                                yutObjectLabel[i].setIcon(img_yutSpecial);
                            }
                        }

                        try {
                            sleep(1000);
                        }catch(InterruptedException e) {
                            e.printStackTrace();
                        }

                        int yutRollValue = Integer.parseInt(yutRollResult[4]);
                        if (yutRollValue == -1) {
                            yutResultTextLabel.setText("빽도");
                            AppendText(rollUserName + "=>---빽도---");
                        }else if (yutRollValue == 1) {
                            yutResultTextLabel.setText("도");
                            AppendText(rollUserName + "=>◇도◇");
                        }else if (yutRollValue == 2) {
                            yutResultTextLabel.setText("개");
                            AppendText(rollUserName + "=>◇◇개◇◇");
                        }else if (yutRollValue == 3) {
                            yutResultTextLabel.setText("걸");
                            AppendText(rollUserName + "=>◇◇◇걸◇◇◇");
                        }else if (yutRollValue == 4) {
                            yutResultTextLabel.setText("윷");
                            AppendText(rollUserName + "=>◇◇◇◇윷◇◇◇◇");
                        }else if (yutRollValue == 5) {
                            yutResultTextLabel.setText("모");
                            AppendText(rollUserName + "=>◇◇◇◇◇모◇◇◇◇◇");
                        }

                        try {
                            yutResultBgLabel.setVisible(true);
                            yutResultTextLabel.setVisible(true);
                            int time = 0;
                            while(time<3) {
                                time+=1;
                                sleep(500);
                            }
                            yutResultBgLabel.setVisible(false);
                            yutResultTextLabel.setVisible(false);
                        }catch (InterruptedException e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }

                        repaint();
                        break;
                    case "502":
                        System.out.println("502 들어옴");
                        try {
                            myTurnLabel.setVisible(true);
                            yutResultBgLabel.setVisible(true);
                            sleep(1000);
                            myTurnLabel.setVisible(false);
                            yutResultBgLabel.setVisible(false);
                        }catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                        rollAvailableCnt += 1;
                        break;
                    case "503":
                        System.out.println("503 들어옴");
                        System.out.println("캐릭터 선택하면 됨");

                        String[] rollResult = cm.data.split(" ");
                        userMoveYutCase = new int[rollResult.length];
                        for (int i = 0; i < userMoveYutCase.length; i++) {
                            userMoveYutCase[i] = Integer.parseInt(rollResult[i]);
                        }
                        break;
                    case "504":
                        System.out.println("504 들어옴 data: " + cm.data);
                        for (int i = 0; i < gameObjectList.size(); i++)
                            contentPane.remove(gameObjectList.get(i));

                        gameObjectList.clear();

                        System.out.println("504들어옴 " + cm.data);
                        String[] moveResult = cm.data.split(" ");
                        for (int i = 0; i < moveResult.length; i++)
                            System.out.println(moveResult[i]);
                        System.out.println("moveResult 출력 끝");

                        gameObjectList.clear();
                        int index = 0;
                        int userMoveIdx = 0;
                        while (index < moveResult.length) {
                            if (moveResult[index].equals("user")) {
                                userMoveIdx = Integer.parseInt(moveResult[index + 1]);
                            } else {
                                int objectIdx = Integer.parseInt(moveResult[index]);
                                int pos = Integer.parseInt(moveResult[index + 1]);
                                JLabel objectLabel = new JLabel();
                                if (userMoveIdx == 0)
                                    objectLabel.setIcon(new ImageIcon(
                                            character1.getImage().getScaledInstance(35, 50, Image.SCALE_SMOOTH)));
                                else if (userMoveIdx == 1)
                                    objectLabel.setIcon(new ImageIcon(
                                            character2.getImage().getScaledInstance(35, 50, Image.SCALE_SMOOTH)));
                                else if (userMoveIdx == 2)
                                    objectLabel.setIcon(new ImageIcon(
                                            character3.getImage().getScaledInstance(35, 50, Image.SCALE_SMOOTH)));
                                else if (userMoveIdx == 3)
                                    objectLabel.setIcon(new ImageIcon(
                                            character4.getImage().getScaledInstance(35, 50, Image.SCALE_SMOOTH)));
                                objectLabel.addMouseListener(new MyMouseAdapter());
                                objectLabel.setName("object " + userMoveIdx + " " + objectIdx);
                                if (userIdx == userMoveIdx)
                                    userObjectPos[objectIdx] = pos;
                                if (spotPos[pos][4] == 1)
                                    objectLabel.setBounds(spotPos[pos][0] + 21, spotPos[pos][1] + 12, 35, 50);
                                else
                                    objectLabel.setBounds(spotPos[pos][0] + 12, spotPos[pos][1], 35, 50);
                                contentPane.add(objectLabel);
                                contentPane.setComponentZOrder(objectLabel, 2);
                                gameObjectList.add(objectLabel);
                            }
                            index += 2;
                        }
                        repaint();
                        break;
                    case "505":
                        System.out.println("505 들어옴 data: " + cm.data);
                        String[] restObjectCntData = cm.data.split(" ");
                        for (int i = 0; i < restObjectCntData.length; i++) {
                            userObjectCnt[i] = Integer.parseInt(restObjectCntData[i]);
                            userObjectCntLabel[i].setText(Integer.toString(userObjectCnt[i]));
                            repaint();
                        }
                        break;
                    case "507":
                        System.out.println("507 data: " + cm.data);
                        if(cm.data.equals("win")) {
                            winLabel.setText("승리!!");
                        }else {
                            winLabel.setText("패배..");
                        }
                        yutResultBgLabel.setVisible(true);
                        winLabel.setVisible(true);
                        repaint();
                        break;
                }
            }
        }
    }

    class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) { // 마우스로 클릭했을 때
            if (isPlaying) {
                System.out.println(((JLabel) e.getSource()).getName());
                System.out.println("playTurnIdx: " + playTurnIdx + " userIdx: " + userIdx);
                if (userIdx == playTurnIdx) {
                    if (rollAvailableCnt != 0) {
                        AppendText("먼저 윷을 굴려주세요!");
                    } else {
                        System.out.println("선택가능");
//                  ChatMsg obcm = new ChatMsg(UserName, "504", "finish");
//                  SendChatMsg(obcm);
                        JLabel clickLabel = (JLabel) e.getSource();
                        String labelName = clickLabel.getName();
                        if (labelName.equals("userObjectStart " + playTurnIdx)) {
                            clickObjectLabel = clickLabel;
                            userClickObjectName = "new Object";

                            if (userObjectCnt[userIdx] == 0)
                                AppendText("더이상 말을 추가할 수 없습니다.");
                            else {
                                tempArrowList.clear();
                                for (int i = 0; i < userMoveYutCase.length; i++) {
                                    if(userMoveYutCase[i] - 1 == -1 && userObjectCnt[playTurnIdx] == 4) continue;
                                    tempArrowList.add(arrowLabel[userMoveYutCase[i] - 1]);
                                    arrowLabel[userMoveYutCase[i] - 1].setVisible(true);
                                }
                                repaint();
                            }
                        } else if (labelName.contains("object")) {
                            System.out.println("오브젝트 클릭");
                            clickObjectLabel = clickLabel;
                            userClickObjectName = "object";
                            String[] labelSplit = labelName.split(" ");

                            tempArrowList.clear();
                            if (Integer.parseInt(labelSplit[1]) == userIdx) {
                                int objectPos = userObjectPos[Integer.parseInt(labelSplit[2])];
                                for (int i = 0; i < userMoveYutCase.length; i++) {
                                    int arrowIdx = 0;
                                    if (userMoveYutCase[i] == -1){
                                        if (objectPos == 0)
                                            arrowIdx = 19;
                                        else if (objectPos == 25)
                                            arrowIdx = 4;
                                        else if (objectPos == 20)
                                            arrowIdx = 9;
                                        else
                                            arrowIdx = objectPos - 1;
                                    } else { // 도착 시 처리 19 처리해야함
                                        int moveDist = userMoveYutCase[i] - 1;
                                        arrowIdx= getArrowIdx(objectPos, moveDist);
                                    }
                                    arrowLabel[arrowIdx].setVisible(true);
                                    tempArrowList.add(arrowLabel[arrowIdx]);
                                }
                                repaint();
                            }

                        } else if (labelName.contains("arrow")) {
                            System.out.println("arrow 클릭");
                            System.out.println("ClickLabelObjectName: " + clickObjectLabel.getName());
                            int useYutCaseIdx = 0;
                            for(int j=0; j<tempArrowList.size(); j++) {
                                if(tempArrowList.get(j).getName().equals(labelName)){
                                    useYutCaseIdx = j;
                                    break;
                                }
                            }

                            String[] labelSplit = labelName.split(" ");
                            int arrowPos = Integer.parseInt(labelSplit[1]);
                            String arrowMsg = "";
                            if (userClickObjectName.equals("new Object")) {
                                System.out.println("새로운 오브젝트");
                                arrowMsg = "new object " + arrowPos + " " + useYutCaseIdx;
                            } else if (userClickObjectName.equals("object")) {
                                System.out.println("clickLabelName: " + clickObjectLabel.getName());
                                arrowMsg = "move " + clickObjectLabel.getName() + " " + arrowPos + " " + useYutCaseIdx;
                            }
                            System.out.println(arrowMsg);
                            ChatMsg obcm = new ChatMsg(UserName, "504", arrowMsg);
                            SendChatMsg(obcm);
                            for (int i = 0; i < arrowLabel.length; i++)
                                arrowLabel[i].setVisible(false);
                            tempArrowList.clear();
                            userMoveYutCase = null;
                            repaint();
                        }
                    }

                }
            }
        }
    };

    public int getArrowIdx(int objectPos, int moveDist) {
        int arrowIdx = 0;
        if (objectPos == 4) {
            if (moveDist == 2) arrowIdx = 22;
            else if(moveDist == 0 || moveDist == 1) arrowIdx = 25 + moveDist;
            else arrowIdx = 25 + moveDist - 1;
        } else if (objectPos == 9) {
            arrowIdx = 20 + moveDist;
        } else if (objectPos >= 15 && objectPos <= 19) {
            if (objectPos + moveDist + 1 > 19)
                arrowIdx = 29; // 도착 시 처리
            else arrowIdx = objectPos + moveDist + 1;
        } else if (objectPos >= 20 && objectPos <= 24) {
            if (objectPos + moveDist + 1 > 24) arrowIdx = 29;  // 도착 시 처리
            else arrowIdx = objectPos + moveDist + 1;
        } else if (objectPos >= 25 && objectPos <= 26) {
            if(moveDist == 1 && objectPos == 25) arrowIdx = 22;
            else if(moveDist == 0 && objectPos == 26) arrowIdx = 22;
            else if(moveDist + objectPos <= 28) arrowIdx = moveDist + objectPos;
            else arrowIdx = 14 + (29 - moveDist - objectPos);
        }else if(objectPos >= 27 && objectPos <= 28) {
            if(objectPos == 27 && moveDist == 0) arrowIdx = 28;
            else arrowIdx = 14 + 28 - objectPos - moveDist;
        }else {
            arrowIdx = objectPos + moveDist + 1;
        }
        return arrowIdx;
    }

    // keyboard enter key 치면 서버로 전송
    class TextSendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Send button을 누르거나 메시지 입력하고 Enter key 치면
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = null;
                // msg = String.format("[%s] %s\n", UserName, txtInput.getText());
                msg = txtInput.getText();
                SendMessage(msg);
                txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
                txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
                if (msg.contains("/exit")) // 종료 처리
                    System.exit(0);
            }
        }
    }

    class ImageSendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
            if (e.getSource() == imgBtn) {
                frame = new Frame("이미지첨부");
                fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
                fd.setVisible(true);
                // System.out.println(fd.getDirectory() + fd.getFile());
                if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
                    ChatMsg obcm = new ChatMsg(UserName, "300", fd.getFile());
//               ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
//               obcm.img = img;
//               SendChatMsg(obcm);

                    BufferedImage bImage = null;
                    String filename = fd.getDirectory() + fd.getFile();
                    try {
                        bImage = ImageIO.read(new File(filename));
                    } catch (IOException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(bImage, "jpg", bos);
                        byte[] data = bos.toByteArray();
                        bos.close();
                        obcm.imgbytes = data;
                        // AppendImageBytes(data);

                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    SendChatMsg(obcm);
                }
            }
        }
    }

    ImageIcon icon1 = new ImageIcon("src/icon1.jpg");

    public void AppendIcon(ImageIcon icon) {
        int len = textArea.getDocument().getLength();
        // 끝으로 이동
        textArea.setCaretPosition(len);
        textArea.insertIcon(icon);
    }

    // 화면에 출력
    public void AppendText(String msg) {
        // textArea.append(msg + "\n");
        // AppendIcon(icon1);
        msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
        int len = textArea.getDocument().getLength();
        // 끝으로 이동
        textArea.setCaretPosition(len);
        textArea.replaceSelection(msg + "\n");
    }

    public void AppendImage(ImageIcon ori_icon) {
        int len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len); // place caret at the end (with no selection)
        Image ori_img = ori_icon.getImage();
        int width, height;
        double ratio;
        width = ori_icon.getIconWidth();
        height = ori_icon.getIconHeight();
        // Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
        if (width > 200 || height > 200) {
            if (width > height) { // 가로 사진
                ratio = (double) height / width;
                width = 200;
                height = (int) (width * ratio);
            } else { // 세로 사진
                ratio = (double) width / height;
                height = 200;
                width = (int) (height * ratio);
            }
            Image new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon new_icon = new ImageIcon(new_img);
            textArea.insertIcon(new_icon);

        } else
            textArea.insertIcon(ori_icon);
        len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len);
        textArea.replaceSelection("\n");
    }

    public void AppendImageBytes(byte[] imgbytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(imgbytes);
        BufferedImage ori_img = null;
        try {
            ori_img = ImageIO.read(bis);
            bis.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ImageIcon new_icon = new ImageIcon(ori_img);
        AppendImage(new_icon);
    }

    // Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
    public byte[] MakePacket(String msg) {
        byte[] packet = new byte[BUF_LEN];
        byte[] bb = null;
        int i;
        for (i = 0; i < BUF_LEN; i++)
            packet[i] = 0;
        try {
            bb = msg.getBytes("euc-kr");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
        for (i = 0; i < bb.length; i++)
            packet[i] = bb[i];
        return packet;
    }

    // Server에게 network으로 전송
    public void SendMessage(String msg) {
        ChatMsg obcm = new ChatMsg(UserName, "200", msg);
        SendChatMsg(obcm);
    }

    // 하나의 Message 보내는 함수
    // Android와 호환성을 위해 code, UserName, data 모드 각각 전송한다.
    public void SendChatMsg(ChatMsg obj) {
        try {
            oos.writeObject(obj.code);
            oos.writeObject(obj.UserName);
            oos.writeObject(obj.data);
            if (obj.code.equals("300")) { // 이미지 첨부 있는 경우
                oos.writeObject(obj.imgbytes);
            }
            oos.flush();
        } catch (IOException e) {
            AppendText("SendChatMsg Error");
            e.printStackTrace();
            try {
                oos.close();
                socket.close();
                ois.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            // textArea.append("메세지 송신 에러!!\n");
            // System.exit(0);
        }
    }

}