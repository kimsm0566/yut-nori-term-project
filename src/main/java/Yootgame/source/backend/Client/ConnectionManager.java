package Yootgame.source.backend.Client;

import java.io.*;
import java.net.Socket;

public class ConnectionManager {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private BufferedReader serverInput;
    private PrintWriter serverOutput;

    public void connect() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        serverOutput = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + SERVER_PORT);
    }

    public void disconnect() {
        try {
            if (serverInput != null) serverInput.close();
            if (serverOutput != null) serverOutput.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Connection to server closed.");
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (serverOutput != null) {
            serverOutput.println(message);
        }
    }

    public String readMessage() throws IOException {
        if (serverInput != null) {
            return serverInput.readLine();
        }
        return null;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }
}