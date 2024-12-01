package Yootgame.source.client;

import java.io.Serializable;

public class ChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    public String code;
    public String UserName;
    public String data;
    public byte[] imgbytes;

    public ChatMsg(String userName, String code, String msg) {
        this.code = code;
        this.UserName = userName;
        this.data = msg;
        }
}