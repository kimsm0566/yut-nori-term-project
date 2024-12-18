package Yootgame.source.server;//JavaObjServer.java ObjectStream 기반 채팅 Server


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.*;

public class YutGameServer extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    JTextArea textArea;
    private JTextField txtPortNumber;

    private ServerSocket socket; // 서버소켓
    private Socket client_socket; // accept() 에서 생성된 client 소켓
    private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    private boolean[] userConnect = new boolean[4];
    private int playTurnIdx = 0;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    YutGameServer frame = new YutGameServer();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public YutGameServer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 338, 440);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 300, 298);
        contentPane.add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        JLabel lblNewLabel = new JLabel("Port Number");
        lblNewLabel.setBounds(13, 318, 87, 26);
        contentPane.add(lblNewLabel);

        txtPortNumber = new JTextField();
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setText("30000");
        txtPortNumber.setBounds(112, 318, 199, 26);
        contentPane.add(txtPortNumber);
        txtPortNumber.setColumns(10);

        JButton btnServerStart = new JButton("Server Start");
        btnServerStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
                } catch (NumberFormatException | IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                AppendText("Chat Server Running..");
                btnServerStart.setText("Chat Server Running..");
                btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
                txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다

                AcceptServer accept_server = new AcceptServer();
                accept_server.start();
            }
        });
        btnServerStart.setBounds(12, 356, 300, 35);
        contentPane.add(btnServerStart);
    }

    class AcceptServer extends Thread {
        @SuppressWarnings("unchecked")
        public void run() {
            while (true) { // 사용자 접속을 계속해서 받기 위해 while문
                try {
                    AppendText("Waiting new clients ...");
                    client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중

                    // 최대 인원(4명) 체크
                    if (UserVec.size() >= 4) {
                        AppendText("방이 가득 찼습니다.");
                        ObjectOutputStream tempOos = new ObjectOutputStream(client_socket.getOutputStream());
                        ChatMsg msg = new ChatMsg("SERVER", "999", "Room is full");
                        tempOos.writeObject(msg.code);
                        tempOos.writeObject(msg.UserName);
                        tempOos.writeObject(msg.data);
                        tempOos.flush();
                        tempOos.close();
                        client_socket.close();
                        break;
                    }

                    AppendText("새로운 참가자 from " + client_socket);
                    // User 당 하나씩 Thread 생성
                    UserService new_user = new UserService(client_socket);
                    UserVec.add(new_user); // 새로운 참가자 배열에 추가
                    new_user.start(); // 만든 객체의 스레드 실행
                    AppendText("현재 참가자 수 " + UserVec.size());
                    System.out.println("현재 참가자 수 출력: " + UserVec.size());
                } catch (IOException e) {
                    AppendText("accept() error");
                }
            }
        }
    }

    public void AppendText(String str) {
        // textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    public void AppendObject(ChatMsg msg) {
        // textArea.append("사용자로부터 들어온 object : " + str+"\n");
        textArea.append("code = " + msg.code + "\n");
        textArea.append("id = " + msg.UserName + "\n");
        textArea.append("data = " + msg.data + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    // User 당 생성되는 Thread
    // Read One 에서 대기 -> Write All
    class UserService extends Thread {
        private InputStream is;
        private OutputStream os;
        private DataInputStream dis;
        private DataOutputStream dos;

        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        private Socket client_socket;
        private Vector user_vc;
        public String UserName = "";
        public String imagepath = "";
        public int userIdx = -1;
        public int[] userGameObjectPos = new int[] { -1, -1, -1, -1 };
        public int[] overlapGameObjectIdx = new int[] { -1, -1, -1, -1 };
        public int restObjectCnt = 4;
        public boolean isOwner = false;
        public boolean isReady = false;
        public boolean isGiveUp = false;
        private List<Integer> rollResultList = new ArrayList();

        public UserService(Socket client_socket) {
            // TODO Auto-generated constructor stub
            // 매개변수로 넘어온 자료 저장
            this.client_socket = client_socket;
            this.user_vc = UserVec;
            try {
                oos = new ObjectOutputStream(client_socket.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(client_socket.getInputStream());
            } catch (Exception e) {
                AppendText("userService error");
            }
        }

        public void Login() {
            int index = 0;
            while (index < 4) {
                if (!userConnect[index])
                    break;
                index += 1;
            }
            System.out.println("index: " + index);
            if (index != 4) {
                if (UserVec.size() == 1)
                    this.isOwner = true;
                System.out.println("if문 안으로 들어옴");
                userConnect[index] = true;
                userIdx = index;
                AppendText("새로운 참가자 " + UserName + " 입장.");
                WriteOne(UserName + "님 환영합니다!\n"); // 연결된 사용자에게 정상접속을 알림
                SendUserIdx();
                String msg = "[" + UserName + "]님이 입장 하였습니다.\n";
                WriteOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.
                SendUserInfo();
            }
        }

        public void Logout() {
            String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
            userConnect[this.userIdx] = false;
            boolean isLogoutUserHaveOwner = this.isOwner;
            UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
            WriteAll(msg); // 나를 제외한 다른 User들에게 전송
            this.client_socket = null;
            AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
            if (UserVec.size() != 0 && isLogoutUserHaveOwner) {
                UserService user = (UserService) UserVec.elementAt(0);
                user.isOwner = true;
            }
            SendUserInfo();
        }

        // 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
        public void WriteAll(String str) {
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                user.WriteOne(str);
            }
        }

        // 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
        public void WriteAllObject(ChatMsg obj) {
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                user.WriteChatMsg(obj);
            }
        }

        // 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
        public void WriteOthers(String str) {
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                if (user != this)
                    user.WriteOne(str);
            }
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
            }
            for (i = 0; i < bb.length; i++)
                packet[i] = bb[i];
            return packet;
        }

        public void SendUserIdx() {
            ChatMsg obcm = new ChatMsg("SERVER", "101", Integer.toString(userIdx));
            WriteChatMsg(obcm);
        }

        public void SendUserInfo() {
            System.out.println("SendUserIfno" + UserVec.size());
            StringBuilder data = new StringBuilder("");
            for (int i = 0; i < UserVec.size(); i++) {
                UserService user = (UserService) UserVec.elementAt(i);
                data.append(user.userIdx).append(' ').append(user.UserName).append(' ').append(user.isOwner).append(' ')
                        .append(user.isReady).append(' ');
            }

            for (int i = 0; i < UserVec.size(); i++) {
                UserService user = (UserService) UserVec.elementAt(i);
                ChatMsg obcm = new ChatMsg("SERVER", "102", data.toString());
                user.WriteChatMsg(obcm);
            }
        }

        // UserService Thread가 담당하는 Client 에게 1:1 전송
        public void WriteOne(String msg) {
            ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
            System.out.println("obcm: " + obcm.data);
            WriteChatMsg(obcm);
        }

        // 귓속말 전송
        public void WritePrivate(String msg) {
            ChatMsg obcm = new ChatMsg("귓속말", "200", msg);
            WriteChatMsg(obcm);
        }

        //
        public void WriteChatMsg(ChatMsg obj) {
            try {
                oos.writeObject(obj.code);
                oos.writeObject(obj.UserName);
                oos.writeObject(obj.data);
//             if (obj.code.equals("300")) {
//                oos.writeObject(obj.imgbytes);
//                //oos.writeObject(obj.bimg);
//             }
            } catch (IOException e) {
                AppendText("oos.writeObject(ob) error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Logout();

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
//               obj = ois.readObject();
//               cm.bimg = (BufferedImage) obj;
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                Logout();
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Logout();
                return null;
            }
            return cm;
        }

        public void run() {
            while (true) { // 사용자 접속을 계속해서 받기 위해 while문
                ChatMsg cm = null;
                if (client_socket == null)
                    break;
                cm = ReadChatMsg();
                if (cm == null)
                    break;
                if (cm.code.length() == 0)
                    break;
                AppendObject(cm);
                if (cm.code.matches("100")) {
                    UserName = cm.UserName;
                    System.out.println("100들어옴");
                    Login();
                } else if (cm.code.matches("103")) {
                    this.isReady = !this.isReady;
                    SendUserInfo();
                } else if (cm.code.matches("104")) {
                    int readyCnt = 0;
                    for (int i = 0; i < UserVec.size(); i++) {
                        UserService user = (UserService) UserVec.elementAt(i);
                        if (!user.isOwner && user.isReady)
                            readyCnt += 1;
                    }
                    ChatMsg obcm = null;
                    if (UserVec.size() == 1) {
                        obcm = new ChatMsg("SERVER", "105", "false NoUser");
                    } else if (readyCnt == UserVec.size() - 1) {
                        obcm = new ChatMsg("SERVER", "105", "true");
                    } else {
                        obcm = new ChatMsg("SERVER", "105", "false NoReady");
                    }

                    // 유저에 따라 다 보내줘야함.
                    for (int i = 0; i < UserVec.size(); i++) {
                        UserService user = (UserService) UserVec.elementAt(i);
                        user.WriteChatMsg(obcm);
                    }

                    if (readyCnt == UserVec.size() - 1) {
                        obcm = new ChatMsg("SERVER", "500", cm.UserName + " " + playTurnIdx + " true");
                        for (int i = 0; i < UserVec.size(); i++) {
                            UserService user = (UserService) UserVec.elementAt(i);
                            user.WriteChatMsg(obcm);
                        }
                    }

                } else if (cm.code.matches("200")) {
                    String msg = String.format("[%s] %s", cm.UserName, cm.data);
                    AppendText(msg); // server 화면에 출력
                    String[] args = msg.split(" "); // 단어들을 분리한다.
                    if (args.length == 1) { // Enter key 만 들어온 경우 Wakeup 처리만 한다.
                        // UserStatus = "O";
                    } else if (args[1].matches("/exit")) {
                        Logout();
                        break;
                    } else if (args[1].matches("/list")) {
                        WriteOne("User list\n");
                        WriteOne("Name\tStatus\n");
                        WriteOne("-----------------------------\n");
                        for (int i = 0; i < user_vc.size(); i++) {
                            UserService user = (UserService) user_vc.elementAt(i);
                            WriteOne(user.UserName + "\n");
                        }
                        WriteOne("-----------------------------\n");
                    } else if (args[1].matches("/to")) { // 귓속말
                        for (int i = 0; i < user_vc.size(); i++) {
                            UserService user = (UserService) user_vc.elementAt(i);
                            if (user.UserName.matches(args[2])) {
                                String msg2 = "";
                                for (int j = 3; j < args.length; j++) {// 실제 message 부분
                                    msg2 += args[j];
                                    if (j < args.length - 1)
                                        msg2 += " ";
                                }
                                // /to 빼고.. [귓속말] [user1] Hello user2..
                                user.WritePrivate(args[0] + " " + msg2 + "\n");
                                // user.WriteOne("[귓속말] " + args[0] + " " + msg2 + "\n");
                                break;
                            }
                        }
                    } else { // 일반 채팅 메시지
                        // WriteAll(msg + "\n"); // Write All
                        WriteAllObject(cm);
                    }
                } else if (cm.code.matches("400")) { // logout message 처리
                    Logout();
                    break;
                } else if (cm.code.matches("300")) {
                    WriteAllObject(cm);
                } else if (cm.code.matches("501")) {
                    Random random = new Random();
                    random.setSeed(System.currentTimeMillis());
                    int special = random.nextInt(100);
                    int specialPos = random.nextInt(4);

                    int[] yutList = new int[4];
                    // yutList -> 1 = 앞면 / 0 = 뒷면
                    for (int i = 0; i < 4; i++) {
                        int num = random.nextInt(2);
                        if (num == 0)
                            yutList[i] = 1;
                        else
                            yutList[i] = 0;
                    }

                    boolean isHasBack = false;
                    if (special < 25 && yutList[specialPos] == 0) {
                        yutList[specialPos] = -1;
                        isHasBack = true;
                    }

                    int yutCnt = 0;
                    for (int i = 0; i < 4; i++) {
                        if (yutList[i] != 1) {
                            yutCnt += 1;
                        }
                    }

                    int yutRollValue = 1;
                    switch (yutCnt) {
                        case 1:
                            if (isHasBack)
                                yutRollValue = -1;
                            else
                                yutRollValue = 1;
                            break;
                        case 2:
                            yutRollValue = 2;
                            break;
                        case 3:
                            yutRollValue = 3;
                            break;
                        case 4:
                            yutRollValue = 4;
                            break;
                        case 0:
                            yutRollValue = 5;
                            break;
                    }

                    rollResultList.add(yutRollValue);
                    Collections.sort(rollResultList);

                    StringBuilder yutRollResult = new StringBuilder("");
                    for (int i = 0; i < 4; i++) {
                        if (yutList[i] != 1) {
                            if (yutList[i] == -1) {
                                yutRollResult.append(-1).append(' ');
                            } else {
                                yutRollResult.append(0).append(' ');
                            }
                        } else {
                            yutRollResult.append(1).append(' ');
                        }
                    }

                    System.out.println("yutroll Server" + cm.data + yutRollValue);
                    ChatMsg obcm = new ChatMsg("SERVER", "501", yutRollResult.toString() + yutRollValue);
                    for (int i = 0; i < UserVec.size(); i++) {
                        UserService user = (UserService) UserVec.elementAt(i);
                        user.WriteChatMsg(obcm);
                    }

                    if (yutRollValue == 4 || yutRollValue == 5) {
                        obcm = new ChatMsg("SERVER", "502", "roll again");
                        for (int i = 0; i < UserVec.size(); i++) {
                            UserService user = (UserService) UserVec.elementAt(i);
                            user.WriteChatMsg(obcm);
                        }
                    } else {
                        StringBuilder sb = new StringBuilder("");
                        for (int i = 0; i < rollResultList.size(); i++)
                            sb.append(rollResultList.get(i)).append(' ');
                        obcm = new ChatMsg("SERVER", "503", sb.toString());
                        for (int i = 0; i < UserVec.size(); i++) {
                            UserService user = (UserService) UserVec.elementAt(i);
                            user.WriteChatMsg(obcm);
                        }
                    }
                } else if (cm.code.matches("504")) {
                    AppendText("504> 화살표 클릭 data: " + cm.data);

                    System.out.println("504 data: " + cm.data);
                    // 움직인 말 처리

                    int arrowpos = -1;
                    int objectIdx = -1;
                    int moveDist = 0;
                    boolean isArival = false;
                    if (cm.data.contains("new")) {
                        this.restObjectCnt -= 1;
                        String[] arrowResult = cm.data.split(" ");
                        arrowpos = Integer.parseInt(arrowResult[2]);
                        moveDist = rollResultList.get(Integer.parseInt(arrowResult[3]));
                        rollResultList.remove(Integer.parseInt(arrowResult[3]));
                        for(int i=0; i<4; i++) {
                            if(userGameObjectPos[i] == -1 && overlapGameObjectIdx[i] == -1) {
                                objectIdx = i;
                                break;
                            }
                        }
                    } else {
                        String[] arrowResult = cm.data.split(" "); // move object useridx objectIdx arrpos removeIdx
                        objectIdx = Integer.parseInt(arrowResult[3]);
                        arrowpos = Integer.parseInt(arrowResult[4]);
                        if (arrowpos == 29) isArival = true;
                        moveDist = rollResultList.get(Integer.parseInt(arrowResult[5]));
                        rollResultList.remove(Integer.parseInt(arrowResult[5]));
                    }

                    int move = 0;
                    try {
                        while(move < moveDist-1) {
                            int arrowIdx = getArrowIdx(userGameObjectPos[objectIdx], 0);
                            AppendText("오브젝트 이동: " + arrowIdx);
                            userGameObjectPos[objectIdx] = arrowIdx;
                            sendObjectInfo();
                            move+=1;
                            sleep(500);
                        }
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }


                    if (isArival) {
                        // 도착하면 무조건 move object이기에 objectIdx가 있다.
                        userGameObjectPos[objectIdx] = -1;
                        for (int i = 0; i < 4; i++) {
                            if (overlapGameObjectIdx[i] == objectIdx) {
                                overlapGameObjectIdx[i] = -1;
                                userGameObjectPos[i] = -1;
                            }
                        }
                        sendObjectInfo();

                        // 턴 변경 로직 추가
                        this.rollResultList.clear();
                        playTurnIdx += 1;
                        int turn = playTurnIdx % UserVec.size();

                        // 기권한 플레이어 건너뛰기
                        while (true) {
                            UserService nextUser = (UserService) UserVec.elementAt(turn);
                            if (!nextUser.isGiveUp) break;
                            playTurnIdx += 1;
                            turn = playTurnIdx % UserVec.size();
                        }

                        String nextUserName = "";
                        for (int i = 0; i < UserVec.size(); i++) {
                            UserService user = (UserService) UserVec.elementAt(i);
                            if (user.userIdx == turn) {
                                nextUserName = user.UserName;
                                break;
                            }
                        }

                        ChatMsg obcm = new ChatMsg("SERVER", "500", nextUserName + " " + turn + " true");
                        WriteAllObject(obcm);
                        return; // 도착 후 추가 이동 방지
                    }
                    else {
                        int overlapIdx = -1;
                        boolean isOverlap = false;
                        for (int i = 0; i < 4; i++) {
                            if (userGameObjectPos[i] == arrowpos) {
                                isOverlap = true;
                                overlapIdx = i;
                                break;
                            }
                        }

                        if (cm.data.contains("new")) {
                            if (!isOverlap)
                                userGameObjectPos[objectIdx] = arrowpos;
                            else
                                overlapGameObjectIdx[objectIdx] = overlapIdx;
                        } else {
                            if (isOverlap) {
                                userGameObjectPos[objectIdx] = -1;
                                overlapGameObjectIdx[objectIdx] = overlapIdx;
                            } else {
                                userGameObjectPos[objectIdx] = arrowpos;
                            }
                        }

                        boolean isRemoveOtherObject = false;

                        if (!isOverlap) {
                            AppendText("중복아니라서 remove체크");
                            for (int i = 0; i < UserVec.size(); i++) {
                                UserService user = (UserService) UserVec.elementAt(i);
                                for (int j = 0; j < user.userGameObjectPos.length; j++) {
                                    if (user == this)
                                        continue;

                                    if (user.userGameObjectPos[j] == arrowpos) {
                                        user.userGameObjectPos[j] = -1;

                                        int addRestCnt = 1;
                                        for (int k = 0; k < user.overlapGameObjectIdx.length; k++) {
                                            if (user.overlapGameObjectIdx[k] == j) {
                                                user.overlapGameObjectIdx[k] = -1;
                                                addRestCnt += 1;
                                            }
                                        }
                                        AppendText("유저가 잡은 object개수: " + addRestCnt);
                                        user.restObjectCnt += addRestCnt;
                                        isRemoveOtherObject = true;
                                        break;
                                    }
                                }
                            }
                        }

                        sendObjectInfo();

                        StringBuilder userRestObjectCnt = new StringBuilder("");
                        for (int i = 0; i < UserVec.size(); i++) {
                            UserService user = (UserService) UserVec.elementAt(i);
                            userRestObjectCnt.append(user.restObjectCnt).append(' ');
                        }

                        ChatMsg obcm = new ChatMsg("SERVER", "505", userRestObjectCnt.toString());
                        for (int i = 0; i < UserVec.size(); i++) {
                            UserService user = (UserService) UserVec.elementAt(i);
                            user.WriteChatMsg(obcm);
                        }

                        if (isRemoveOtherObject) {
                            obcm = new ChatMsg("SERVER", "502", "roll again");
                            for (int i = 0; i < UserVec.size(); i++) {
                                UserService user = (UserService) UserVec.elementAt(i);
                                user.WriteChatMsg(obcm);
                            }
                        } else if (rollResultList.size() != 0) {
                            StringBuilder sb = new StringBuilder("");
                            for (int i = 0; i < rollResultList.size(); i++)
                                sb.append(rollResultList.get(i)).append(' ');
                            obcm = new ChatMsg("SERVER", "503", sb.toString());
                            for (int i = 0; i < UserVec.size(); i++) {
                                UserService user = (UserService) UserVec.elementAt(i);
                                user.WriteChatMsg(obcm);
                            }
                        } else {
                            this.rollResultList.clear();
                            playTurnIdx += 1;
                            int turn = playTurnIdx % UserVec.size();

                            // 기권한 플레이어 건너뛰기
                            while (true) {
                                UserService nextUser = (UserService) UserVec.elementAt(turn);
                                if (!nextUser.isGiveUp) break;
                                playTurnIdx += 1;
                                turn = playTurnIdx % UserVec.size();
                            }

                            String nextUserName = "";
                            for (int i = 0; i < UserVec.size(); i++) {
                                UserService user = (UserService) UserVec.elementAt(i);
                                if (user.userIdx == turn) {
                                    nextUserName = user.UserName;
                                    break;
                                }
                            }

                            // true 추가하여 턴 변경 메시지 전송
                            obcm = new ChatMsg("SERVER", "500", nextUserName + " " + turn + " true");
                            WriteAllObject(obcm);
                        }
                    }
                }else if (cm.code.matches("506")) {
                    AppendText(UserName+"이 기권하였습니다.");
                    isGiveUp = true;

                    restObjectCnt = 4;
                    for(int i=0; i<4; i++) {
                        userGameObjectPos[i] = -1;
                        overlapGameObjectIdx[i] = -1;
                    }

                    sendObjectInfo();

                    //TODO 게임 리셋해야함
                    int giveupCnt = 0;
                    for (int i = 0; i < UserVec.size(); i++) {
                        UserService user = (UserService) UserVec.elementAt(i);
                        if(user.isGiveUp) giveupCnt +=1;
                    }

                    if(giveupCnt == UserVec.size()-1) {
                        for (int i = 0; i < UserVec.size(); i++) {
                            UserService user = (UserService) UserVec.elementAt(i);
                            if(!user.isGiveUp) {
                                System.out.println(user.userIdx + " " + user.UserName+"이 이김");
                                game_over("win", user);
                            }
                            else game_over("lose", user);
                        }
                    }
                } else if (cm.code.equals("999")) {  // 방 가득 참 처리
                    AppendText("방이 가득 찼습니다.");
                    try {
                        client_socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            } // while
        } // run

        public void game_over(String msg, UserService user) {
            ChatMsg obcm = new ChatMsg("SERVER", "507", msg);
            user.WriteChatMsg(obcm);

        }

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

        public void sendObjectInfo() {
            StringBuilder allObjectMsg = new StringBuilder("");
            for (int i = 0; i < UserVec.size(); i++) {
                UserService user = (UserService) UserVec.elementAt(i);
                allObjectMsg.append("user").append(' ').append(user.userIdx).append(' ');

                for (int j = 0; j < user.userGameObjectPos.length; j++) {
                    if (user.userGameObjectPos[j] != -1) {
                        allObjectMsg.append(j).append(' ').append(user.userGameObjectPos[j]).append(' ');
                    }
                }
            }

            System.out.println("504보내는 msg" + allObjectMsg.toString());
            ChatMsg obcm = new ChatMsg("SERVER", "504", allObjectMsg.toString());
            for (int i = 0; i < UserVec.size(); i++) {
                UserService user = (UserService) UserVec.elementAt(i);
                user.WriteChatMsg(obcm);
            }
        }
    }
}